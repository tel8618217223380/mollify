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
import org.sjarvela.mollify.client.log.DefaultLogger;
import org.sjarvela.mollify.client.log.HtmlLogger;
import org.sjarvela.mollify.client.log.MollifyLogger;
import org.sjarvela.mollify.client.service.MollifyError;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint, UncaughtExceptionHandler,
		LogoutListener {
	private static final String META_PROPERTY = "mollify:property";
	private static final String NONE = "none";

	private static final String THEME_PATH = "themes/";
	private static final String DEFAULT_THEME = "basic";
	private static final String THEME_CSS = "/style.css";

	private static final String PARAM_THEME = "theme";
	private static final String PARAM_SERVICE_PATH = "service-path";

	private static final String MOLLIFY_PANEL_ID = "mollify";

	MollifyService service;
	Localizator localizator;
	WindowManager windowManager;
	RootPanel panel;
	private MollifyLogger logger;

	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(this);

		panel = RootPanel.get(MOLLIFY_PANEL_ID);
		if (panel == null)
			return;

		ParameterParser parser = new ParameterParser(META_PROPERTY);
		try {
			Settings settings = Settings.create(parser);
			logger = createLogger(settings);

			importTheme(parser.getParameter(PARAM_THEME));
			service = new MollifyService(logger, parser
					.getParameter(PARAM_SERVICE_PATH));
			localizator = Localizator.getInstance();

			MainViewFactory mainViewFactory = new MainViewFactory(localizator,
					new DefaultTextProvider(localizator), service);
			windowManager = new WindowManager(panel, localizator,
					mainViewFactory, new DialogManager(localizator));
		} catch (RuntimeException e) {
			showExceptionError("Error initializing: ", e);
			return;
		}

		start();
	}

	private MollifyLogger createLogger(Settings settings) {
		if (settings.isDebug())
			return new HtmlLogger(panel);
		return new DefaultLogger();
	}

	private void importTheme(String theme) {
		if (theme != null && theme.toLowerCase().equals(NONE)) {
			logger.logInfo("Theme import disabled");
			return;
		}

		String themeUrl = GWT.getModuleBaseURL() + THEME_PATH;
		if (theme == null) {
			themeUrl += DEFAULT_THEME;
		} else {
			if (theme.toLowerCase().startsWith("http")
					|| theme.indexOf("/") >= 0) {
				throw new RuntimeException(
						"Invalid theme setting, no path allowed");
			}

			themeUrl += theme;
		}
		themeUrl += THEME_CSS;

		if (theme == null)
			logger.logInfo("Importing default theme (" + themeUrl + ")");
		else
			logger
					.logInfo("Importing theme '" + theme + "' (" + themeUrl
							+ ")");

		importCss(themeUrl);
	}

	private native String importCss(String css) /*-{
		var cssNode = document.createElement('link');
		cssNode.rel = 'stylesheet';
		cssNode.href = css;
		$doc.getElementsByTagName("head")[0].appendChild(cssNode);
	}-*/;

	private void start() {
		logger.logInfo("Starting Mollify");

		service.getSessionInfo(new ResultListener() {
			public void onFail(MollifyError error) {
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
				logger.logInfo("User login: " + userName);

				service.authenticate(userName, password, new ResultListener() {
					public void onFail(MollifyError error) {
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
		logger.logInfo("Session started: " + info.asString());
		windowManager.showMainView(info, this);
	}

	public void onLogout(SessionInfo info) {
		logger.logInfo("Logging out");

		service.logout(new ResultListener() {
			public void onFail(MollifyError error) {
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
		panel.clear();
		showExceptionError("Unexpected error: ", e);
	}

	private void showExceptionError(String message, Throwable e) {
		if (logger != null)
			logger.logError(message + e.getMessage());

		panel.add(new HTML(message + e.getMessage()));
		panel.add(new HTML(e.toString()));

		for (StackTraceElement ste : e.getStackTrace())
			panel.add(new HTML(ste.toString()));
	}

}
