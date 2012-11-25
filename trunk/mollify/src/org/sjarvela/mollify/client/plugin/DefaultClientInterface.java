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

import java.util.List;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.event.DefaultEventDispatcher;
import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.plugin.response.NativeResponseProcessor;
import org.sjarvela.mollify.client.plugin.service.NativeService;
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

	private native void invokeCb(JavaScriptObject cb, JavaScriptObject r) /*-{
		if (cb)
			cb(r);
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
				folders : function(p, cb) {
					e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::getFolders(Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;Lcom/google/gwt/core/client/JavaScriptObject;)(p, cb);
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
