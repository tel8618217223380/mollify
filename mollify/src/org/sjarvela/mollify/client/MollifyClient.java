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

import org.sjarvela.mollify.client.plugin.PluginSystem;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionListener;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.login.LoginViewHandler;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MollifyClient implements Client {
	private static Logger logger = Logger.getLogger(MollifyClient.class
			.getName());

	public static final String PROTOCOL_VERSION = "3";
	private static final String PARAM_SHOW_LOGIN = "show-login";

	private static boolean pluginsInitialized = false;

	private final ViewManager viewManager;
	private final DialogManager dialogManager;
	private final SessionManager sessionManager;
	private final MainViewFactory mainViewFactory;
	private final SessionService service;
	private final ClientSettings settings;
	private final PluginSystem pluginSystem;

	@Inject
	public MollifyClient(ViewManager viewManager, DialogManager dialogManager,
			MainViewFactory mainViewFactory, SessionManager sessionManager,
			ServiceProvider serviceProvider, ClientSettings settings,
			PluginSystem pluginSystem) {
		this.viewManager = viewManager;
		this.dialogManager = dialogManager;
		this.mainViewFactory = mainViewFactory;
		this.sessionManager = sessionManager;
		this.settings = settings;
		this.pluginSystem = pluginSystem;
		this.service = serviceProvider.getSessionService();

		sessionManager.addSessionListener(new SessionListener() {
			@Override
			public void onSessionStarted(SessionInfo session) {
				logger.log(Level.FINE, "Session started, authenticated: "
						+ session.isAuthenticated());
				if (session.isAuthenticationRequired()
						&& !session.isAuthenticated())
					openLogin(session);
				else
					openMainView();
			}

			@Override
			public void onSessionEnded() {
				start();
			}
		});
	}

	public void start() {
		logger.log(Level.INFO, "Starting Mollify, protocol version "
				+ PROTOCOL_VERSION);
		logger.log(Level.INFO,
				"Host page location: " + GWT.getHostPageBaseURL());
		logger.log(Level.INFO, "Module name: " + GWT.getModuleName());
		logger.log(Level.INFO, "Module location: " + GWT.getModuleBaseURL());

		service.getSessionInfo(MollifyClient.PROTOCOL_VERSION,
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						viewManager.showErrorInMainView(error.getError()
								.getError(), error);
					}

					public void onSuccess(final SessionInfo session) {
						logger.log(Level.FINE,
								"Session info received, plugins initialized "
										+ pluginsInitialized);

						if (!pluginsInitialized) {
							pluginSystem.setup(session, new Callback() {
								@Override
								public void onCallback() {
									pluginsInitialized = true;
									sessionManager.setSession(session);
								}
							});
						} else {
							sessionManager.setSession(session);
						}
					}
				});
	}

	private void openMainView() {
		mainViewFactory.openMainView();
	}

	private void openLogin(SessionInfo session) {
		viewManager.empty();
		if (!settings.getBool(PARAM_SHOW_LOGIN, true))
			return;

		new LoginViewHandler(viewManager, dialogManager, service,
				sessionManager);
	}
}