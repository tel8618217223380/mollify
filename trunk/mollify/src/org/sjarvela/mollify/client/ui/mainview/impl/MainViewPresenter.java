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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.event.Event;
import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.SearchResult;
import org.sjarvela.mollify.client.filesystem.VirtualGroupFolder;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FolderHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.plugin.ClientInterface;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;
import org.sjarvela.mollify.client.ui.dialog.CreateFolderDialogFactory;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dialog.InputListener;
import org.sjarvela.mollify.client.ui.dialog.WaitDialog;
import org.sjarvela.mollify.client.ui.dnd.DragDataProvider;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.folderselector.FolderListener;
import org.sjarvela.mollify.client.ui.mainview.MainView;
import org.sjarvela.mollify.client.ui.mainview.MainView.ViewType;
import org.sjarvela.mollify.client.ui.password.PasswordDialogFactory;
import org.sjarvela.mollify.client.ui.permissions.PermissionEditorViewFactory;
import org.sjarvela.mollify.client.ui.searchresult.SearchResultDialogFactory;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;

public class MainViewPresenter implements FolderListener, PasswordHandler,
		DragDataProvider<FileSystemItem>, SearchListener,
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
	private final PermissionEditorViewFactory permissionEditorViewFactory;
	private final PasswordDialogFactory passwordDialogFactory;
	private final FileUploadDialogFactory fileUploadDialogFactory;
	private final CreateFolderDialogFactory createFolderDialogFactory;
	private final ViewManager viewManager;
	// TODO private final DropBox dropBox;
	private final EventDispatcher eventDispatcher;
	private final SearchResultDialogFactory searchResultDialogFactory;

	private final boolean exposeFileUrls;

	private final ClientInterface pluginEnvironment;

	public MainViewPresenter(DialogManager dialogManager,
			ViewManager viewManager, SessionManager sessionManager,
			MainViewModel model, MainView view,
			ConfigurationService configurationService,
			FileSystemService fileSystemService, TextProvider textProvider,
			FileSystemActionHandler fileSystemActionHandler,
			PermissionEditorViewFactory permissionEditorViewFactory,
			PasswordDialogFactory passwordDialogFactory,
			FileUploadDialogFactory fileUploadDialogFactory,
			CreateFolderDialogFactory createFolderDialogFactory,
			boolean exposeFileUrls, SessionService sessionService,
			EventDispatcher eventDispatcher,
			SearchResultDialogFactory searchResultDialogFactory,
			ClientInterface pluginEnvironment) {
		this.dialogManager = dialogManager;
		this.viewManager = viewManager;
		this.sessionManager = sessionManager;
		this.configurationService = configurationService;
		this.fileSystemService = fileSystemService;
		this.sessionService = sessionService;
		this.pluginEnvironment = pluginEnvironment;

		this.model = model;
		this.view = view;
		this.textProvider = textProvider;
		this.fileSystemActionHandler = fileSystemActionHandler;
		this.permissionEditorViewFactory = permissionEditorViewFactory;
		this.passwordDialogFactory = passwordDialogFactory;
		this.fileUploadDialogFactory = fileUploadDialogFactory;
		this.createFolderDialogFactory = createFolderDialogFactory;
		this.exposeFileUrls = exposeFileUrls;
		this.eventDispatcher = eventDispatcher;
		this.searchResultDialogFactory = searchResultDialogFactory;

		// TODO
		// this.view.getItemContext().setActionHandler(fileSystemActionHandler);

		// TODO this.view.getFolderSelector().addListener(this);
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

		this.setListOrder(FileList.COLUMN_ID_NAME, SortOrder.asc);

		if (model.getSession().isAuthenticationRequired())
			view.setUsername(model.getSession().getUser());

		// TODO view.addSearchListener(this);
		model.setRequestDataProvider(this);
	}

	public void initialize() {
		// TODO if (exposeFileUrls)
		// viewManager.getHiddenPanel().add(view.createFileUrlContainer());

		if (!model.hasFolder())
			changeToRootFolder(model.getRootFolders().size() == 1 ? model
					.getRootFolders().get(0) : null);
		if (model.getRootFolders().size() == 0)
			view.hideButtons();
	}

	public void onFileSystemItemSelected(final FileSystemItem item,
			String columnId, Element e) {
		if (columnId.equals(FileList.COLUMN_ID_NAME)) {
			if (item.isFile()) {
				// TODO view.showItemContext(item, e);
			} else {
				view.showProgress();

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						Folder folder = (Folder) item;

						if (folder == Folder.Parent)
							onMoveToParentFolder();
						else
							changeToFolder(folder);
					}
				});
			}
		}
	}

	public void changeToRootFolder(final Folder root) {
		view.showProgress();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				model.changeToRootFolder(root, createFolderChangeListener());
			}
		});
	}

	public void changeToFolder(final Folder folder) {
		view.showProgress();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				model.changeToSubfolder(folder, createFolderChangeListener());
			}
		});
	}

	public void reset() {
		view.clear();
	}

	public void reload() {
		view.showProgress();
		model.refreshData(new ResultListener<FolderInfo>() {
			public void onFail(ServiceError error) {
				view.hideProgress();
				onError(error, false);
			}

			public void onSuccess(FolderInfo result) {
				refreshView();
			}
		});
	}

	private void refreshView() {
		List<FileSystemItem> allItems = new ArrayList(model.getAllItems());
		if (model.getFolderModel().canAscend())
			allItems.add(0, Folder.Parent);

		view.setData(allItems, model.getData());
		view.showAddButton(model.getFolderPermission().canWrite());
		view.refresh();
		if (exposeFileUrls)
			refreshFileUrls(model.getFiles());
	}

	private void refreshFileUrls(List<File> files) {
		String sessionId = sessionManager.getSession().getSessionId();
		Map<String, String> urls = new HashMap();
		for (File f : files)
			urls.put(f.getName(),
					fileSystemService.getDownloadUrl(f, sessionId));
		// TODO view.refreshFileUrls(urls);
	}

	@Override
	public void onMoveToParentFolder() {
		if (!model.getFolderModel().canAscend())
			return;
		view.showProgress();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				model.moveToParentFolder(view.getViewType(),
						createFolderChangeListener());
			}
		});
	}

	@Override
	public void onChangeToFolder(final int level, final Folder folder) {
		view.showProgress();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				model.changeToFolder(level, folder,
						createFolderChangeListener());
			}
		});
	}

	public void onError(ServiceError error, boolean reload) {
		dialogManager.showError(error);

		if (reload)
			reload();
		else
			reset();
	}

	public void openUploadDialog() {
		if (!model.hasFolder() || model.getCurrentFolder().isEmpty())
			return;

		fileUploadDialogFactory.openFileUploadDialog(model.getCurrentFolder(),
				createReloadListener("Upload"));
	}

	public void openNewFolderDialog() {
		if (!model.hasFolder() || model.getCurrentFolder().isEmpty())
			return;

		createFolderDialogFactory.openCreateFolderDialog(
				model.getCurrentFolder(), new FolderHandler() {
					public void createFolder(Folder parentFolder,
							String folderName) {
						fileSystemService.createFolder(parentFolder,
								folderName,
								createReloadListener("Create folder"));
					}
				});
	}

	public void retrieveFromUrl() {
		if (!model.hasFolder() || model.getCurrentFolder().isEmpty())
			return;

		dialogManager.showInputDialog(
				textProvider.getText(Texts.retrieveUrlTitle),
				textProvider.getText(Texts.retrieveUrlMessage), "",
				new InputListener() {
					@Override
					public void onInput(String url) {
						retrieveUrl(url);
					}

					@Override
					public boolean isInputAcceptable(String input) {
						return input.length() > 0
								&& input.toLowerCase().startsWith("http");
					}
				});
	}

	private void retrieveUrl(final String url) {
		final WaitDialog waitDialog = dialogManager.openWaitDialog("",
				textProvider.getText(Texts.pleaseWait));

		fileSystemService.retrieveUrl(model.getCurrentFolder(), url,
				new ResultListener() {
					@Override
					public void onSuccess(Object result) {
						waitDialog.close();
						logger.log(Level.INFO, "URL retrieve complete");
						reload();
					}

					@Override
					public void onFail(ServiceError error) {
						waitDialog.close();

						if (error.getError().getCode() == 301)
							dialogManager.showInfo(textProvider
									.getText(Texts.retrieveUrlTitle),
									textProvider.getText(
											Texts.retrieveUrlNotFound, url));
						else if (error.getError().getCode() == 302)
							dialogManager.showInfo(
									textProvider
											.getText(Texts.retrieveUrlTitle),
									textProvider
											.getText(
													Texts.retrieveUrlNotAuthorized,
													url));
						else if (ServiceErrorType.REQUEST_FAILED.equals(error
								.getType()))
							dialogManager.showInfo(textProvider
									.getText(Texts.retrieveUrlTitle),
									textProvider
											.getText(Texts.retrieveUrlFailed),
									error.getDetails());
						else
							dialogManager.showError(error);
					}
				});
	}

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

	private ResultListener createFolderChangeListener() {
		return createListener(createRefreshCallback(),
				createCurrentFolderChangedEventCallback());
	}

	private Callback createCurrentFolderChangedEventCallback() {
		return new Callback() {
			@Override
			public void onCallback() {
				eventDispatcher.onEvent(MainViewEvent
						.onCurrentFolderChanged(model.getCurrentFolder()));
			}
		};
	}

	private Callback createRefreshCallback() {
		return new Callback() {
			public void onCallback() {
				refreshView();
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

	public void logout() {
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

	public void changePassword() {
		passwordDialogFactory.openPasswordDialog(this);
	}

	public void changePassword(String oldPassword, String newPassword) {
		configurationService.changePassword(oldPassword, newPassword,
				new ResultListener() {
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

					public void onSuccess(Object result) {
						dialogManager.showInfo(
								textProvider.getText(Texts.passwordDialogTitle),
								textProvider
										.getText(Texts.passwordDialogPasswordChangedSuccessfully));
					}
				});
	}

	public void setListOrder(String columnId, SortOrder sort) {
		view.sortColumn(columnId, sort);
	}

	public void onEditItemPermissions() {
		permissionEditorViewFactory.openPermissionEditor(null);
	}

	public void onOpenAdministration() {
		viewManager.openUrlInNewWindow(configurationService
				.getAdministrationUrl());
	}

	public void onToggleSelectMode() {
		// TODO view.setSelectMode(view.selectModeButton().isDown());
	}

	public void onFileSystemItemSelectionChanged(List<FileSystemItem> selected) {
		model.setSelected(selected);
		// TODO view.updateFileSelection(selected);
	}

	public void onSelectAll() {
		view.selectAll();
	}

	public void onSelectNone() {
		view.selectNone();
	}

	public void onCopySelected() {
		fileSystemActionHandler.onAction(model.getSelectedItems(),
				FileSystemAction.copy, null, null, new Callback() {
					@Override
					public void onCallback() {
						view.selectNone();
					}
				});
	}

	public void onMoveSelected() {
		fileSystemActionHandler.onAction(model.getSelectedItems(),
				FileSystemAction.move, null, null, new Callback() {
					@Override
					public void onCallback() {
						view.selectNone();
					}
				});
	}

	public void onDeleteSelected() {
		fileSystemActionHandler.onAction(model.getSelectedItems(),
				FileSystemAction.delete, null, null, new Callback() {
					@Override
					public void onCallback() {
						view.selectNone();
					}
				});
	}

	@Override
	public void onSearch(final String text) {
		if (model.getCurrentFolder() instanceof VirtualGroupFolder) {
			return; // TODO support this
		}
		view.showProgress();

		fileSystemService.search(model.getCurrentFolder(), text,
				new ResultListener<SearchResult>() {
					@Override
					public void onSuccess(SearchResult result) {
						// TODO view.clearSearchField();
						view.hideProgress();
						onShowSearchResult(text, result);
					}

					@Override
					public void onFail(ServiceError error) {
						view.hideProgress();
						dialogManager.showError(error);
					}
				});
	}

	protected void onShowSearchResult(String criteria, SearchResult result) {
		//TODO
//		if (result.getMatchCount() == 0)
//			dialogManager.showInfo(
//					textProvider.getText(Texts.searchResultsDialogTitle),
//					textProvider.getText(Texts.searchResultsNoMatchesFound));
//		else
//			searchResultDialogFactory.show(dropBox, criteria, result);
	}

	public void onAddSelectedToDropbox() {
		//TODO dropBox.addItems(getSelectedItems());
		view.selectNone();
	}

	boolean slidebarVisible = false;

	public void onToggleSlidebar() {
		toggle(!slidebarVisible);
		slidebarVisible = !slidebarVisible;
	}

	private native void toggle(boolean open) /*-{
		$wnd.$("#mollify-mainview-slidebar").stop().animate({
			'width' : open ? "300px" : "0px"
		}, 200);
		$wnd.$("#mollify-main-lower-content").stop().animate({
			'marginRight' : open ? "300px" : "0px"
		}, 200);
	}-*/;

	@Override
	public List<FileSystemItem> getSelectedItems() {
		return model.getSelectedItems();
	}

	public void onListRendered() {
		view.hideProgress();
		dispatchEvent(MainViewEvent.onFileListReady(model.getCurrentFolder()));
	}

	private void dispatchEvent(final Event event) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				eventDispatcher.onEvent(event);
			}
		});
	}

	public List<FileSystemItem> getAllItems() {
		return model.getAllItems();
	}

	public Folder getCurrentFolder() {
		return model.getCurrentFolder();
	}

	public void onShowListView() {
		setViewType(ViewType.list);
	}

	public void onShowGridView(boolean small) {
		setViewType(small ? ViewType.gridSmall : ViewType.gridLarge);
	}

	private void setViewType(ViewType type) {
		view.showProgress();
		// TODO view.setViewType(type);
		reload();
	}

	public void setCurrentFolder(String id) {
		model.changeToFolder(id, new ResultListener() {
			@Override
			public void onSuccess(Object result) {
				refreshView();
			}

			@Override
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

		});
	}

	@Override
	public JavaScriptObject getDataRequest(Folder folder) {
		if (!ViewType.list.equals(view.getViewType()))
			return null;
		return null;
		/*
		 * TODO return pluginEnvironment.getFileListExt().getDataRequest(
		 * folder, ((FileListWithExternalColumns) view.getFileWidget())
		 * .getColumns());
		 */
	}
}