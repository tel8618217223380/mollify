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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.UserFolder;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.JSONStringBuilder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserGroup;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;
import org.sjarvela.mollify.client.session.user.UsersAndGroups;
import org.sjarvela.mollify.client.util.JsUtil;
import org.sjarvela.mollify.client.util.MD5;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;

public class PhpConfigurationService extends ServiceBase implements
		ConfigurationService {
	enum ConfigurationAction implements ActionId {
		users, usersgroups, password, folders, userfolders
	}

	public PhpConfigurationService(PhpService service) {
		super(service, RequestType.configuration);
	}

	public void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Change password");

		String data = new JSONStringBuilder("old", MD5.generate(oldPassword))
				.add("new", MD5.generate(newPassword)).toString();

		request().url(
				serviceUrl().item("users").item("current").action(
						ConfigurationAction.password)).data(data).listener(
				resultListener).put();
	}

	public void getUsersAndGroups(
			final ResultListener<UsersAndGroups> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get users");

		ResultListener<JavaScriptObject> listener = new ResultListener<JavaScriptObject>() {
			public void onFail(ServiceError error) {
				resultListener.onFail(error);
			}

			public void onSuccess(JavaScriptObject result) {
				JSONObject o = new JSONObject(result);
				JsArray<User> users = (JsArray<User>) o.get("users").isArray()
						.getJavaScriptObject();
				JsArray<UserGroup> groups = (JsArray<UserGroup>) o
						.get("groups").isArray().getJavaScriptObject();
				resultListener.onSuccess(new UsersAndGroups(JsUtil.asList(
						users, User.class), JsUtil.asList(groups,
						UserGroup.class)));
			}
		};

		request().url(serviceUrl().action(ConfigurationAction.usersgroups))
				.listener(listener).get();
	}

	public void getFolders(final ResultListener<List<FolderInfo>> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directories");

		ResultListener<JsArray<FolderInfo>> listener = new ResultListener<JsArray<FolderInfo>>() {
			public void onFail(ServiceError error) {
				resultListener.onFail(error);
			}

			public void onSuccess(JsArray<FolderInfo> result) {
				resultListener.onSuccess(JsUtil
						.asList(result, FolderInfo.class));
			}
		};

		request().url(serviceUrl().action(ConfigurationAction.folders))
				.listener(listener).get();
	}

	public void addUser(String name, String password, UserPermissionMode mode,
			ResultListener resultListener) {
		String data = new JSONStringBuilder("name", name).add("password",
				MD5.generate(password)).add("permission_mode",
				mode.getStringValue()).toString();

		request().url(serviceUrl().action(ConfigurationAction.users))
				.data(data).listener(resultListener).post();
	}

	public void editUser(User user, String name, UserPermissionMode mode,
			ResultListener resultListener) {
		String data = new JSONStringBuilder("name", name).add(
				"permission_mode", mode.getStringValue()).toString();

		request().url(
				serviceUrl().action(ConfigurationAction.users).item(
						user.getId())).data(data).listener(resultListener)
				.put();
	}

	public void removeUser(User user, final ResultListener resultListener) {
		request().url(
				serviceUrl().action(ConfigurationAction.users).item(
						user.getId())).listener(resultListener).delete();
	}

	public void addFolder(String name, String path,
			ResultListener resultListener) {
		String data = new JSONStringBuilder("name", name).add("path", path)
				.toString();

		request().url(serviceUrl().action(ConfigurationAction.folders)).data(
				data).listener(resultListener).post();
	}

	public void editFolder(FolderInfo dir, String name, String path,
			ResultListener resultListener) {
		String data = new JSONStringBuilder("name", name).add("path", path)
				.toString();

		request().url(
				serviceUrl().action(ConfigurationAction.folders).item(
						dir.getId())).data(data).listener(resultListener).put();
	}

	public void removeFolder(FolderInfo dir, ResultListener resultListener) {
		request().url(
				serviceUrl().action(ConfigurationAction.folders).item(
						dir.getId())).listener(resultListener).delete();
	}

	public void getUserFolders(User user,
			final ResultListener<List<UserFolder>> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Get directories");

		ResultListener<JsArray<UserFolder>> listener = new ResultListener<JsArray<UserFolder>>() {
			public void onFail(ServiceError error) {
				resultListener.onFail(error);
			}

			public void onSuccess(JsArray<UserFolder> result) {
				resultListener.onSuccess(JsUtil
						.asList(result, UserFolder.class));
			}
		};
		request().url(
				serviceUrl().action(ConfigurationAction.userfolders).item(
						user.getId())).listener(listener).get();
	}

	public void addUserFolder(User user, FolderInfo dir, String name,
			ResultListener resultListener) {
		String data = new JSONStringBuilder("id", dir.getId())
				.add("name", name).toString();

		request().url(
				serviceUrl().action(ConfigurationAction.userfolders).item(
						user.getId())).data(data).listener(resultListener)
				.post();
	}

	public void editUserFolder(User user, UserFolder dir, String name,
			ResultListener resultListener) {
		String data = new JSONStringBuilder("name", name).toString();

		request().url(
				serviceUrl().action(ConfigurationAction.userfolders).item(
						user.getId()).item(dir.getId())).data(data).listener(
				resultListener).put();
	}

	public void removeUserFolder(User user, UserFolder dir,
			ResultListener resultListener) {
		request().url(
				serviceUrl().action(ConfigurationAction.userfolders).item(
						user.getId()).item(dir.getId())).listener(
				resultListener).delete();
	}

}
