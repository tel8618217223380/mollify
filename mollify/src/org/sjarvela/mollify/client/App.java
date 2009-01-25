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

import org.sjarvela.mollify.client.data.SessionInfo;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint, UncaughtExceptionHandler,
		LogoutListener {
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
		TextProvider textProvider = new DefaultTextProvider(localizator);

		service = new MollifyService(getParameter("mollify:property",
				"service_path"));

		MainViewFactory mainViewFactory = new MainViewFactory(localizator,
				textProvider, service);
		windowManager = new WindowManager(panel, localizator, mainViewFactory,
				new DialogManager(localizator));

		start();
	}

	private String getParameter(String meta, String propertyName) {
		String metaParam = getMetaParameter(meta);
		if (metaParam == null || metaParam.length() == 0)
			return null;

		int index = metaParam.toLowerCase().indexOf(
				propertyName.toLowerCase() + "=");
		if (index < 0)
			return null;
		index += propertyName.length() + 1;

		int split = metaParam.indexOf(';', index);
		if (split >= 0)
			return metaParam.substring(index, split);
		return metaParam.substring(index);
	}

	private native String getMetaParameter(String name) /*-{
		var metaArray = $doc.getElementsByTagName("meta");
		
		for (var i = 0; i < metaArray.length; i++) {
			if (metaArray[i].getAttribute("name") == name)
				return metaArray[i].getAttribute("content");
		}
		return null;
	}-*/;

	private void start() {
		service.getSessionInfo(new ResultListener() {
			public void onFail(ServiceError error) {
				windowManager.getDialogManager().showError(error);
			}

			public void onSuccess(Object... result) {
				startSession((SessionInfo) result[0]);
			}

		});
	};

	private void startSession(SessionInfo info) {
		if (info.isAuthenticationRequired() && !info.getAuthenticated())
			showLogin();
		else
			showMain(info);
	}

	private void showLogin() {
		windowManager.getDialogManager().showLoginDialog(new LoginHandler() {
			public void onLogin(String userName, String password,
					final ConfirmationListener listener) {
				service.authenticate(userName, password, new ResultListener() {

					public void onFail(ServiceError error) {
						if (ServiceError.AUTHENTICATION_FAILED.equals(error)) {
							showLoginError();
							return;
						}
						windowManager.getDialogManager().showError(error);
					}

					public void onSuccess(Object... result) {
						listener.onConfirm();
						showMain((SessionInfo) result[0]);
					}

				});
			}
		});
	}

	private void showMain(SessionInfo info) {
		windowManager.showMainView(info, this);
	}

	public void onLogout(SessionInfo info) {
		service.logout(new ResultListener() {

			public void onFail(ServiceError error) {
				windowManager.empty();
				windowManager.getDialogManager().showError(error);
			}

			public void onSuccess(Object... result) {
				windowManager.empty();
				startSession((SessionInfo) result[0]);
			}
		});

	}

	private void showLoginError() {
		String title = localizator.getStrings().loginDialogTitle();
		String msg = localizator.getStrings().loginDialogLoginFailedMessage();
		windowManager.getDialogManager().showInfo(title, msg);
	}

	public void onUncaughtException(Throwable e) {
		GWT.log("UNCAUGHT", e);
		windowManager.getDialogManager().showInfo("Unexpected error",
				e.getMessage());
	}

}
