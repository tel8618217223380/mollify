/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.FileItemUserPermission;
import org.sjarvela.mollify.client.session.FilePermissionMode;
import org.sjarvela.mollify.client.session.User;

public class PermissionEditorModel {
	private final FileSystemItem item;
	private final ConfigurationService configurationService;
	private final FileSystemService fileSystemService;

	private ResultCallback<ServiceError> errorCallback = null;
	private List<User> users = null;
	private Map<String, User> usersById = new HashMap();
	private FilePermissionMode defaultPermission;
	private FilePermissionMode originalDefaultPermission;

	private List<FileItemUserPermission> effectivePermissions = new ArrayList();
	private List<FileItemUserPermission> newPermissions = new ArrayList();
	private List<FileItemUserPermission> removedPermissions = new ArrayList();

	public PermissionEditorModel(FileSystemItem item,
			ConfigurationService configurationService,
			FileSystemService fileSystemService) {
		this.item = item;
		this.configurationService = configurationService;
		this.fileSystemService = fileSystemService;
	}

	public void setErrorCallback(ResultCallback<ServiceError> errorCallback) {
		this.errorCallback = errorCallback;
	}

	protected void onError(ServiceError error) {
		errorCallback.onCallback(error);
	}

	public FileSystemItem getItem() {
		return item;
	}

	public List<User> getUsers() {
		return users;
	}

	public void refresh(final Callback successCallback) {
		if (users == null) {
			configurationService.getUsers(new ResultListener<List<User>>() {
				public void onFail(ServiceError error) {
					onError(error);
				}

				public void onSuccess(List<User> result) {
					users = result;
					usersById.clear();
					for (User user : users)
						usersById.put(user.getId(), user);
					refreshPermissions(successCallback);
				}
			});
		}
		refreshPermissions(successCallback);
	}

	private void refreshPermissions(final Callback successCallback) {
		fileSystemService.getItemPermissions(item,
				new ResultListener<List<FileItemUserPermission>>() {
					public void onFail(ServiceError error) {
						onError(error);
					}

					public void onSuccess(List<FileItemUserPermission> result) {
						updatePermissions(result);
						successCallback.onCallback();
					}
				});
	}

	protected void updatePermissions(List<FileItemUserPermission> permissions) {
		boolean defaultPermissionFound = false;

		effectivePermissions.clear();
		defaultPermission = FilePermissionMode.None;

		for (FileItemUserPermission permission : permissions) {
			if (permission.getUserId() != null) {
				effectivePermissions.add(permission);
			} else {
				if (defaultPermissionFound) {
					errorCallback.onCallback(new ServiceError(
							ServiceErrorType.INVALID_RESPONSE));
					return;
				}
				defaultPermissionFound = true;
				defaultPermission = permission.getPermission();
			}
		}
		originalDefaultPermission = defaultPermission;
	}

	public boolean hasChanged() {
		return !defaultPermission.equals(originalDefaultPermission)
				|| newPermissions.size() > 0 || removedPermissions.size() > 0;
	}

	public FilePermissionMode getDefaultPermission() {
		return defaultPermission;
	}

	public List<FileItemUserPermission> getUserSpecificPermissions() {
		return effectivePermissions;
	}

	public void addPermission(FileItemUserPermission permission) {
		newPermissions.add(permission);
		effectivePermissions.add(permission);
	}

	public void removePermission(FileItemUserPermission permission) {
		effectivePermissions.remove(permission);

		if (newPermissions.contains(permission))
			newPermissions.remove(permission);
		else
			removedPermissions.add(permission);
	}

	public void commit(Callback successCallback) {
		// TODO Auto-generated method stub

	}
}
