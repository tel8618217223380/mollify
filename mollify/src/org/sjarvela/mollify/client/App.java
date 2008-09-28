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

import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.dialog.LoginHandler;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint, UncaughtExceptionHandler {
	private static final String MOLLIFY_PANEL_ID = "mollify";

	MollifyService service;
	Localizator localizator;
	WindowManager windowManager;
	RootPanel panel;

	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(this);

		panel = RootPanel.get(MOLLIFY_PANEL_ID);
		if (panel == null)
			return;

		localizator = Localizator.getInstance();
		service = new MollifyService();

		MainViewFactory mainViewFactory = new MainViewFactory(localizator,
				service);
		windowManager = new WindowManager(panel, localizator, mainViewFactory,
				new DialogManager(localizator));

		service.checkAuthentication(new ResultListener() {
			public void onFail(ServiceError error) {
				if (ServiceError.AUTHENTICATION_FAILED.equals(error)) {
					showLogin();
					return;
				}
				windowManager.getDialogManager().showError(error);
			}

			public void onSuccess(JavaScriptObject result) {
				showMain();
			}
		});
	};

	private void showLogin() {
		windowManager.getDialogManager().showLoginDialog(new LoginHandler() {
			public void onLogin(String userName, String password,
					final ConfirmationListener listener) {
				service.authenticate(userName, password, new ResultListener() {
					public void onFail(ServiceError error) {
						if (ServiceError.AUTHENTICATION_FAILED.equals(error)) {
							String title = localizator.getStrings()
									.loginDialogTitle();
							String msg = localizator.getStrings()
									.loginDialogLoginFailedMessage();
							windowManager.getDialogManager().showInfo(title,
									msg);
							return;
						}
						windowManager.getDialogManager().showError(error);
					}

					public void onSuccess(JavaScriptObject result) {
						listener.onConfirm();
						showMain();
					}
				});
			}
		});
	}

	private void showMain() {
		windowManager.showMainView();
	}

	public void onUncaughtException(Throwable e) {
		GWT.log("UNCAUGHT", e);
	}
}
