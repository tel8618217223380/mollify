/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.demo;

import org.sjarvela.mollify.client.request.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.SessionService;

public class DemoSessionService implements SessionService {
	private static final String USERNAME = "demo";
	private static final String PASSWORD = "demo";
	private static final String VISIBLE_USERNAME = "Mollify Demo";

	private final DemoData data;

	public DemoSessionService(DemoData data) {
		this.data = data;
	}

	public void authenticate(String userName, String password,
			ResultListener resultListener) {
		if (userName.equals(USERNAME) && password.equals(PASSWORD))
			resultListener.onSuccess(data.getSessionInfo(VISIBLE_USERNAME));
		else
			resultListener.onFail(new ServiceError(
					ServiceErrorType.AUTHENTICATION_FAILED));
	}

	public void getSessionInfo(ResultListener resultListener) {
		resultListener.onSuccess(data.getSessionInfo(""));
	}

	public void logout(ResultListener resultListener) {
		resultListener.onSuccess(true);
	}

}
