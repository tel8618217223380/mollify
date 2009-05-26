/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.User;

public interface SessionService {

	void getSessionInfo(ResultListener<SessionInfo> resultListener);

	void authenticate(String userName, String password,
			String version, ResultListener<SessionInfo> resultListener);

	void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener);

	void resetPassword(User user, String password, ResultListener resultListener);

	void logout(ResultListener<SessionInfo> resultListener);

}
