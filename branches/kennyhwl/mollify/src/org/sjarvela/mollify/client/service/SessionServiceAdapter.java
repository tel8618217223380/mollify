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
import org.sjarvela.mollify.client.service.request.listener.ResultListenerFactory;
import org.sjarvela.mollify.client.session.SessionInfo;

public class SessionServiceAdapter implements SessionService {
	private final SessionService sessionService;
	private final ResultListenerFactory resultListenerFactory;

	public SessionServiceAdapter(SessionService sessionService,
			ResultListenerFactory resultListenerFactory) {
		this.sessionService = sessionService;
		this.resultListenerFactory = resultListenerFactory;
	}

	public void getSessionInfo(String protocolVersion,
			final ResultListener<SessionInfo> resultListener) {
		sessionService.getSessionInfo(protocolVersion, resultListenerFactory
				.createListener(resultListener));
	}

	public void authenticate(String userName, String password,
			String protocolVersion, ResultListener<SessionInfo> resultListener) {
		sessionService.authenticate(userName, password, protocolVersion,
				resultListenerFactory.createListener(resultListener));
	}

	public void logout(ResultListener resultListener) {
		sessionService.logout(resultListenerFactory
				.createListener(resultListener));
	}

}
