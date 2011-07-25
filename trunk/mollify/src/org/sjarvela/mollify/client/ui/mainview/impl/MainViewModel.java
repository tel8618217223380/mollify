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

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FolderHierarchyInfo;
import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderModel;
import org.sjarvela.mollify.client.filesystem.foldermodel.FolderProvider;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ResultCallback;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.ui.mainview.impl.DefaultMainView.ViewType;

import com.google.gwt.core.client.JavaScriptObject;

public class MainViewModel {
	private final SessionInfo session;
	private final FileSystemService fileServices;
	private final List<Folder> rootFolders;

	private FolderInfoRequestDataProvider dataRequestProvider = null;
	private FolderModel folderModel;

	private List<File> files = new ArrayList();
	private List<Folder> folders = new ArrayList();
	private List<FileSystemItem> all = new ArrayList();
	private List<FileSystemItem> selected = new ArrayList();
	private FilePermission folderPermission = FilePermission.None;
	private JsObj data;

	public MainViewModel(FileSystemService fileServices, SessionInfo session,
			FolderProvider folderProvider) {
		this.fileServices = fileServices;
		this.session = session;
		this.rootFolders = folderProvider.getRootFolders();

		clear();
	}

	public void clear() {
		folderModel = new FolderModel();

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

	public FolderModel getFolderModel() {
		return folderModel;
	}

	public List<Folder> getRootFolders() {
		return rootFolders;
	}

	public List<Folder> getSubFolders() {
		return folders;
	}

	public FilePermission getFolderPermission() {
		return folderPermission;
	}

	public List<File> getFiles() {
		return files;
	}

	public List<FileSystemItem> getAllItems() {
		return all;
	}

	public boolean hasFolder() {
		return folderModel.getCurrentFolder() != null;
	}

	public Folder getCurrentFolder() {
		return folderModel.getCurrentFolder();
	}

	public void changeToRootFolder(Folder root, ResultListener resultListener) {
		folderModel.setRootFolder(root);
		refreshData(resultListener);
	}

	public void changeToSubfolder(Folder folder, ResultListener resultListener) {
		folderModel.descendIntoFolder(folder);
		refreshData(resultListener);
	}

	public void changeToFolder(int level, Folder folder,
			ResultListener resultListener) {
		folderModel.changeFolder(level, folder);
		refreshData(resultListener);
	}

	public void moveToParentFolder(ViewType viewType,
			ResultListener resultListener) {
		folderModel.ascend();
		refreshData(resultListener);
	}

	public void refreshData(ResultListener<FolderInfo> resultListener) {
		if (getCurrentFolder() == null) {
			FolderInfo result = new FolderInfo(FilePermission.ReadOnly,
					rootFolders, null, null);
			onUpdateData(result);
			resultListener.onSuccess(result);
			return;
		}

		Folder currentFolder = getCurrentFolder();
		JavaScriptObject dataRequest = dataRequestProvider != null ? dataRequestProvider
				.getDataRequest(currentFolder) : null;
		fileServices.getFolderInfo(
				currentFolder,
				dataRequest,
				createListener(resultListener,
						new ResultCallback<FolderInfo>() {
							public void onCallback(FolderInfo result) {
								onUpdateData(result);
							}
						}));
	}

	private void onUpdateData(FolderInfo info) {
		this.folders = info.getFolders();
		this.files = info.getFiles();
		this.data = info.getData();
		this.folderPermission = info.getPermission();
		this.all = new ArrayList(info.getFolders());
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

	public void setSelected(List<FileSystemItem> selected) {
		this.selected = selected;
	}

	public List<FileSystemItem> getSelectedItems() {
		return this.selected;
	}

	public void clearSelected() {
		this.selected.clear();
	}

	public void changeToFolder(String id, final ResultListener listener) {
		fileServices.getFolderInfoWithHierarchy(id,
				new ResultListener<FolderHierarchyInfo>() {

					@Override
					public void onSuccess(FolderHierarchyInfo result) {
						folderModel.setFolderHierarchy(result.getHierarchy());
						onUpdateData(result);
					}

					@Override
					public void onFail(ServiceError error) {
						listener.onFail(error);
					}
				});
	}

	public JsObj getData() {
		return data;
	}
}
