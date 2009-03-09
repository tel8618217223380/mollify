/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.demo;

import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.request.ResultListener;

public class DemoSessionService implements SessionService {
	private static final String VISIBLE_USERNAME = "Mollify Demo";

	private final DemoData data;

	public DemoSessionService(DemoData data) {
		this.data = data;
	}

	public void authenticate(String userName, String password,
			ResultListener resultListener) {
		resultListener.onSuccess(data.getSessionInfo(VISIBLE_USERNAME));
	}

	public void getSessionInfo(ResultListener resultListener) {
		resultListener.onSuccess(data.getSessionInfo(""));
	}

	public void logout(ResultListener resultListener) {
		resultListener.onSuccess(data.getSessionInfo(""));
	}

}
