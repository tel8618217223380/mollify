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
import org.sjarvela.mollify.client.service.request.ResultListener;

public class PhpSessionService implements SessionService {
	private final PhpService service;

	public PhpSessionService(PhpService service) {
		this.service = service;
	}

	public void authenticate(String userName, String password,
			ResultListener resultListener) {
		service.authenticate(userName, password, resultListener);
	}

	public void getSessionInfo(ResultListener resultListener) {
		service.getSessionInfo(resultListener);
	}

	public void logout(ResultListener resultListener) {
		service.logout(resultListener);
	}

}
