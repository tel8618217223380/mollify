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
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.DefaultSessionManager;
import org.sjarvela.mollify.client.session.ParameterParser;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.DefaultDialogManager;
import org.sjarvela.mollify.client.ui.DefaultViewManager;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.flash.FlashFileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.http.HttpFileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.login.DefaultUiSessionManager;
import org.sjarvela.mollify.client.ui.login.UiSessionManager;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;
import org.sjarvela.mollify.client.ui.mainview.impl.DefaultMainViewFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ContainerConfiguration extends AbstractGinModule {
	static final String MOLLIFY_PANEL_ID = "mollify";
	static final String META_PROPERTY = "mollify:property";

	static final String PARAM_FILE_UPLOADER = "file-uploader";
	static final String VALUE_FILE_UPLOADER_FLASH = "flash";

	@Override
	protected void configure() {
		bind(SessionManager.class).to(DefaultSessionManager.class);
		bind(UiSessionManager.class).to(DefaultUiSessionManager.class);
		bind(TextProvider.class).to(DefaultTextProvider.class);
		bind(DialogManager.class).to(DefaultDialogManager.class);
		bind(MainViewFactory.class).to(DefaultMainViewFactory.class);
		bind(Client.class).to(MollifyClient.class);
	}

	@Provides
	SessionProvider getSessionProvider(SessionManager sessionManager) {
		return sessionManager;
	}

	@Provides
	@Singleton
	ClientSettings getClientSettings() {
		return new ClientSettings(new ParameterParser(META_PROPERTY));
	}

	@Provides
	@Singleton
	ServiceEnvironment getEnvironment(ClientSettings clientSettings) {
		ServiceEnvironment env = GWT.create(ServiceEnvironment.class);
		env.initialize(clientSettings);
		return env;
	}

	@Provides
	@Singleton
	FileUploadDialogFactory getFileUploadDialogFactory(ServiceEnvironment env,
			ClientSettings settings, TextProvider textProvider,
			SessionProvider sessionProvider) {
		if (VALUE_FILE_UPLOADER_FLASH.equalsIgnoreCase(settings
				.getString(PARAM_FILE_UPLOADER)))
			return new FlashFileUploadDialogFactory(textProvider, env
					.getFileUploadHandler(), sessionProvider.getSession()
					.getFileSystemInfo());

		return new HttpFileUploadDialogFactory(textProvider, env
				.getFileUploadHandler(), sessionProvider.getSession()
				.getFileSystemInfo());
	}

	@Provides
	@Singleton
	ViewManager getViewManager() {
		RootPanel panel = RootPanel.get(MOLLIFY_PANEL_ID);
		if (panel == null)
			throw new RuntimeException("No placeholder found for Mollify");
		return new DefaultViewManager(panel);
	}

}
