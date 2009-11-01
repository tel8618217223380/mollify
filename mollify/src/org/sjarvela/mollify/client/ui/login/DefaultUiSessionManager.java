/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.login;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.LoginHandler;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultUiSessionManager implements UiSessionManager {
	private final SessionManager sessionManager;
	private final DialogManager dialogManager;
	private final ServiceEnvironment env;
	private final TextProvider textProvider;

	private Callback logoutCallback = null;
	private Callback loginCallback = null;

	@Inject
	public DefaultUiSessionManager(SessionManager sessionManager,
			TextProvider textProvider, DialogManager dialogManager,
			ServiceEnvironment env) {
		this.sessionManager = sessionManager;
		this.textProvider = textProvider;
		this.dialogManager = dialogManager;
		this.env = env;
	}

	public void start(SessionInfo session, final Callback loginCallback,
			Callback logoutCallback) {
		this.loginCallback = loginCallback;
		this.logoutCallback = logoutCallback;
		setSession(session);

		if (!session.isAuthenticationRequired() || session.getAuthenticated()) {
			loginCallback.onCallback();
			return;
		}

		login();
	}

	private void login() {
		new LoginDialog(textProvider, new LoginHandler() {
			public void login(String userName, String password,
					final ConfirmationListener listener) {
				Log.info("User login: " + userName);

				env.getSessionService().authenticate(userName, password,
						new ResultListener<SessionInfo>() {
							public void onFail(ServiceError error) {
								if (ServiceErrorType.AUTHENTICATION_FAILED
										.equals(error)) {
									showLoginError();
									return;
								}
								dialogManager.showError(error);
							}

							public void onSuccess(SessionInfo session) {
								setSession(session);
								listener.onConfirm();
								loginCallback.onCallback();
							}
						});
			}
		});
	}

	public void logout() {
		Log.info("Logging out");

		env.getSessionService().logout(new ResultListener<SessionInfo>() {
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

			public void onSuccess(SessionInfo session) {
				setSession(session);
				if (logoutCallback != null)
					logoutCallback.onCallback();
			}
		});
	}

	private void showLoginError() {
		String title = textProvider.getStrings().loginDialogTitle();
		String msg = textProvider.getStrings().loginDialogLoginFailedMessage();
		dialogManager.showInfo(title, msg);
	}

	private void setSession(SessionInfo session) {
		sessionManager.setSession(session);
	}

}
