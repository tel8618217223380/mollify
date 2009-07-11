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

import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.UrlParam;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.user.User;

import com.allen_sauer.gwt.log.client.Log;

public class PhpSessionService implements SessionService {
	private final PhpService service;

	enum SessionAction {
		authenticate, session_info, logout, change_pw, reset_pw
	}

	public PhpSessionService(PhpService service) {
		this.service = service;
	}

	public void getSessionInfo(ResultListener resultListener) {
		service.doGetRequest(getUrl(SessionAction.session_info), resultListener);
	}

	public void authenticate(String userName, String password, String version,
			final ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Authenticating '" + userName + "'");
		service.doGetRequest(getUrl(SessionAction.authenticate, new UrlParam(
				"username", userName, UrlParam.Encoding.BASE64), new UrlParam(
				"password", password, UrlParam.Encoding.MD5), new UrlParam(
				"version", version)), resultListener);
	}

	public void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Change password");
		service.doGetRequest(getUrl(SessionAction.change_pw, new UrlParam("old",
				oldPassword, UrlParam.Encoding.MD5), new UrlParam("new",
				newPassword, UrlParam.Encoding.MD5)), resultListener);
	}

	public void resetPassword(User user, String password,
			ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Reset password for user " + user.getId());
		service.doGetRequest(getUrl(SessionAction.reset_pw, new UrlParam("id",
				user.getId()), new UrlParam("new", password,
				UrlParam.Encoding.MD5)), resultListener);
	}

	public void logout(ResultListener<SessionInfo> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Logout");
		service.doGetRequest(getUrl(SessionAction.logout), resultListener);
	}

	private String getUrl(SessionAction action, UrlParam... params) {
		return getUrl(action, Arrays.asList(params));
	}

	private String getUrl(SessionAction action, List<UrlParam> parameters) {
		List<UrlParam> params = new ArrayList(parameters);
		params.add(0, new UrlParam("action", action.name()));
		return service.getUrl(RequestType.session, params);
	}

}
