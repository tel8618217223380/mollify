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
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.util.MD5;

import com.allen_sauer.gwt.log.client.Log;

public class PhpSessionService implements SessionService {
	private final PhpService service;

	enum SessionAction {
		authenticate, session_info, logout, change_pw
	}

	public PhpSessionService(PhpService service) {
		this.service = service;
	}

	public void getSessionInfo(ResultListener resultListener) {
		service.doRequest(getUrl(SessionAction.session_info), resultListener);
	}

	public void authenticate(String userName, String password,
			final ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Authenticating '" + userName + "'");
		service.doRequest(getUrl(SessionAction.authenticate, Arrays.asList("username="
				+ userName, "password=" + MD5.generateMD5(password))),
				resultListener);
	}

	public void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Change password");
		service.doRequest(getUrl(SessionAction.change_pw, Arrays.asList("old="
				+ MD5.generateMD5(oldPassword), "new="
				+ MD5.generateMD5(newPassword))), resultListener);
	}

	public void logout(ResultListener<SessionInfo> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Logout");
		service.doRequest(getUrl(SessionAction.logout), resultListener);
	}

	private String getUrl(SessionAction action, String... params) {
		return getUrl(action, Arrays.asList(params));
	}

	private String getUrl(SessionAction action, List<String> parameters) {
		List<String> params = new ArrayList(parameters);
		params.add(0, "action=" + action.name());
		return service.getUrl(RequestType.session, params);
	}

}
