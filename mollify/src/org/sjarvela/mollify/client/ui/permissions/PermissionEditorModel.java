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
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.Callback;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ResultCallback;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.session.file.FileSystemItemCache;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserBase;
import org.sjarvela.mollify.client.session.user.UserCache;
import org.sjarvela.mollify.client.session.user.UserGroup;
import org.sjarvela.mollify.client.session.user.UsersAndGroups;

public class PermissionEditorModel {
	private final ConfigurationService configurationService;
	private final FileSystemService fileSystemService;

	private ResultCallback<ServiceError> errorCallback = null;
	private UsersAndGroups usersAndGroups = null;
	private UserCache userCache;

	private FileSystemItem item;
	private FileItemUserPermission defaultPermission = null;
	private boolean originalDefaultPermissionExists = false;

	private List<FileItemUserPermission> effectivePermissions = new ArrayList();
	private List<FileItemUserPermission> newPermissions = new ArrayList();
	private List<FileItemUserPermission> modifiedPermissions = new ArrayList();
	private List<FileItemUserPermission> removedPermissions = new ArrayList();

	public PermissionEditorModel(FileSystemItem item,
			ConfigurationService configurationService,
			FileSystemService fileSystemService) {
		this.configurationService = configurationService;
		this.fileSystemService = fileSystemService;

		setItem(item);
	}

	public void setErrorCallback(ResultCallback<ServiceError> errorCallback) {
		this.errorCallback = errorCallback;
	}

	protected void onError(ServiceError error) {
		errorCallback.onCallback(error);
	}

	public void setItem(FileSystemItem item) {
		this.defaultPermission = null;
		this.originalDefaultPermissionExists = false;

		this.effectivePermissions = new ArrayList();
		this.newPermissions = new ArrayList();
		this.modifiedPermissions = new ArrayList();
		this.removedPermissions = new ArrayList();

		this.item = item;
	}

	public boolean hasItem() {
		return item != null;
	}

	public FileSystemItem getItem() {
		return item;
	}

	public List<User> getUsers() {
		return usersAndGroups.getUsers();
	}

	public List<UserGroup> getUserGroups() {
		return usersAndGroups.getUserGroups();
	}

	public void refresh(final Callback successCallback) {
		if (usersAndGroups == null) {
			configurationService
					.getUsersAndGroups(new ResultListener<UsersAndGroups>() {
						public void onFail(ServiceError error) {
							onError(error);
						}

						public void onSuccess(UsersAndGroups result) {
							usersAndGroups = result;
							userCache = new UserCache(usersAndGroups);
							refreshPermissions(successCallback);
						}
					});
		} else {
			refreshPermissions(successCallback);
		}
	}

	private void refreshPermissions(final Callback successCallback) {
		if (!hasItem()) {
			successCallback.onCallback();
			return;
		}

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
		defaultPermission = null;

		for (FileItemUserPermission permission : permissions) {
			if (permission.getUserOrGroup() != null) {
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
		List<User> result = new ArrayList(usersAndGroups.getUsers());
		for (FileItemUserPermission permission : effectivePermissions) {
			if (permission.getUserOrGroup() == null)
				continue;
			result.remove(permission.getUserOrGroup());
		}
		return result;
	}

	public List<UserGroup> getGroupsWithoutPermission() {
		List<UserGroup> result = new ArrayList(usersAndGroups.getUserGroups());
		for (FileItemUserPermission permission : effectivePermissions) {
			if (permission.getUserOrGroup() == null)
				continue;
			result.remove(permission.getUserOrGroup());
		}
		return result;
	}

	public boolean hasChanged() {
		if (!hasItem())
			return false;

		return newPermissions.size() > 0 || modifiedPermissions.size() > 0
				|| removedPermissions.size() > 0;
	}

	public FilePermission getDefaultPermission() {
		return defaultPermission == null ? null : defaultPermission
				.getPermission();
	}

	public void setDefaultPermission(FilePermission permission) {
		// remove old default permission from update lists
		removedPermissions.remove(defaultPermission);
		newPermissions.remove(defaultPermission);
		modifiedPermissions.remove(defaultPermission);

		if (permission == null) {
			if (originalDefaultPermissionExists)
				removedPermissions.add(defaultPermission);
			defaultPermission = null;
			return;
		}

		// create new default permission
		defaultPermission = new FileItemUserPermission(item, null, permission);

		if (originalDefaultPermissionExists)
			modifiedPermissions.add(defaultPermission);
		else
			newPermissions.add(defaultPermission);
	}

	public List<FileItemUserPermission> getPermissions() {
		List<FileItemUserPermission> list = new ArrayList(effectivePermissions);
		Collections.sort(list, new PermissionComparator());
		return list;
	}

	public void addPermission(UserBase userOrGroup, FilePermission permission) {
		addPermission(new FileItemUserPermission(item, userOrGroup, permission));
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
			if (userPermission.getUserOrGroup().equals(
					permission.getUserOrGroup())) {
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
