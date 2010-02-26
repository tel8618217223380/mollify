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
import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.plugin.DefaultPluginSystem;
import org.sjarvela.mollify.client.plugin.PluginSystem;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.SystemServiceProvider;
import org.sjarvela.mollify.client.service.UrlResolver;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.DefaultFileSystemItemProvider;
import org.sjarvela.mollify.client.session.DefaultSessionManager;
import org.sjarvela.mollify.client.session.ParameterParser;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.session.user.DefaultPasswordGenerator;
import org.sjarvela.mollify.client.session.user.PasswordGenerator;
import org.sjarvela.mollify.client.ui.DefaultViewManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DefaultDialogManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dnd.DefaultDragAndDropManager;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBoxFactory;
import org.sjarvela.mollify.client.ui.dropbox.impl.DefaultDropBoxFactory;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.flash.FlashFileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.http.HttpFileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.pluploader.PluploaderDialogFactory;
import org.sjarvela.mollify.client.ui.itemselector.DefaultItemSelectorFactory;
import org.sjarvela.mollify.client.ui.itemselector.ItemSelectorFactory;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;
import org.sjarvela.mollify.client.ui.mainview.impl.DefaultMainViewFactory;
import org.sjarvela.mollify.client.ui.password.DefaultPasswordDialogFactory;
import org.sjarvela.mollify.client.ui.password.PasswordDialogFactory;
import org.sjarvela.mollify.client.ui.permissions.DefaultPermissionEditorViewFactory;
import org.sjarvela.mollify.client.ui.permissions.PermissionEditorViewFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ContainerConfiguration extends AbstractGinModule {
	static final String META_PROPERTY = "mollify:property";

	static final String PARAM_FILE_UPLOADER = "file-uploader";
	static final String VALUE_FILE_UPLOADER_FLASH = "flash";
	static final String VALUE_FILE_UPLOADER_PLUPLOAD = "plupload";

	@Override
	protected void configure() {
		bind(TextProvider.class).to(DefaultTextProvider.class);
		bind(ViewManager.class).to(DefaultViewManager.class);
		bind(MainViewFactory.class).to(DefaultMainViewFactory.class);
		bind(DialogManager.class).to(DefaultDialogManager.class);
		bind(ItemSelectorFactory.class).to(DefaultItemSelectorFactory.class);
		bind(PasswordDialogFactory.class)
				.to(DefaultPasswordDialogFactory.class);
		bind(FileSystemItemProvider.class).to(
				DefaultFileSystemItemProvider.class);
		bind(PermissionEditorViewFactory.class).to(
				DefaultPermissionEditorViewFactory.class);
		bind(DropBoxFactory.class).to(DefaultDropBoxFactory.class);
		bind(SessionManager.class).to(DefaultSessionManager.class);
		bind(PasswordGenerator.class).to(DefaultPasswordGenerator.class);
		bind(EventDispatcher.class).to(DefaultEventDispatcher.class);
		bind(PluginSystem.class).to(DefaultPluginSystem.class);
		bind(Client.class).to(MollifyClient.class);
	}

	@Provides
	@Singleton
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
	UrlResolver getUrlResolver() {
		return new UrlResolver(GWT.getHostPageBaseURL(), GWT.getModuleBaseURL());
	}

	@Provides
	@Singleton
	ServiceEnvironment getEnvironment(UrlResolver urlResolver,
			ClientSettings clientSettings) {
		ServiceEnvironment env = GWT.create(ServiceEnvironment.class);
		env.initialize(urlResolver, clientSettings);
		return env;
	}

	@Provides
	@Singleton
	ServiceProvider getServiceProvider(ServiceEnvironment env,
			ViewManager viewManager, SessionManager sessionManager) {
		return new SystemServiceProvider(env, viewManager, sessionManager);
	}

	@Provides
	@Singleton
	DragAndDropManager getDragAndDropManager(ViewManager viewManager) {
		return new DefaultDragAndDropManager(viewManager.getRootPanel());
	}

	@Provides
	@Singleton
	FileUploadDialogFactory getFileUploadDialogFactory(ServiceEnvironment env,
			ClientSettings settings, TextProvider textProvider,
			UrlResolver urlResolver, SessionProvider sessionProvider,
			DialogManager dialogManager) {
		String param = settings.getString(PARAM_FILE_UPLOADER);

		if (VALUE_FILE_UPLOADER_FLASH.equalsIgnoreCase(param))
			return new FlashFileUploadDialogFactory(textProvider, urlResolver,
					env.getFileUploadService(), sessionProvider, settings);
		else if (VALUE_FILE_UPLOADER_PLUPLOAD.equalsIgnoreCase(param))
			return new PluploaderDialogFactory(textProvider, urlResolver, env
					.getFileUploadService(), sessionProvider, dialogManager,
					settings);

		return new HttpFileUploadDialogFactory(env, textProvider, env
				.getFileUploadService(), sessionProvider, dialogManager);
	}

}
