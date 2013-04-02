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

import org.sjarvela.mollify.client.event.DefaultEventDispatcher;
import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.plugin.ClientInterface;
import org.sjarvela.mollify.client.plugin.DefaultClientInterface;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.SystemServiceProvider;
import org.sjarvela.mollify.client.service.UrlResolver;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.service.environment.php.PhpServiceEnvironment;
import org.sjarvela.mollify.client.service.request.DefaultResponseInterceptor;
import org.sjarvela.mollify.client.service.request.ResponseInterceptor;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.DefaultFileSystemItemProvider;
import org.sjarvela.mollify.client.session.DefaultSessionManager;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.SettingsProvider;
import org.sjarvela.mollify.client.ui.dialog.DefaultDialogManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.filesystem.DefaultFileSystemActionHandlerFactory;

import com.google.gwt.core.client.GWT;

public class MollifyClient implements Client {
	// private static Logger logger = Logger.getLogger(MollifyClient.class
	// .getName());
	static final String META_PROPERTY = "mollify:property";

	public static final String PROTOCOL_VERSION = "3";
	// private static final String PARAM_SHOW_LOGIN = "show-login";
	//
	// private static boolean pluginsInitialized = false;

	// private final ViewManager viewManager;
	private final DialogManager dialogManager;
	private final SessionManager sessionManager;
	// private final MainViewFactory mainViewFactory;
	// private final SessionService service;
	private final ClientSettings settings;
	private final ClientInterface client;
	private final ServiceProvider serviceProvider;
	private final TextProvider textProvider;

	public MollifyClient() {
		this.textProvider = new DefaultTextProvider();
		// this.viewManager = new DefaultViewManager();
		this.dialogManager = new DefaultDialogManager(textProvider);
		this.settings = new ClientSettings(new SettingsProvider(META_PROPERTY));

		EventDispatcher eventDispatcher = new DefaultEventDispatcher();
		this.sessionManager = new DefaultSessionManager(eventDispatcher);

		ServiceEnvironment env = new PhpServiceEnvironment();
		ResponseInterceptor responseInterceptor = new DefaultResponseInterceptor();
		env.initialize(createUrlResolver(), settings, responseInterceptor);

		FileSystemItemProvider fileSystemItemProvider = new DefaultFileSystemItemProvider(
				sessionManager, env);
		this.serviceProvider = new SystemServiceProvider(env, sessionManager);
		FileSystemActionHandlerFactory fileSystemActionHandlerFactory = new DefaultFileSystemActionHandlerFactory(
				eventDispatcher, textProvider, dialogManager, env,
				fileSystemItemProvider, sessionManager);
		// this.mainViewFactory = new DefaultMainViewFactory(eventDispatcher,
		// textProvider, viewManager, dialogManager, serviceProvider,
		// sessionManager, fileSystemItemProvider,
		// fileSystemActionHandlerFactory);
		this.client = new DefaultClientInterface(eventDispatcher,
				sessionManager, serviceProvider, dialogManager, textProvider,
				fileSystemActionHandlerFactory, fileSystemItemProvider);
		// this.service = serviceProvider.getSessionService();
		//
		// sessionManager.addSessionListener(new SessionListener() {
		// @Override
		// public void onSessionStarted(SessionInfo session) {
		// logger.log(Level.FINE, "Session started, authenticated: "
		// + session.isAuthenticated());
		// if (!session.isAuthenticated())
		// openLogin(session);
		// else
		// openMainView();
		// }
		//
		// @Override
		// public void onSessionEnded() {
		// start();
		// }
		// });
	}

	private UrlResolver createUrlResolver() {
		return new UrlResolver(GWT.getHostPageBaseURL(), GWT.getModuleBaseURL());
	}

	public void start() {
		client.setup();

		// logger.log(Level.INFO, "Starting Mollify, protocol version "
		// + PROTOCOL_VERSION);
		// logger.log(Level.INFO,
		// "Host page location: " + GWT.getHostPageBaseURL());
		// logger.log(Level.INFO, "Module name: " + GWT.getModuleName());
		// logger.log(Level.INFO, "Module location: " + GWT.getModuleBaseURL());
		//
		// if (settings.getBool("guest-mode", false)) {
		// logger.log(Level.INFO, "Guest mode enabled");
		// serviceProvider.setSessionId("guest");
		// }

		// service.getSessionInfo(MollifyClient.PROTOCOL_VERSION,
		// new ResultListener<SessionInfo>() {
		// public void onFail(ServiceError error) {
		// viewManager.showErrorInMainView(error.getError()
		// .getError(), error);
		// }
		//
		// public void onSuccess(final SessionInfo session) {
		// logger.log(Level.FINE,
		// "Session info received, plugins initialized "
		// + pluginsInitialized);
		//
		// if (!pluginsInitialized) {
		// client.setup(session, new Callback() {
		// @Override
		// public void onCallback() {
		// pluginsInitialized = true;
		// sessionManager.setSession(session);
		// }
		// });
		// } else {
		// sessionManager.setSession(session);
		// }
		// }
		// });
	}

	// private void openMainView() {
	// mainViewFactory.openMainView();
	// }
	//
	// private void openLogin(SessionInfo session) {
	// viewManager.empty();
	// if (!settings.getBool(PARAM_SHOW_LOGIN, true))
	// return;
	//
	// new LoginViewHandler(viewManager, dialogManager, service,
	// sessionManager);
	// }
}