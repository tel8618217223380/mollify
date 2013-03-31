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
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.foldermodel.FolderProvider;
import org.sjarvela.mollify.client.filesystem.js.JsFile;
import org.sjarvela.mollify.client.filesystem.js.JsFilesystemItem;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsFolderHierarchyInfo;
import org.sjarvela.mollify.client.filesystem.js.JsFolderInfo;
import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ResultCallback;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.file.FilePermission;

import com.google.gwt.core.client.JavaScriptObject;

public class MainViewModel {
	private final SessionInfo session;
	private final FileSystemService fileServices;
	private final List<JsRootFolder> rootFolders;

	private FolderInfoRequestDataProvider dataRequestProvider = null;
	// private FolderModel folderModel;

	private List<JsFile> files = new ArrayList();
	private List<JsFolder> folders = new ArrayList();
	private List<JavaScriptObject> all = new ArrayList();
	private List<JsFilesystemItem> selected = new ArrayList();
	private FilePermission folderPermission = FilePermission.None;
	private JsObj data;
	private JsFolder currentFolder;
	protected List<JsFolder> hierarchy;

	public MainViewModel(FileSystemService fileServices, SessionInfo session,
			FolderProvider folderProvider) {
		this.fileServices = fileServices;
		this.session = session;
		this.rootFolders = folderProvider.getRootFolders();

		clear();
	}

	public void clear() {
		// folderModel = new FolderModel();

		folders.clear();
		files.clear();
		all.clear();
	}

	public void setRequestDataProvider(
			FolderInfoRequestDataProvider requestDataProvider) {
		this.dataRequestProvider = requestDataProvider;
	}

	public SessionInfo getSession() {
		return session;
	}

	// public FolderModel getFolderModel() {
	// return folderModel;
	// }

	public List<JsRootFolder> getRootFolders() {
		return rootFolders;
	}

	public List<JsFolder> getSubFolders() {
		return folders;
	}

	public FilePermission getFolderPermission() {
		return folderPermission;
	}

	public List<JsFile> getFiles() {
		return files;
	}

	public List<JavaScriptObject> getAllItems() {
		return all;
	}

	public boolean hasFolder() {
		return getCurrentFolder() != null;
	}

	public JsFolder getCurrentFolder() {
		return currentFolder;
	}

	public List<JsFolder> getFolderHierarchy() {
		return hierarchy;
	}

	// public void changeToRootFolder(JsFolder root, ResultListener
	// resultListener) {
	// folderModel.setRootFolder(root);
	// refreshData(resultListener);
	// }

	// public void changeToSubfolder(JsFolder folder, ResultListener
	// resultListener) {
	// folderModel.descendIntoFolder(folder);
	// refreshData(resultListener);
	// }

	// public void changeToFolder(int level, JsFolder folder,
	// ResultListener resultListener) {
	// folderModel.changeFolder(level, folder);
	// refreshData(resultListener);
	// }

	// public void moveToParentFolder(ViewType viewType,
	// ResultListener resultListener) {
	// folderModel.ascend();
	// refreshData(resultListener);
	// }

	public void refreshData(ResultListener<JsFolderInfo> resultListener) {
		if (getCurrentFolder() == null) {
			/*
			 * TODO JsFolderInfo result = JsFolderInfo.create(rootFolders, null,
			 * FilePermission.ReadOnly); onUpdateData(result);
			 * resultListener.onSuccess(result); return;
			 */
		}

		JsFolder currentFolder = getCurrentFolder();
		/*
		 * TODOif (currentFolder instanceof VirtualGroupFolder) { FolderInfo
		 * result = new FolderInfo(FilePermission.ReadOnly,
		 * ((VirtualGroupFolder) currentFolder).getChildren(), null, null);
		 * onUpdateData(result); resultListener.onSuccess(result); return; }
		 */

		JavaScriptObject dataRequest = dataRequestProvider != null ? dataRequestProvider
				.getDataRequest(currentFolder) : null;
		fileServices.getFolderInfo(
				currentFolder,
				dataRequest,
				createListener(resultListener,
						new ResultCallback<JsFolderInfo>() {
							public void onCallback(JsFolderInfo result) {
								onUpdateData(result);
							}
						}));
	}

	private void onUpdateData(JsFolderInfo info) {
		this.folders = info.getFolders();

		List<JsFile> list = info.getFiles();
		this.files = list != null ? list : Collections.EMPTY_LIST;

		this.data = info.getData();
		this.folderPermission = info.getPermission();

		this.all = new ArrayList(this.folders);
		this.all.addAll(files);
	}

	private ResultListener createListener(final ResultListener listener,
			final ResultCallback resultCallback) {
		return new ResultListener<Object>() {
			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(Object result) {
				resultCallback.onCallback(result);
				listener.onSuccess(result);
			}
		};
	}

	public void setSelected(List<JsFilesystemItem> selected) {
		this.selected = selected;
	}

	public List<JsFilesystemItem> getSelectedItems() {
		return this.selected;
	}

	public void clearSelected() {
		this.selected.clear();
	}

	public void changeToFolder(JsFolder folder, final ResultListener listener) {
		// createListener(resultListener,
		// new ResultCallback<JsFolderInfo>() {
		// public void onCallback(JsFolderInfo result) {
		// onUpdateData(result);
		// }
		// }));
		currentFolder = folder;
		JavaScriptObject dataRequest = dataRequestProvider != null ? dataRequestProvider
				.getDataRequest(folder) : null;
		fileServices.getFolderInfoWithHierarchy(
				folder.getId(),
				dataRequest,
				createListener(listener,
						new ResultCallback<JsFolderHierarchyInfo>() {
							@Override
							public void onCallback(JsFolderHierarchyInfo result) {
								hierarchy = result.getHierarchy();
								onUpdateData(result);
							}
						}));
	}

	public JsObj getData() {
		return data;
	}
}
