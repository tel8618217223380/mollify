/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.php;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.filesystem.UserDirectory;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.util.Base64;
import org.sjarvela.mollify.client.util.JsUtil;
import org.sjarvela.mollify.client.util.MD5;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;

public class PhpSettingsService implements SettingsService {
	private final PhpService service;

	enum ConfigurationAction {
		get_users, add_user, update_user, remove_user, get_folders, add_folder, update_folder, remove_folder, get_user_folders, add_user_folder, update_user_folder, remove_user_folder
	}

	public PhpSettingsService(PhpService service) {
		this.service = service;
	}

	public void getUsers(final ResultListener<List<User>> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get users");

		service.doRequest(getUrl(ConfigurationAction.get_users),
				new ResultListener<JsArray<User>>() {
					public void onFail(ServiceError error) {
						resultListener.onFail(error);
					}

					public void onSuccess(JsArray<User> result) {
						resultListener.onSuccess(JsUtil.asList(result,
								User.class));
					}
				});
	}

	public void getFolders(
			final ResultListener<List<DirectoryInfo>> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directories");

		service.doRequest(getUrl(ConfigurationAction.get_folders),
				new ResultListener<JsArray<DirectoryInfo>>() {
					public void onFail(ServiceError error) {
						resultListener.onFail(error);
					}

					public void onSuccess(JsArray<DirectoryInfo> result) {
						resultListener.onSuccess(JsUtil.asList(result,
								DirectoryInfo.class));
					}
				});
	}

	public void addUser(String name, String password, PermissionMode mode,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.add_user, "name="
				+ Base64.encode(name), "password=" + MD5.generate(password),
				"permission_mode=" + mode.getStringValue()), resultListener);
	}

	public void editUser(User user, String name, PermissionMode mode,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.update_user, "id="
				+ user.getId(), "name=" + Base64.encode(name),
				"permission_mode=" + mode.getStringValue()), resultListener);
	}

	public void removeUser(User user, final ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.remove_user, "id="
				+ user.getId()), resultListener);
	}

	public void addFolder(String name, String path,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.add_folder, "name="
				+ Base64.encode(name), "path=" + Base64.encode(path)),
				resultListener);
	}

	public void editFolder(DirectoryInfo dir, String name, String path,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.update_folder, "id="
				+ dir.getId(), "name=" + Base64.encode(name), "path="
				+ Base64.encode(path)), resultListener);
	}

	public void removeFolder(DirectoryInfo dir, ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.remove_folder, "id="
				+ dir.getId()), resultListener);
	}

	private String getUrl(ConfigurationAction action, String... params) {
		return getUrl(action, Arrays.asList(params));
	}

	private String getUrl(ConfigurationAction action, List<String> parameters) {
		List<String> params = new ArrayList(parameters);
		params.add("action=" + action.name());
		return service.getUrl(RequestType.configuration, params);
	}

	public void getUserFolders(User user,
			final ResultListener<List<UserDirectory>> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directories");

		service.doRequest(getUrl(ConfigurationAction.get_user_folders,
				"user_id=" + user.getId()),
				new ResultListener<JsArray<UserDirectory>>() {
					public void onFail(ServiceError error) {
						resultListener.onFail(error);
					}

					public void onSuccess(JsArray<UserDirectory> result) {
						resultListener.onSuccess(JsUtil.asList(result,
								UserDirectory.class));
					}
				});
	}

	public void addUserFolder(User user, DirectoryInfo dir, String name,
			ResultListener resultListener) {
		List<String> params = new ArrayList();
		params.add("user_id=" + user.getId());
		params.add("id=" + dir.getId());
		if (name != null)
			params.add("name=" + Base64.encode(name));

		service.doRequest(getUrl(ConfigurationAction.add_user_folder, params),
				resultListener);
	}

	public void editUserFolder(User user, UserDirectory dir, String name,
			ResultListener resultListener) {
		List<String> params = new ArrayList();
		params.add("user_id=" + user.getId());
		params.add("id=" + dir.getId());
		if (name != null)
			params.add("name=" + Base64.encode(name));

		service.doRequest(
				getUrl(ConfigurationAction.update_user_folder, params),
				resultListener);
	}

	public void removeUserFolder(User user, UserDirectory dir,
			ResultListener resultListener) {
		service
				.doRequest(getUrl(ConfigurationAction.remove_user_folder,
						"user_id=" + user.getId(), "id=" + dir.getId()),
						resultListener);
	}
}
