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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.plugin.PluginSystem;
import org.sjarvela.mollify.client.service.ConfirmationListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.LoginHandler;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionListener;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.login.LoginDialog;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MollifyClient implements Client, SessionListener {
	private static Logger logger = Logger.getLogger(MollifyClient.class
			.getName());

	public static final String PROTOCOL_VERSION = "1_5_0";
	private static final String PARAM_SHOW_LOGIN = "show-login";

	private final ViewManager viewManager;
	private final DialogManager dialogManager;
	private final MainViewFactory mainViewFactory;
	private final SessionManager sessionManager;
	private final SessionService service;
	private final TextProvider textProvider;
	private final ClientSettings settings;
	private final ServiceProvider serviceProvider;

	@Inject
	public MollifyClient(ViewManager viewManager, DialogManager dialogManager,
			MainViewFactory mainViewFactory, SessionManager sessionManager,
			ServiceProvider serviceProvider, TextProvider textProvider,
			ClientSettings settings, PluginSystem pluginSystem) {
		this.viewManager = viewManager;
		this.dialogManager = dialogManager;
		this.mainViewFactory = mainViewFactory;
		this.sessionManager = sessionManager;
		this.serviceProvider = serviceProvider;
		this.textProvider = textProvider;
		this.settings = settings;
		this.service = serviceProvider.getSessionService();

		sessionManager.addSessionListener(this);
		pluginSystem.setup();
	}

	public void start() {
		logger.log(Level.INFO, "Starting Mollify, protocol version "
				+ PROTOCOL_VERSION);
		logger.log(Level.INFO,
				"Host page location: " + GWT.getHostPageBaseURL());
		logger.log(Level.INFO, "Module name: " + GWT.getModuleName());
		logger.log(Level.INFO, "Module location: " + GWT.getModuleBaseURL());

		viewManager.empty();

		service.getSessionInfo(MollifyClient.PROTOCOL_VERSION,
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						viewManager.showErrorInMainView(error.getError()
								.getError(), error);
					}

					public void onSuccess(SessionInfo session) {
						sessionManager.setSession(session);
					}
				});
	}

	public void onSessionStarted(SessionInfo session) {
		if (session.isAuthenticationRequired() && !session.isAuthenticated())
			openLogin(session);
		else
			viewManager.openView(mainViewFactory.createMainView()
					.getViewWidget());
	}

	public void onSessionEnded() {
		start();
	}

	private void openLogin(SessionInfo session) {
		if (!settings.getBool(PARAM_SHOW_LOGIN, true)) {
			viewManager.empty();
			return;
		}

		new LoginDialog(textProvider, dialogManager, new LoginHandler() {
			public void login(String userName, String password,
					final ConfirmationListener listener) {
				logger.log(Level.INFO, "User login: " + userName);

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
		}, serviceProvider, session.getFeatures().lostPassword());
	}

	private void showLoginError() {
		String title = textProvider.getStrings().loginDialogTitle();
		String msg = textProvider.getStrings().loginDialogLoginFailedMessage();
		dialogManager.showInfo(title, msg);
	}

}
