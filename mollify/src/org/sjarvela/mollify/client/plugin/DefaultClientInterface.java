/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.event.DefaultEventDispatcher;
import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.filesystem.js.JsFilesystemItem;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.js.JsObjBuilder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.plugin.response.NativeResponseProcessor;
import org.sjarvela.mollify.client.plugin.service.NativeService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.request.ResponseInterceptor;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class DefaultClientInterface implements ClientInterface {
	private static Logger logger = Logger
			.getLogger(DefaultClientInterface.class.getName());

	private final EventDispatcher eventDispatcher;
	private final ResponseInterceptor responseInterceptor;
	// private final ItemContextHandler itemContextHandler;
	private final SessionProvider sessionProvider;
	private final ServiceProvider serviceProvider;
	private final DialogManager dialogManager;
	private final TextProvider textProvider;
	// private final FileListExt fileListInterface;
	// private final ViewManager viewManager;
	private final FileSystemActionHandler actionHandler;

	// private FileUploadDialogFactory uploader = null;
	private final NativeViewManager nativeViewManager;
	private final FileSystemItemProvider filesystemItemProvider;

	private FileSystemService fileSystemService;

	// TODO move this entire class into external js
	public DefaultClientInterface(EventDispatcher eventDispatcher,
			ResponseInterceptor responseInterceptor,
			SessionProvider sessionProvider, ServiceProvider serviceProvider,
			DialogManager dialogManager, TextProvider textProvider,
			ViewManager viewManager,
			FileSystemActionHandlerFactory actionHandlerFactory,
			FileSystemItemProvider filesystemItemProvider) {
		this.eventDispatcher = eventDispatcher;
		this.responseInterceptor = responseInterceptor;
		// this.itemContextHandler = itemContextProvider;
		this.sessionProvider = sessionProvider;
		this.serviceProvider = serviceProvider;
		this.dialogManager = dialogManager;
		this.textProvider = textProvider;
		this.filesystemItemProvider = filesystemItemProvider;
		fileSystemService = serviceProvider.getFileSystemService();
		// this.viewManager = viewManager;//
		// this.fileListInterface = new FileListExt(textProvider);
		this.actionHandler = actionHandlerFactory.create();

		this.nativeViewManager = new NativeViewManager(viewManager,
				dialogManager);
	}

	@Override
	public void setup(final SessionInfo session, final Callback onReady) {
		initApp(asJs(session.getPluginBaseUrl()), onReady);
	}

	// private Map<String, String> getExternalPluginScripts(SessionInfo session)
	// {
	// logger.log(Level.INFO, "Initializing client plugins from session");
	// JavaScriptObject pluginsObj = session.getPlugins();
	// if (pluginsObj == null)
	// return Collections.EMPTY_MAP;
	//
	// Map<String, String> result = new HashMap();
	// JsObj plugins = pluginsObj.cast();
	// for (String id : plugins.getKeys()) {
	// if (id == null || id.length() == 0 || id.startsWith("_"))
	// continue;
	// logger.log(Level.INFO, "Initializing client plugin " + id);
	// JsObj plugin = plugins.getJsObj(id).cast();
	//
	// if (plugin.hasValue("client_plugin"))
	// result.put(id, plugin.getString("client_plugin"));
	// }
	// return result;
	// }

	private native void initApp(JavaScriptObject i, Callback cb) /*-{
		if (!$wnd.mollify)
			return;
		$wnd.mollify.setup(i, function() {
			cb.@org.sjarvela.mollify.client.Callback::onCallback()();
		});
	}-*/;

	public void addResponseProcessor(JavaScriptObject rp) {
		responseInterceptor.addProcessor(new NativeResponseProcessor(rp));
	}

	public void addEventHandler(JavaScriptObject eh) {
		// TODO use proper interface here instead of casting
		((DefaultEventDispatcher) eventDispatcher).addEventHandler(eh);
	}

	protected JavaScriptObject getSession() {
		return new NativeSession(sessionProvider.getSession()).asJs();
	}

	private JavaScriptObject asJs(String pluginBaseUrl) {
		return createNativeInterface(this, pluginBaseUrl, getTextProvider(),
				getService(), getViewManager(), getLogger());
	}

	protected JavaScriptObject getService() {
		return new NativeService(serviceProvider.getExternalService()).asJs();
	};

	protected JavaScriptObject getViewManager() {
		return nativeViewManager.asJs();
	};

	protected JavaScriptObject getTextProvider() {
		return new NativeTextProvider(textProvider).asJs();
	};

	protected JavaScriptObject getLogger() {
		return new NativeLogger().asJs();
	};

	protected void getFolders(JsFolder parent, final JavaScriptObject cb) {
		if (parent == null) {
			invokeCb(cb,
					JsUtil.asJsArray(filesystemItemProvider.getRootFolders(),
							JsRootFolder.class));
			return;
		}
		filesystemItemProvider.getFolders(parent,
				new ResultListener<List<JsFolder>>() {
					@Override
					public void onSuccess(List<JsFolder> result) {
						invokeCb(cb, JsUtil.asJsArray(result, JsFolder.class));
					}

					@Override
					public void onFail(ServiceError error) {
						dialogManager.showError(error);
					}
				});
	};

	public void getItemDetails(final JsFilesystemItem item,
			final JavaScriptObject requestData, final JavaScriptObject callback) {
		fileSystemService.getItemDetails(
				item,
				requestData,
				createItemDetailsListener(item,
						new ResultCallback<ItemDetails>() {
							@Override
							public void onCallback(ItemDetails details) {
//								boolean root = !item.isFile()
//										&& ((JsFolder) item.cast()).isRoot();
//								boolean writable = !root
//										&& details.getFilePermission()
//												.canWrite();
//								List<JsObj> itemActions = getItemActions(item,
//										writable, root);
//								JsArray<JsObj> actions = JsUtil.asJsArray(
//										itemActions, JsObj.class);
								call(callback, details);
								// call2(callback, details, JsUtil.asJsArray(
								// itemActions, JsObj.class));
							}
						}));
	}

//	private List<JsObj> getItemActions(JsFilesystemItem item, boolean writable,
//			boolean root) {
//		List<JsObj> actions = new ArrayList();
//
//		// if (item.isFile() || !root)
//		// actions.add(createAction(item, FileSystemAction.download,
//		// Texts.fileActionDownloadTitle.name()));
//		//
//		// actions.add(createSeparator());
//		//
//		// if (writable)
//		// actions.add(createAction(item, FileSystemAction.rename,
//		// Texts.fileActionRenameTitle.name()));
//		// if (!root)
//		// actions.add(createAction(item, FileSystemAction.copy,
//		// Texts.fileActionCopyTitle.name()));
//		// if (item.isFile())
//		// actions.add(createAction(item, FileSystemAction.copyHere,
//		// Texts.fileActionCopyHereTitle.name()));
//		// if (writable)
//		// actions.add(createAction(item, FileSystemAction.move,
//		// Texts.fileActionMoveTitle.name()));
//		// if (writable)
//		// actions.add(createAction(item, FileSystemAction.delete,
//		// Texts.fileActionDeleteTitle.name()));
//		return actions;
//	}

	private JsObj createSeparator() {
		return new JsObjBuilder().string("type", "separator")
				.string("title", "-").create();
	}

	private JsObj createAction(final JsFilesystemItem item,
			final ResourceId action, String titleKey) {
		JavaScriptObject cb = JsUtil.createJsCallback(new Callback() {
			@Override
			public void onCallback() {
				if (action instanceof FileSystemAction)
					actionHandler.onAction(item, (FileSystemAction) action,
							null);
			}
		});
		return new JsObjBuilder().string("type", "action")
				.string("group", "core").string("id", action.name())
				.string("title-key", titleKey).obj("callback", cb).create();
	}

	private ResultListener<ItemDetails> createItemDetailsListener(
			final JsFilesystemItem item,
			final ResultCallback<ItemDetails> callback) {
		return new ResultListener<ItemDetails>() {
			public void onFail(ServiceError error) {
				if (error.getDetails() != null
						&& (error.getDetails().startsWith("PHP error #2048") || error
								.getDetails()
								.contains(
										"It is not safe to rely on the system's timezone settings"))) {
					dialogManager
							.showInfo("ERROR",
									"Mollify configuration error, PHP timezone information missing.");
					return;
				}
				dialogManager.showError(error);
			}

			public void onSuccess(ItemDetails details) {
				callback.onCallback(details);
			}
		};
	}

	protected native void call(JavaScriptObject callback, JavaScriptObject o) /*-{
		callback(o);
	}-*/;

	protected native void call2(JavaScriptObject callback, JavaScriptObject o1,
			JavaScriptObject o2) /*-{
		callback([ o1, o2 ]);
	}-*/;

	private native void invokeCb(JavaScriptObject cb, JavaScriptObject r) /*-{
		if (cb)
			cb(r);
	}-*/;

	public void onCopy(JavaScriptObject items, JsFolder to) {
		if (isArray(items))
			actionHandler.onAction(getItems(items), FileSystemAction.copy, to);
		else
			actionHandler.onAction((JsFilesystemItem) items.cast(),
					FileSystemAction.copy, to);
	}

	public void onMove(JavaScriptObject items, JsFolder to) {
		if (isArray(items))
			actionHandler.onAction(getItems(items), FileSystemAction.move, to);
		else
			actionHandler.onAction((JsFilesystemItem) items.cast(),
					FileSystemAction.move, to);
	}

	public void onRename(JsFilesystemItem item, String name) {
		actionHandler.onAction(item, FileSystemAction.rename, name);
	}

	public void onDelete(JavaScriptObject items) {
		if (isArray(items))
			actionHandler.onAction(getItems(items), FileSystemAction.delete, null);
		else
			actionHandler.onAction((JsFilesystemItem) items.cast(),
					FileSystemAction.delete, null);
	}
	
	private List<JsFilesystemItem> getItems(JavaScriptObject items) {
		if (isArray(items))
			return JsUtil
					.asList((JsArray) items.cast(), JsFilesystemItem.class);

		List<JsFilesystemItem> result = new ArrayList();
		result.add((JsFilesystemItem) items.cast());
		return result;
	}

	private native boolean isArray(JavaScriptObject o) /*-{
		return $wnd.isArray(o);
	}-*/;

	private native JavaScriptObject createNativeInterface(
			DefaultClientInterface e, String pluginBaseUrl,
			JavaScriptObject textProvider, JavaScriptObject service,
			JavaScriptObject viewManager, JavaScriptObject logger) /*-{
		var env = {};

		env.addResponseProcessor = function(cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::addResponseProcessor(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.addEventHandler = function(cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::addEventHandler(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.filesystem = function() {
			return {
				itemDetails : function(item, data, cb) {
					e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::getItemDetails(Lorg/sjarvela/mollify/client/filesystem/js/JsFilesystemItem;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(item, data, cb);
				},
				folders : function(p, cb) {
					e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::getFolders(Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;Lcom/google/gwt/core/client/JavaScriptObject;)(p, cb);
				},
				copy : function(items, to) {
					//var args = Array.prototype.slice.call(arguments);
					//var to = null;
					//var items = null;
					//if (args.length < 1)
					//	return;
					//else if (args.length == 1)
					//	items = args[0];
					//else {
					//	items = args.slice(0, args.length - 1);
					//	to = args.slice(args.length - 1);
					//}
					e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::onCopy(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;)(items, to);
				},
				move : function(items, to) {
					e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::onMove(Lcom/google/gwt/core/client/JavaScriptObject;Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;)(items, to);
				},
				rename : function(item, name) {
					e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::onRename(Lorg/sjarvela/mollify/client/filesystem/js/JsFilesystemItem;Ljava/lang/String;)(item, name);
				},
				del : function(items) {
					e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::onDelete(Lcom/google/gwt/core/client/JavaScriptObject;)(items);
				}
			};
		}

		env.session = {
			get : function() {
				return e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::getSession()();
			}
		}

		env.service = function() {
			return service;
		}

		env.texts = function() {
			return textProvider;
		}

		env.log = function() {
			return logger;
		}

		env.views = function() {
			return viewManager;
		}

		env.pluginUrl = function(id) {
			return pluginBaseUrl + id + "/";
		}

		return env;
	}-*/;
}
