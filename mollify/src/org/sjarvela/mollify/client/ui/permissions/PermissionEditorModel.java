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
import java.util.List;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.FileItemUserPermission;
import org.sjarvela.mollify.client.session.FilePermissionMode;

public class PermissionEditorModel {
	private final FileSystemItem item;
	private final FileSystemService service;

	private ResultCallback<ServiceError> errorCallback = null;
	private List<FileItemUserPermission> userSpecificPermissions = new ArrayList();
	private FilePermissionMode defaultPermission;

	public PermissionEditorModel(FileSystemItem item, FileSystemService service) {
		this.item = item;
		this.service = service;
	}

	public void setErrorCallback(ResultCallback<ServiceError> errorCallback) {
		this.errorCallback = errorCallback;
	}

	public FileSystemItem getItem() {
		return item;
	}

	public void refreshPermissions(final Callback successCallback) {
		service.getItemPermissions(item,
				new ResultListener<List<FileItemUserPermission>>() {
					public void onFail(ServiceError error) {
						errorCallback.onCallback(error);
					}

					public void onSuccess(List<FileItemUserPermission> result) {
						updatePermissions(result);
						successCallback.onCallback();
					}
				});
	}

	protected void updatePermissions(List<FileItemUserPermission> permissions) {
		boolean defaultPermissionFound = false;

		userSpecificPermissions.clear();
		defaultPermission = FilePermissionMode.None;

		for (FileItemUserPermission permission : permissions) {
			if (permission.getUser() != null) {
				userSpecificPermissions.add(permission);
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
	}

	public FilePermissionMode getDefaultPermission() {
		return defaultPermission;
	}

	public List<FileItemUserPermission> getUserSpecificPermissions() {
		return userSpecificPermissions;
	}
}
