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
import org.sjarvela.mollify.client.session.user.User;

public class SessionServiceAdapter implements SessionService {
	private final SessionService sessionService;
	private final AdapterListenerCreator adapterListenerCreator;

	public SessionServiceAdapter(SessionService sessionService,
			AdapterListenerCreator adapterListenerCreator) {
		this.sessionService = sessionService;
		this.adapterListenerCreator = adapterListenerCreator;
	}

	public void getSessionInfo(String protocolVersion,
			final ResultListener<SessionInfo> resultListener) {
		sessionService.getSessionInfo(protocolVersion, adapterListenerCreator
				.createAdapterListener(resultListener));
	}

	public void authenticate(String userName, String password,
			ResultListener<SessionInfo> resultListener) {
		sessionService.authenticate(userName, password, adapterListenerCreator
				.createAdapterListener(resultListener));
	}

	public void changePassword(String oldPassword, String newPassword,
			ResultListener<Boolean> resultListener) {
		sessionService.changePassword(oldPassword, newPassword,
				adapterListenerCreator.createAdapterListener(resultListener));
	}

	public void logout(ResultListener<SessionInfo> resultListener) {
		sessionService.logout(adapterListenerCreator
				.createAdapterListener(resultListener));
	}

	public void resetPassword(User user, String password,
			ResultListener resultListener) {
		sessionService.resetPassword(user, password, adapterListenerCreator
				.createAdapterListener(resultListener));
	}

}
