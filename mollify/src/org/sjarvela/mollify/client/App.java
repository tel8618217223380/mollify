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

import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.LoginHandler;
import org.sjarvela.mollify.client.session.LogoutHandler;
import org.sjarvela.mollify.client.session.ParameterParser;
import org.sjarvela.mollify.client.session.SessionHandler;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;
import org.sjarvela.mollify.client.util.Browser;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint, LogoutHandler {
	private static final String META_PROPERTY = "mollify:property";
	private static final String MOLLIFY_PANEL_ID = "mollify";

	ServiceEnvironment environment;
	TextProvider textProvider;
	WindowManager windowManager;
	RootPanel panel;
	SessionHandler sessionInfoProvider;

	public void onModuleLoad() {
		Log.setUncaughtExceptionHandler();

		DeferredCommand.addCommand(new Command() {
			public void execute() {
				onLoad();
			}
		});
	}

	private void onLoad() {
		panel = RootPanel.get(MOLLIFY_PANEL_ID);
		if (panel == null)
			return;

		Log.debug("IE:" + Browser.isIE());

		ClientSettings settings = new ClientSettings(new ParameterParser(
				META_PROPERTY));

		try {
			environment = createEnvironment(settings);
			sessionInfoProvider = new SessionHandler();
			textProvider = DefaultTextProvider.getInstance();

			MainViewFactory mainViewFactory = new MainViewFactory(textProvider,
					environment);
			windowManager = new WindowManager(panel, textProvider,
					mainViewFactory, new DialogManager(textProvider, sessionInfoProvider));
		} catch (RuntimeException e) {
			showExceptionError("Error initializing: ", e);
			return;
		}

		start();
	}

	private ServiceEnvironment createEnvironment(ClientSettings settings) {
		ServiceEnvironment environment = (ServiceEnvironment) GWT
				.create(ServiceEnvironment.class);
		environment.initialize(settings);
		return environment;
	}

	private void start() {
		Log.info("Starting Mollify");

		environment.getSessionService().getSessionInfo(
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						windowManager.getDialogManager().showError(error);
					}

					public void onSuccess(SessionInfo result) {
						startSession(result);
					}
				});
	};

	private void startSession(SessionInfo info) {
		sessionInfoProvider.setSession(info);
		
		if (info.isAuthenticationRequired() && !info.getAuthenticated())
			showLogin();
		else
			showMain(info);
	}

	private void showLogin() {
		windowManager.getDialogManager().showLoginDialog(new LoginHandler() {
			public void onLogin(String userName, String password,
					final ConfirmationListener listener) {
				Log.info("User login: " + userName);

				environment.getSessionService().authenticate(userName,
						password, new ResultListener<SessionInfo>() {
							public void onFail(ServiceError error) {
								if (ServiceErrorType.AUTHENTICATION_FAILED
										.equals(error)) {
									showLoginError();
									return;
								}
								windowManager.getDialogManager().showError(
										error);
							}

							public void onSuccess(SessionInfo result) {
								listener.onConfirm();
								showMain(result);
							}
						});
			}
		});
	}

	private void showMain(SessionInfo info) {
		Log.info("Session started: " + info.asString());
		windowManager.showMainView(info, this);
	}

	public void onLogout(SessionInfo info) {
		Log.info("Logging out");

		environment.getSessionService().logout(
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						windowManager.empty();
						windowManager.getDialogManager().showError(error);
					}

					public void onSuccess(SessionInfo result) {
						windowManager.empty();
						startSession(result);
					}
				});
	}

	private void showLoginError() {
		String title = textProvider.getStrings().loginDialogTitle();
		String msg = textProvider.getStrings().loginDialogLoginFailedMessage();
		windowManager.getDialogManager().showInfo(title, msg);
	}

	private void showExceptionError(String message, Throwable e) {
		Log.error(message, e);
		windowManager.getDialogManager().showInfo("Mollify",
				"Unexpected error: " + e.getMessage());
	}

}
