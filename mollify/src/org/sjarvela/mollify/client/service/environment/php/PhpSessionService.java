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

import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.UrlParam;
import org.sjarvela.mollify.client.service.request.data.JSONStringBuilder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.util.MD5;

import com.allen_sauer.gwt.log.client.Log;

public class PhpSessionService extends ServiceBase implements SessionService {
	enum SessionAction implements ActionId {
		authenticate, info, logout, change_pw, reset_pw
	}

	public PhpSessionService(PhpService service) {
		super(service, RequestType.session);
	}

	public void getSessionInfo(String protocolVersion,
			ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Requesting session info (protocol version '"
					+ protocolVersion + "')");

		service.doGetRequest(getUrl(SessionAction.info, protocolVersion),
				resultListener);
	}

	public void authenticate(String userName, String password,
			String protocolVersion, final ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Authenticating '" + userName + "'");

		String data = new JSONStringBuilder("username", userName).add(
				"password", MD5.generate(password)).add("protocol_version",
				protocolVersion).toString();
		service.doPostRequest(getUrl(SessionAction.authenticate), data,
				resultListener);
	}

	public void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Change password");

		service.doGetRequest(getUrl(SessionAction.change_pw, new UrlParam(
				"old", oldPassword, UrlParam.Encoding.MD5), new UrlParam("new",
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

	public void logout(ResultListener resultListener) {
		if (Log.isDebugEnabled())
			Log.debug("Logout");

		service.doPostRequest(getUrl(SessionAction.logout), resultListener);
	}

}
