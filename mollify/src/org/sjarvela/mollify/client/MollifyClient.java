/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.login.UiSessionManager;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MollifyClient implements Client {
	public static final String PROTOCOL_VERSION = "1_0_0";

	private final ViewManager viewManager;
	private final ServiceEnvironment env;
	private final UiSessionManager sessionManager;
	private final MainViewFactory mainViewFactory;
	private final TextProvider textProvider;

	@Inject
	public MollifyClient(ViewManager viewManager, ServiceEnvironment env,
			TextProvider textProvider, UiSessionManager sessionManager,
			MainViewFactory mainViewFactory) {
		this.viewManager = viewManager;
		this.env = env;
		this.textProvider = textProvider;
		this.sessionManager = sessionManager;
		this.mainViewFactory = mainViewFactory;
	}

	public void connect() {
		Log.info("Starting Mollify, protocol version " + PROTOCOL_VERSION);

		env.getSessionService().getSessionInfo(PROTOCOL_VERSION,
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						showPlainError(error);
					}

					public void onSuccess(SessionInfo session) {
						onConnected(session);
					}
				});
	};

	protected void showPlainError(ServiceError error) {
		viewManager.showPlainError(textProvider.getStrings()
				.infoDialogErrorTitle()
				+ ": " + error.getType().getMessage(textProvider));
	}

	private void onConnected(SessionInfo session) {
		if (session.isAuthenticationRequired() && !session.getAuthenticated()) {
			sessionManager.login(new Callback() {
				public void onCallback() {
					showMain();
				}
			}, new Callback() {
				public void onCallback() {
					connect();
				}
			});
		} else {
			sessionManager.setSession(session);
			showMain();
		}
	}

	private void showMain() {
		viewManager.openView(mainViewFactory.createMainView(sessionManager)
				.getViewWidget());
	}
}
