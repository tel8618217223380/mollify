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
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.LoginHandler;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionListener;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.login.LoginDialog;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MollifyClient implements Client, SessionListener {
	public static final String PROTOCOL_VERSION = "1_5_0";

	private final ViewManager viewManager;
	private final DialogManager dialogManager;
	private final MainViewFactory mainViewFactory;
	private final SessionManager sessionManager;
	private final SessionService service;
	private final TextProvider textProvider;

	@Inject
	public MollifyClient(ViewManager viewManager, DialogManager dialogManager,
			MainViewFactory mainViewFactory, SessionManager sessionManager,
			ServiceProvider serviceProvider, TextProvider textProvider) {
		this.viewManager = viewManager;
		this.dialogManager = dialogManager;
		this.mainViewFactory = mainViewFactory;
		this.sessionManager = sessionManager;
		this.textProvider = textProvider;
		this.service = serviceProvider.getSessionService();

		sessionManager.addSessionListener(this);
	}

	public void start() {
		Log.info("Starting Mollify, protocol version " + PROTOCOL_VERSION);
		Log.debug("Host page location: " + GWT.getHostPageBaseURL());
		Log.debug("Module name: " + GWT.getModuleName());
		Log.debug("Module location: " + GWT.getModuleBaseURL());

		viewManager.empty();

		service.getSessionInfo(MollifyClient.PROTOCOL_VERSION,
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						viewManager.showServiceError(error.getError()
								.getError(), error);
					}

					public void onSuccess(SessionInfo session) {
						sessionManager.setSession(session);
					}
				});
	}

	public void onSessionStarted(SessionInfo session) {
		if (!session.isAuthenticationRequired() || !session.isAuthenticated())
			openLogin();
		else
			viewManager.openView(mainViewFactory.createMainView()
					.getViewWidget());
	}

	public void onSessionEnded() {
		service.logout(new ResultListener<Boolean>() {
			public void onFail(ServiceError error) {
				start();
			}

			public void onSuccess(Boolean b) {
				start();
			}
		});
	}

	private void openLogin() {
		new LoginDialog(textProvider, new LoginHandler() {
			public void login(String userName, String password,
					final ConfirmationListener listener) {
				Log.info("User login: " + userName);

				service.authenticate(userName, password,
						MollifyClient.PROTOCOL_VERSION,
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
								listener.onConfirm();
								sessionManager.setSession(session);
							}
						});
			}
		});
	}

	private void showLoginError() {
		String title = textProvider.getStrings().loginDialogTitle();
		String msg = textProvider.getStrings().loginDialogLoginFailedMessage();
		dialogManager.showInfo(title, msg);
	}

}
