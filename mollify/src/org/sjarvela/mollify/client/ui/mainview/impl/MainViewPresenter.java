/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.filesystem.SearchResult;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.js.JsFilesystemItem;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsFolderInfo;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.js.JsObjBuilder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.mainview.MainView;
import org.sjarvela.mollify.client.ui.mainview.MainViewListener;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class MainViewPresenter implements MainViewListener,
		FolderInfoRequestDataProvider {
	private static Logger logger = Logger.getLogger(MainViewPresenter.class
			.getName());

	private final MainViewModel model;
	private final MainView view;
	private final DialogManager dialogManager;
	private final SessionManager sessionManager;
	private final SessionService sessionService;

	private final FileSystemService fileSystemService;
	private final ConfigurationService configurationService;
	private final FileSystemActionHandler fileSystemActionHandler;
	private final TextProvider textProvider;
	// private final PermissionEditorViewFactory permissionEditorViewFactory;
	// private final PasswordDialogFactory passwordDialogFactory;
	// private final FileUploadDialogFactory fileUploadDialogFactory;
	// private final CreateFolderDialogFactory createFolderDialogFactory;
	private final ViewManager viewManager;
	// TODO private final DropBox dropBox;
	private final EventDispatcher eventDispatcher;

	// private final SearchResultDialogFactory searchResultDialogFactory;

	// private final boolean exposeFileUrls;

	public MainViewPresenter(ViewManager viewManager,
			DialogManager dialogManager, SessionManager sessionManager,
			MainViewModel model, MainView view,
			ConfigurationService configurationService,
			FileSystemService fileSystemService, TextProvider textProvider,
			FileSystemActionHandler fileSystemActionHandler,
			SessionService sessionService, EventDispatcher eventDispatcher) {
		this.dialogManager = dialogManager;
		this.viewManager = viewManager;
		this.sessionManager = sessionManager;
		this.configurationService = configurationService;
		this.fileSystemService = fileSystemService;
		this.sessionService = sessionService;

		this.model = model;
		this.view = view;
		this.textProvider = textProvider;
		this.fileSystemActionHandler = fileSystemActionHandler;
		this.eventDispatcher = eventDispatcher;

		/*
		 * TODO this.view .setListSelectController(new
		 * SelectController<FileSystemItem>() {
		 * 
		 * @Override public boolean isSelectable(FileSystemItem t) { if
		 * (t.isFile()) return true; if (Folder.Parent.equals(t)) return false;
		 * if (((Folder) t).isRoot()) return false; return true; }
		 * 
		 * });
		 */

		model.setRequestDataProvider(this);

		view.init(sessionManager.getSession().getRootFolders(), this);
	}

	@Override
	public void onViewLoaded() {
		if (!model.hasFolder()) {
			if (model.getRootFolders().size() == 0)
				view.showNoRoots();
			else if (model.getRootFolders().size() == 1)
				changeToRootFolder(model.getRootFolders().get(0));
			else
				view.showAllRoots();
		} else {
			refreshView(true);
		}
	}

	@Override
	public JavaScriptObject getDataRequest(JsFolder folder) {
		return view.getDataRequest(folder);
	}

	@Override
	public void onHomeSelected() {
		model.clear();
		view.showAllRoots();
	}

	// @Override
	// public void onSubFolderSelected(JsFolder f) {
	// changeToFolderOnCurrentLevel(f);
	// }

	@Override
	public void onFolderSelected(JsFolder f) {
		changeToFolder(f);
	}

//	@Override
//	public void onCopy(JsFolder f, JavaScriptObject items) {
//		fileSystemActionHandler.onAction(getItems(items),
//				FileSystemAction.copy, f);
//	}
//
//	@Override
//	public void onMove(JsFolder f, JavaScriptObject items) {
//		fileSystemActionHandler.onAction(getItems(items),
//				FileSystemAction.move, f);
//	}

	@Override
	public void onSearch(final String text, final JavaScriptObject cb) {
		view.showProgress();

		fileSystemService.search(null, text,
				new ResultListener<SearchResult>() {
					@Override
					public void onSuccess(SearchResult result) {
						view.hideProgress();
						call(cb, result);
					}

					@Override
					public void onFail(ServiceError error) {
						dialogManager.showError(error);
					}
				});
	}

	private List<JsFilesystemItem> getItems(JavaScriptObject items) {
		// TODO check if array
		List<JsFilesystemItem> result = new ArrayList();
		result.add((JsFilesystemItem) items.cast());
		return result;
	}

	@Override
	public void onRefresh() {
		this.reload();
	}

	@Override
	public void onCreateFolder(String name) {
		fileSystemService.createFolder(model.getCurrentFolder(), name,
				createReloadListener("Create folder"));
	}

	@Override
	public void onChangePassword(String oldPassword, String newPassword,
			final JavaScriptObject callback) {
		configurationService.changePassword(oldPassword, newPassword,
				new ResultListener<Boolean>() {

					@Override
					public void onSuccess(Boolean result) {
						call(callback, null);
					}

					@Override
					public void onFail(ServiceError error) {
						if (ServiceErrorType.AUTHENTICATION_FAILED.equals(error
								.getType())) {
							dialogManager.showInfo(
									textProvider
											.getText(Texts.passwordDialogTitle),
									textProvider
											.getText(Texts.passwordDialogOldPasswordIncorrect));
						} else {
							onError(error, false);
						}
					}
				});
	}

	// @Override
	// public void getItemDetails(final JsFilesystemItem item,
	// final JavaScriptObject requestData, final JavaScriptObject callback) {
	// fileSystemService.getItemDetails(
	// item,
	// requestData,
	// createItemDetailsListener(item,
	// new ResultCallback<ItemDetails>() {
	// @Override
	// public void onCallback(ItemDetails details) {
	// boolean root = !item.isFile()
	// && ((JsFolder) item.cast()).isRoot();
	// boolean writable = !root
	// && details.getFilePermission()
	// .canWrite();
	// List<JsObj> itemActions = getItemActions(item,
	// writable, root);
	// JsArray<JsObj> actions = JsUtil.asJsArray(
	// itemActions, JsObj.class);
	// call2(callback, details, actions);
	// // call2(callback, details, JsUtil.asJsArray(
	// // itemActions, JsObj.class));
	// }
	// }));
	// }

	@Override
	public void getSessionActions(JavaScriptObject callback) {
		List<JsObj> sessionActions = getSessionActions();
		JsArray<JsObj> actions = JsUtil.asJsArray(sessionActions, JsObj.class);
		call(callback, actions);
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

	private List<JsObj> getSessionActions() {
		List<JsObj> actions = new ArrayList();
		SessionInfo session = sessionManager.getSession();
		if (!session.isAuthenticated())
			return actions;

		if (session.getFeatures().changePassword()) {
			actions.add(createAction("session-changepassword",
					Texts.mainViewChangePasswordTitle.name(),
					JsUtil.createJsCallback(new Callback() {
						@Override
						public void onCallback() {
							view.onChangePassword();
						}
					})));

			actions.add(createSeparator());
		}

		if (session.getDefaultPermissionMode().isAdmin()) {
			actions.add(createAction("session-openadmin",
					Texts.mainViewAdministrationTitle.name(),
					JsUtil.createJsCallback(new Callback() {
						@Override
						public void onCallback() {
							view.onOpenAdminUtil(configurationService
									.getAdministrationUrl());
						}
					})));

			actions.add(createSeparator());
		}

		actions.add(createAction("session-logout",
				Texts.mainViewLogoutTitle.name(),
				JsUtil.createJsCallback(new Callback() {
					@Override
					public void onCallback() {
						sessionService.logout(new ResultListener<Boolean>() {
							@Override
							public void onSuccess(Boolean result) {
								sessionManager.endSession();
							}

							@Override
							public void onFail(ServiceError error) {
								onError(error, false);
							}
						});
					}
				})));

		return actions;
	}

	private JsObj createAction(String id, String titleKey, JavaScriptObject cb) {
		return new JsObjBuilder().string("type", "action").string("id", id)
				.string("title-key", titleKey).obj("callback", cb).create();
	}

	// private List<JsObj> getItemActions(JsFilesystemItem item, boolean
	// writable,
	// boolean root) {
	// List<JsObj> actions = new ArrayList();
	//
	// if (item.isFile() || !root)
	// actions.add(createAction(item, FileSystemAction.download,
	// Texts.fileActionDownloadTitle.name()));
	//
	// actions.add(createSeparator());
	//
	// if (writable)
	// actions.add(createAction(item, FileSystemAction.rename,
	// Texts.fileActionRenameTitle.name()));
	// if (!root)
	// actions.add(createAction(item, FileSystemAction.copy,
	// Texts.fileActionCopyTitle.name()));
	// if (item.isFile())
	// actions.add(createAction(item, FileSystemAction.copyHere,
	// Texts.fileActionCopyHereTitle.name()));
	// if (writable)
	// actions.add(createAction(item, FileSystemAction.move,
	// Texts.fileActionMoveTitle.name()));
	// if (writable)
	// actions.add(createAction(item, FileSystemAction.delete,
	// Texts.fileActionDeleteTitle.name()));
	// return actions;
	// }

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
					fileSystemActionHandler.onAction(item,
							(FileSystemAction) action);
			}
		});
		return new JsObjBuilder().string("type", "action")
				.string("group", "core").string("id", action.name())
				.string("title-key", titleKey).obj("callback", cb).create();
	}

	public void changeToRootFolder(final JsFolder root) {
		view.showProgress();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				model.changeToFolder(root, createFolderChangeListener(true));
			}
		});
	}

	// public void changeToFolderOnCurrentLevel(final JsFolder folder) {
	// view.showProgress();
	// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	// @Override
	// public void execute() {
	// model.changeToSubfolder(folder,
	// createFolderChangeListener(true));
	// }
	// });
	// }

	public void changeToFolder(final JsFolder folder) {
		view.showProgress();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				model.changeToFolder(folder, createFolderChangeListener(true));
			}
		});
	}

	public void reset() {
		view.clear();
	}

	public void reload() {
		view.showProgress();
		model.refreshData(new ResultListener<JsFolderInfo>() {
			public void onFail(ServiceError error) {
				view.hideProgress();
				onError(error, false);
			}

			public void onSuccess(JsFolderInfo result) {
				refreshView(false);
			}
		});
	}

	private void refreshView(boolean folderChange) {
		// List<FileSystemItem> allItems = new ArrayList(model.getAllItems());
		// if (model.getFolderModel().canAscend())
		// allItems.add(0, Folder.Parent);

		if (folderChange)
			view.setFolder(model.getFolderHierarchy(), model
					.getFolderPermission().canWrite());
		view.setData(model.getAllItems(), model.getData());
		// view.showAddButton(model.getFolderPermission().canWrite());
		// view.refresh();
		// if (exposeFileUrls)
		// refreshFileUrls(model.getFiles());
	}

	// private void refreshFileUrls(List<File> files) {
	// String sessionId = sessionManager.getSession().getSessionId();
	// Map<String, String> urls = new HashMap();
	// for (File f : files)
	// urls.put(f.getName(),
	// fileSystemService.getDownloadUrl(f, sessionId));
	// // TODO view.refreshFileUrls(urls);
	// }

	// @Override
	// public void onMoveToParentFolder() {
	// if (!model.getFolderModel().canAscend())
	// return;
	// view.showProgress();
	// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	// @Override
	// public void execute() {
	// model.moveToParentFolder(view.getViewType(),
	// createFolderChangeListener());
	// }
	// });
	// }

	public void onError(ServiceError error, boolean reload) {
		dialogManager.showError(error);

		if (reload)
			reload();
		else
			reset();
	}

	public void retrieveFromUrl() {
		if (!model.hasFolder() || model.getCurrentFolder().isEmpty())
			return;

		// TODO
		// dialogManager.showInputDialog(
		// textProvider.getText(Texts.retrieveUrlTitle),
		// textProvider.getText(Texts.retrieveUrlMessage), "",
		// new InputListener() {
		// @Override
		// public void onInput(String url) {
		// retrieveUrl(url);
		// }
		//
		// @Override
		// public boolean isInputAcceptable(String input) {
		// return input.length() > 0
		// && input.toLowerCase().startsWith("http");
		// }
		// });
	}

	/*
	 * private void retrieveUrl(final String url) { // final WaitDialog
	 * waitDialog = dialogManager.openWaitDialog("", //
	 * textProvider.getText(Texts.pleaseWait));
	 * 
	 * fileSystemService.retrieveUrl(model.getCurrentFolder(), url, new
	 * ResultListener() {
	 * 
	 * @Override public void onSuccess(Object result) { // waitDialog.close();
	 * logger.log(Level.INFO, "URL retrieve complete"); reload(); }
	 * 
	 * @Override public void onFail(ServiceError error) { // TODO
	 * waitDialog.close();
	 * 
	 * // if (error.getError().getCode() == 301) //
	 * dialogManager.showInfo(textProvider // .getText(Texts.retrieveUrlTitle),
	 * // textProvider.getText( // Texts.retrieveUrlNotFound, url)); // else if
	 * (error.getError().getCode() == 302) // dialogManager.showInfo( //
	 * textProvider // .getText(Texts.retrieveUrlTitle), // textProvider //
	 * .getText( // Texts.retrieveUrlNotAuthorized, // url)); // else if
	 * (ServiceErrorType.REQUEST_FAILED.equals(error // .getType())) //
	 * dialogManager.showInfo(textProvider // .getText(Texts.retrieveUrlTitle),
	 * // textProvider // .getText(Texts.retrieveUrlFailed), //
	 * error.getDetails()); // else // dialogManager.showError(error); } }); }
	 */

	private ResultListener createReloadListener(final String operation) {
		return createListener(new Callback() {
			public void onCallback() {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						logger.log(Level.INFO, operation + " complete");
						reload();
					}
				});
			}
		});
	}

	private ResultListener createFolderChangeListener(boolean folderChange) {
		return createListener(createRefreshCallback(folderChange));
	}

	// private Callback createCurrentFolderChangedEventCallback() {
	// return new Callback() {
	// @Override
	// public void onCallback() {
	// eventDispatcher.onEvent(MainViewEvent
	// .onCurrentFolderChanged(model.getCurrentFolder()));
	// }
	// };
	// }

	private Callback createRefreshCallback(final boolean folderChange) {
		return new Callback() {
			public void onCallback() {
				refreshView(folderChange);
			}
		};
	}

	private ResultListener createListener(final Callback... callbacks) {
		return new ResultListener<Object>() {
			public void onFail(ServiceError error) {
				onError(error, true);
			}

			public void onSuccess(Object result) {
				for (Callback callback : callbacks)
					callback.onCallback();
			}
		};
	}

	// public void logout() {
	// sessionService.logout(new ResultListener<Boolean>() {
	// @Override
	// public void onSuccess(Boolean result) {
	// sessionManager.endSession();
	// }
	//
	// @Override
	// public void onFail(ServiceError error) {
	// onError(error, false);
	// }
	// });
	// }

	/*
	 * public void changePassword(String oldPassword, String newPassword) {
	 * configurationService.changePassword(oldPassword, newPassword, new
	 * ResultListener() { public void onFail(ServiceError error) { if
	 * (ServiceErrorType.AUTHENTICATION_FAILED.equals(error .getType())) { //
	 * TODO // dialogManager.showInfo( // textProvider //
	 * .getText(Texts.passwordDialogTitle), // textProvider //
	 * .getText(Texts.passwordDialogOldPasswordIncorrect)); } else {
	 * onError(error, false); } }
	 * 
	 * public void onSuccess(Object result) { // TODO // dialogManager.showInfo(
	 * // textProvider.getText(Texts.passwordDialogTitle), // textProvider //
	 * .getText(Texts.passwordDialogPasswordChangedSuccessfully)); } }); }
	 */

	/*
	 * public void onEditItemPermissions() { //
	 * permissionEditorViewFactory.openPermissionEditor(null); }
	 */

	// public void onOpenAdministration() {
	// // TODO ulkoista
	// viewManager.openUrlInNewWindow(configurationService
	// .getAdministrationUrl());
	// }

	/*
	 * public void onSelectAll() { view.selectAll(); }
	 * 
	 * public void onSelectNone() { view.selectNone(); }
	 */

	/*
	 * public void onCopySelected() {
	 * fileSystemActionHandler.onAction(model.getSelectedItems(),
	 * FileSystemAction.copy, null); }
	 * 
	 * public void onMoveSelected() {
	 * fileSystemActionHandler.onAction(model.getSelectedItems(),
	 * FileSystemAction.move, null); }
	 * 
	 * public void onDeleteSelected() {
	 * fileSystemActionHandler.onAction(model.getSelectedItems(),
	 * FileSystemAction.delete, null); }
	 */

	// @Override
	// public void onSearch(final String text) {
	// // if (model.getCurrentFolder() instanceof VirtualGroupFolder) {
	// // return; // TODO support this
	// // }
	// view.showProgress();
	//
	// fileSystemService.search(model.getCurrentFolder(), text,
	// new ResultListener<SearchResult>() {
	// @Override
	// public void onSuccess(SearchResult result) {
	// // TODO view.clearSearchField();
	// view.hideProgress();
	// onShowSearchResult(text, result);
	// }
	//
	// @Override
	// public void onFail(ServiceError error) {
	// view.hideProgress();
	// // TODO dialogManager.showError(error);
	// }
	// });
	// }

	/*
	 * boolean slidebarVisible = false;
	 * 
	 * public void onToggleSlidebar() { toggle(!slidebarVisible);
	 * slidebarVisible = !slidebarVisible; }
	 * 
	 * private native void toggle(boolean open) /*-{
	 * $wnd.$("#mollify-mainview-slidebar").stop().animate({ 'width' : open ?
	 * "300px" : "0px" }, 200);
	 * $wnd.$("#mollify-main-lower-content").stop().animate({ 'marginRight' :
	 * open ? "300px" : "0px" }, 200); }-
	 */;

	// @Override
	// public List<JsFilesystemItem> getSelectedItems() {
	// return model.getSelectedItems();
	// }

	/*
	 * public JsFolder getCurrentFolder() { return model.getCurrentFolder(); }
	 * 
	 * public void onShowListView() { setViewType(ViewType.list); }
	 * 
	 * public void onShowGridView(boolean small) { setViewType(small ?
	 * ViewType.gridSmall : ViewType.gridLarge); }
	 * 
	 * private void setViewType(ViewType type) { view.showProgress(); // TODO
	 * view.setViewType(type); reload(); }
	 */

	// @Override
	// public JavaScriptObject getDataRequest(JsFolder folder) {
	// if (!ViewType.list.equals(view.getViewType()))
	// return null;
	// return null;
	// /*
	// * TODO return pluginEnvironment.getFileListExt().getDataRequest(
	// * folder, ((FileListWithExternalColumns) view.getFileWidget())
	// * .getColumns());
	// */
	// }
}