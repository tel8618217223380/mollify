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
import org.sjarvela.mollify.client.service.request.UrlParam;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.session.UserPermissionMode;
import org.sjarvela.mollify.client.util.JsUtil;

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

	public void addUser(String name, String password, UserPermissionMode mode,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.add_user, new UrlParam(
				"name", name, UrlParam.Encoding.BASE64), new UrlParam(
				"password", password, UrlParam.Encoding.MD5), new UrlParam(
				"permission_mode", mode.getStringValue(),
				UrlParam.Encoding.NONE)), resultListener);
	}

	public void editUser(User user, String name, UserPermissionMode mode,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.update_user, new UrlParam(
				"id", user.getId()), new UrlParam("name", name,
				UrlParam.Encoding.BASE64), new UrlParam("permission_mode", mode
				.getStringValue(), UrlParam.Encoding.NONE)), resultListener);
	}

	public void removeUser(User user, final ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.remove_user, new UrlParam(
				"id", user.getId())), resultListener);
	}

	public void addFolder(String name, String path,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.add_folder, new UrlParam(
				"name", name, UrlParam.Encoding.BASE64), new UrlParam("path",
				path, UrlParam.Encoding.BASE64)), resultListener);
	}

	public void editFolder(DirectoryInfo dir, String name, String path,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.update_folder,
				new UrlParam("id", dir.getId()), new UrlParam("name", name,
						UrlParam.Encoding.BASE64), new UrlParam("path", path,
						UrlParam.Encoding.BASE64)), resultListener);
	}

	public void removeFolder(DirectoryInfo dir, ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.remove_folder,
				new UrlParam("id", dir.getId())), resultListener);
	}

	private String getUrl(ConfigurationAction action, UrlParam... params) {
		return getUrl(action, Arrays.asList(params));
	}

	private String getUrl(ConfigurationAction action, List<UrlParam> parameters) {
		List<UrlParam> params = new ArrayList(parameters);
		params.add(new UrlParam("action", action.name()));
		return service.getUrl(RequestType.configuration, params);
	}

	public void getUserFolders(User user,
			final ResultListener<List<UserDirectory>> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directories");

		service.doRequest(getUrl(ConfigurationAction.get_user_folders,
				new UrlParam("user_id", user.getId())),
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
		List<UrlParam> params = new ArrayList();
		params.add(new UrlParam("user_id", user.getId()));
		params.add(new UrlParam("id", dir.getId()));
		if (name != null)
			params.add(new UrlParam("name", name, UrlParam.Encoding.BASE64));

		service.doRequest(getUrl(ConfigurationAction.add_user_folder, params),
				resultListener);
	}

	public void editUserFolder(User user, UserDirectory dir, String name,
			ResultListener resultListener) {
		List<UrlParam> params = new ArrayList();
		params.add(new UrlParam("user_id", user.getId()));
		params.add(new UrlParam("id", dir.getId()));
		if (name != null)
			params.add(new UrlParam("name", name, UrlParam.Encoding.BASE64));

		service.doRequest(
				getUrl(ConfigurationAction.update_user_folder, params),
				resultListener);
	}

	public void removeUserFolder(User user, UserDirectory dir,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.remove_user_folder,
				new UrlParam("user_id", user.getId()), new UrlParam("id", dir
						.getId())), resultListener);
	}
}
