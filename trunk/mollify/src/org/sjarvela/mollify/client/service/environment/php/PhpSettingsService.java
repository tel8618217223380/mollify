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
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.util.JsUtil;
import org.sjarvela.mollify.client.util.MD5;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.URL;

public class PhpSettingsService implements SettingsService {
	private final PhpService service;

	enum ConfigurationAction {
		get_users, add_user, edit_user, remove_user
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

	public void getFolders(ResultListener<List<DirectoryInfo>> resultListener) {
		// TODO Auto-generated method stub

	}

	public void addUser(String name, String password, PermissionMode mode,
			ResultListener<Boolean> resultListener) {
		service.doRequest(getUrl(ConfigurationAction.add_user, "name="
				+ URL.encode(name), "password=" + MD5.generateMD5(password),
				"permission_mode=" + mode.getStringValue()), resultListener);
	}

	public void editUser(User user, String name, PermissionMode mode,
			ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.edit_user, "id="
				+ URL.encode(user.getId()), "name=" + URL.encode(name),
				"permission_mode=" + mode.getStringValue()), resultListener);
	}

	public void removeUser(User user, ResultListener resultListener) {
		service.doRequest(getUrl(ConfigurationAction.remove_user, "id="
				+ URL.encode(user.getId())), resultListener);
	}

	private String getUrl(ConfigurationAction action, String... params) {
		return getUrl(action, Arrays.asList(params));
	}

	private String getUrl(ConfigurationAction action, List<String> parameters) {
		List<String> params = new ArrayList(parameters);
		params.add("action=" + action.name());
		return service.getUrl(RequestType.configuration, params);
	}
}
