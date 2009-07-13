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
import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FilePermissionMode;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserCache;

public class PermissionEditorModel {
	private final FileSystemItem item;
	private final ConfigurationService configurationService;
	private final FileSystemService fileSystemService;

	private ResultCallback<ServiceError> errorCallback = null;
	private List<User> users = null;
	private UserCache userCache;

	private FileItemUserPermission defaultPermission;
	private boolean originalDefaultPermissionExists;

	private List<FileItemUserPermission> effectivePermissions = new ArrayList();
	private List<FileItemUserPermission> newPermissions = new ArrayList();
	private List<FileItemUserPermission> modifiedPermissions = new ArrayList();
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
					userCache = new UserCache(users);
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
				}, userCache, new FileSystemItemCache(Arrays.asList(item)));
	}

	protected void updatePermissions(List<FileItemUserPermission> permissions) {
		boolean defaultPermissionFound = false;

		effectivePermissions.clear();
		newPermissions.clear();
		modifiedPermissions.clear();
		removedPermissions.clear();
		defaultPermission = new FileItemUserPermission(item, null,
				FilePermissionMode.None);

		for (FileItemUserPermission permission : permissions) {
			if (permission.getUser() != null) {
				effectivePermissions.add(permission);
			} else {
				if (defaultPermissionFound) {
					errorCallback.onCallback(new ServiceError(
							ServiceErrorType.INVALID_RESPONSE));
					return;
				}
				defaultPermissionFound = true;
				defaultPermission = permission;
			}
		}
		originalDefaultPermissionExists = defaultPermissionFound;
	}

	public List<User> getUsersWithoutPermission() {
		List<User> result = new ArrayList(users);
		for (FileItemUserPermission permission : effectivePermissions) {
			if (permission.getUser() == null)
				continue;
			result.remove(permission.getUser());
		}
		return result;
	}

	public boolean hasChanged() {
		return newPermissions.size() > 0 || modifiedPermissions.size() > 0
				|| removedPermissions.size() > 0;
	}

	public FilePermissionMode getDefaultPermission() {
		return defaultPermission.getPermission();
	}

	public void setDefaultPermission(FilePermissionMode permission) {
		// remove old default permission from update lists
		removedPermissions.remove(defaultPermission);
		newPermissions.remove(defaultPermission);
		modifiedPermissions.remove(defaultPermission);

		// create new default permission
		defaultPermission = new FileItemUserPermission(item, null, permission);

		if (FilePermissionMode.None.equals(permission))
			return;

		if (originalDefaultPermissionExists)
			modifiedPermissions.add(defaultPermission);
		else
			newPermissions.add(defaultPermission);
	}

	public List<FileItemUserPermission> getUserSpecificPermissions() {
		return effectivePermissions;
	}

	public void addPermission(User user, FilePermissionMode permission) {
		addPermission(new FileItemUserPermission(item, user, permission));
	}

	public void addPermission(FileItemUserPermission permission) {
		newPermissions.add(permission);
		effectivePermissions.add(permission);
	}

	public void editPermission(FileItemUserPermission permission) {
		if (newPermissions.contains(permission))
			return;
		modifiedPermissions.add(permission);

		updateUserPermission(permission);
	}

	private void updateUserPermission(FileItemUserPermission permission) {
		for (FileItemUserPermission userPermission : effectivePermissions)
			if (userPermission.getUser().equals(permission.getUser())) {
				effectivePermissions.remove(userPermission);
				effectivePermissions.add(permission);
				return;
			}
	}

	public void removePermission(FileItemUserPermission permission) {
		effectivePermissions.remove(permission);

		if (newPermissions.contains(permission)) {
			newPermissions.remove(permission);
		} else {
			modifiedPermissions.remove(permission);
			removedPermissions.add(permission);
		}
	}

	public void commit(final Callback successCallback) {
		fileSystemService.updateItemPermissions(newPermissions,
				modifiedPermissions, removedPermissions, new ResultListener() {
					public void onFail(ServiceError error) {
						onError(error);
					}

					public void onSuccess(Object result) {
						successCallback.onCallback();
					}
				});
	}

}
