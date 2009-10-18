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
import org.sjarvela.mollify.client.session.ParameterParser;
import org.sjarvela.mollify.client.session.SessionHandler;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.DefaultDialogManager;
import org.sjarvela.mollify.client.ui.DefaultViewManager;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.flash.FlashFileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.http.HttpFileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;
import org.sjarvela.mollify.client.ui.mainview.impl.DefaultMainViewFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;

public class ContainerConfiguration extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(TextProvider.class).to(DefaultTextProvider.class);
		bind(SessionProvider.class).to(SessionHandler.class);
		bind(DialogManager.class).to(DefaultDialogManager.class);
		bind(ViewManager.class).to(DefaultViewManager.class);
		bind(MainViewFactory.class).to(DefaultMainViewFactory.class);
	}

	@Provides
	ClientSettings getClientSettings() {
		return new ClientSettings(new ParameterParser(App.META_PROPERTY));
	}

	@Provides
	ServiceEnvironment getEnvironment(ClientSettings clientSettings) {
		ServiceEnvironment env = GWT.create(ServiceEnvironment.class);
		env.initialize(clientSettings);
		return env;
	}

	@Provides
	FileUploadDialogFactory getFileUploadDialogFactory(ServiceEnvironment env,
			ClientSettings settings, TextProvider textProvider,
			SessionProvider sessionProvider) {
		if (settings.getBool(App.PARAM_FLASH_UPLOADER, false))
			return new FlashFileUploadDialogFactory(textProvider, env
					.getFileUploadHandler(), sessionProvider.getSession()
					.getFileSystemInfo());

		return new HttpFileUploadDialogFactory(textProvider, env
				.getFileUploadHandler(), sessionProvider.getSession()
				.getFileSystemInfo());
	}

}
