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
import org.sjarvela.mollify.client.filesystem.provider.ItemDetailsProvider;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadFactory;
import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.plugin.DefaultClientInterface;
import org.sjarvela.mollify.client.plugin.DefaultPluginSystem;
import org.sjarvela.mollify.client.plugin.ClientInterface;
import org.sjarvela.mollify.client.plugin.PluginSystem;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.SystemServiceProvider;
import org.sjarvela.mollify.client.service.UrlResolver;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.service.request.DefaultResponseInterceptor;
import org.sjarvela.mollify.client.service.request.ResponseInterceptor;
import org.sjarvela.mollify.client.service.request.ResponseProcessor;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.DefaultFileSystemItemProvider;
import org.sjarvela.mollify.client.session.DefaultSessionManager;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.session.SettingsProvider;
import org.sjarvela.mollify.client.session.user.DefaultPasswordGenerator;
import org.sjarvela.mollify.client.session.user.PasswordGenerator;
import org.sjarvela.mollify.client.ui.DefaultViewManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DefaultDialogManager;
import org.sjarvela.mollify.client.ui.dialog.DefaultRenameDialogFactory;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dialog.RenameDialogFactory;
import org.sjarvela.mollify.client.ui.dnd.DefaultDragAndDropManager;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBoxFactory;
import org.sjarvela.mollify.client.ui.dropbox.impl.DefaultDropBoxFactory;
import org.sjarvela.mollify.client.ui.editor.FileEditorFactory;
import org.sjarvela.mollify.client.ui.editor.impl.DefaultFileEditorFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.DefaultItemContextProvider;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.DefaultItemContextPopupFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.ItemContextPopupFactory;
import org.sjarvela.mollify.client.ui.filesystem.DefaultFileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.flash.FlashFileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.http.HttpFileUploadDialogFactory;
import org.sjarvela.mollify.client.ui.formatter.PathFormatter;
import org.sjarvela.mollify.client.ui.formatter.impl.DefaultPathFormatter;
import org.sjarvela.mollify.client.ui.itemselector.DefaultItemSelectorFactory;
import org.sjarvela.mollify.client.ui.itemselector.ItemSelectorFactory;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;
import org.sjarvela.mollify.client.ui.mainview.impl.DefaultMainViewFactory;
import org.sjarvela.mollify.client.ui.password.DefaultPasswordDialogFactory;
import org.sjarvela.mollify.client.ui.password.PasswordDialogFactory;
import org.sjarvela.mollify.client.ui.permissions.DefaultPermissionEditorViewFactory;
import org.sjarvela.mollify.client.ui.permissions.PermissionEditorViewFactory;
import org.sjarvela.mollify.client.ui.searchresult.SearchResultDialogFactory;
import org.sjarvela.mollify.client.ui.searchresult.impl.DefaultSearchResultDialogFactory;
import org.sjarvela.mollify.client.ui.viewer.FileViewerFactory;
import org.sjarvela.mollify.client.ui.viewer.impl.DefaultFileViewerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class ContainerConfiguration extends AbstractGinModule {
	static final String META_PROPERTY = "mollify:property";

	static final String PARAM_FILE_UPLOADER = "file-uploader";
	static final String VALUE_FILE_UPLOADER_FLASH = "flash";

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
		bind(FileViewerFactory.class).to(DefaultFileViewerFactory.class);
		bind(FileEditorFactory.class).to(DefaultFileEditorFactory.class);
		bind(DropBoxFactory.class).to(DefaultDropBoxFactory.class);
		bind(SessionManager.class).to(DefaultSessionManager.class);
		bind(PasswordGenerator.class).to(DefaultPasswordGenerator.class);
		bind(EventDispatcher.class).to(DefaultEventDispatcher.class);
		bind(PluginSystem.class).to(DefaultPluginSystem.class);
		bind(Client.class).to(MollifyClient.class);
		bind(ResponseInterceptor.class).to(DefaultResponseInterceptor.class);
		bind(ClientInterface.class).to(DefaultClientInterface.class);
		bind(ItemContextHandler.class).to(DefaultItemContextProvider.class);
		bind(SearchResultDialogFactory.class).to(
				DefaultSearchResultDialogFactory.class);
		bind(PathFormatter.class).to(DefaultPathFormatter.class);
		bind(FileSystemActionHandlerFactory.class).to(
				DefaultFileSystemActionHandlerFactory.class);
		bind(ItemContextPopupFactory.class).to(
				DefaultItemContextPopupFactory.class);
		bind(RenameDialogFactory.class).to(DefaultRenameDialogFactory.class);
	}

	@Provides
	@Singleton
	ResponseProcessor getResponseProcessor(
			ResponseInterceptor responseInterceptor) {
		return responseInterceptor;
	}

	@Provides
	@Singleton
	SessionProvider getSessionProvider(SessionManager sessionManager) {
		return sessionManager;
	}

	@Provides
	@Singleton
	ClientSettings getClientSettings() {
		return new ClientSettings(new SettingsProvider(META_PROPERTY));
	}

	@Provides
	@Singleton
	UrlResolver getUrlResolver() {
		return new UrlResolver(GWT.getHostPageBaseURL(), GWT.getModuleBaseURL());
	}

	@Provides
	@Singleton
	ItemDetailsProvider getItemDetailsProvider(ServiceEnvironment env) {
		return env.getFileSystemService();
	}

	@Provides
	@Singleton
	ServiceEnvironment getEnvironment(UrlResolver urlResolver,
			ClientSettings clientSettings, ResponseProcessor httpResultProcessor) {
		ServiceEnvironment env = GWT.create(ServiceEnvironment.class);
		env.initialize(urlResolver, clientSettings, httpResultProcessor);
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
			DialogManager dialogManager, ClientInterface pluginEnv) {
		String param = settings.getString(PARAM_FILE_UPLOADER);

		FileUploadDialogFactory uploaderFactory;
		if (VALUE_FILE_UPLOADER_FLASH.equalsIgnoreCase(param))
			uploaderFactory = new FlashFileUploadDialogFactory(textProvider,
					urlResolver, env.getFileUploadService(), sessionProvider,
					settings, dialogManager);
		else
			uploaderFactory = new HttpFileUploadDialogFactory(env,
					textProvider, env.getFileUploadService(), sessionProvider,
					dialogManager);

		return new FileUploadFactory(uploaderFactory, pluginEnv);
	}

}
