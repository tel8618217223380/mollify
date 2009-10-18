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

import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.LoginHandler;
import org.sjarvela.mollify.client.session.LogoutHandler;
import org.sjarvela.mollify.client.session.SessionHandler;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.DefaultViewManager;
import org.sjarvela.mollify.client.ui.ViewManager;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint, LogoutHandler {
	private static final String MOLLIFY_PANEL_ID = "mollify";
	private static final String PROTOCOL_VERSION = "1_0_0";

	static final String META_PROPERTY = "mollify:property";
	static final String PARAM_FLASH_UPLOADER = "enable-flash-file-uploader";

	private ViewManager viewManager;
	private Container container;
	private RootPanel panel;

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

		try {
			container = GWT.create(Container.class);
			((DefaultViewManager) container.getViewManager())
					.setRootPanel(panel);
		} catch (RuntimeException e) {
			showExceptionError("Error initializing application", e);
			return;
		}

		start();
	}

	private void start() {
		Log.info("Starting Mollify, protocol version " + PROTOCOL_VERSION);

		container.getEnvironment().getSessionService().getSessionInfo(
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						panel.clear();
						panel.add(new HTML(container.getTextProvider()
								.getStrings().infoDialogErrorTitle()
								+ ": "
								+ error.getType().getMessage(
										container.getTextProvider())));
					}

					public void onSuccess(SessionInfo session) {
						startSession(session);
					}
				});
	};

	private void startSession(SessionInfo session) {
		setSession(session);

		if (session.isAuthenticationRequired() && !session.getAuthenticated())
			showLogin();
		else
			showMain();
	}

	private void showLogin() {
		container.getDialogManager().openLoginDialog(new LoginHandler() {
			public void onLogin(String userName, String password,
					final ConfirmationListener listener) {
				Log.info("User login: " + userName);

				container.getEnvironment().getSessionService().authenticate(
						userName, password, PROTOCOL_VERSION,
						new ResultListener<SessionInfo>() {
							public void onFail(ServiceError error) {
								if (ServiceErrorType.AUTHENTICATION_FAILED
										.equals(error)) {
									showLoginError();
									return;
								}
								container.getDialogManager().showError(error);
							}

							public void onSuccess(SessionInfo session) {
								setSession(session);
								listener.onConfirm();
								showMain();
							}
						});
			}
		});
	}

	private void showMain() {
		container.getViewManager().openView(
				container.getMainViewFactory().createMainView(this)
						.getViewWidget());
	}

	public void onLogout(SessionInfo session) {
		Log.info("Logging out");

		container.getEnvironment().getSessionService().logout(
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						viewManager.empty();
						container.getDialogManager().showError(error);
					}

					public void onSuccess(SessionInfo session) {
						viewManager.empty();
						startSession(session);
					}
				});
	}

	private void showLoginError() {
		String title = container.getTextProvider().getStrings()
				.loginDialogTitle();
		String msg = container.getTextProvider().getStrings()
				.loginDialogLoginFailedMessage();
		container.getDialogManager().showInfo(title, msg);
	}

	private void setSession(SessionInfo session) {
		((SessionHandler) container.getSessionProvider()).setSession(session);
	}

	private void showExceptionError(String message, Throwable e) {
		GWT.log(message, e);
		Log.error(message, e);

		if (container != null)
			container.getDialogManager().showInfo("Mollify",
					"Unexpected error: " + e.getMessage());
		else
			e.printStackTrace();
	}

}
