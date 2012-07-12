/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin;

import org.sjarvela.mollify.client.event.DefaultEventDispatcher;
import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandlerFactory;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.plugin.response.NativeResponseProcessor;
import org.sjarvela.mollify.client.plugin.service.NativeService;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.request.ResponseInterceptor;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultClientInterface implements ClientInterface {
	private final EventDispatcher eventDispatcher;
	private final ResponseInterceptor responseInterceptor;
	// private final ItemContextHandler itemContextHandler;
	private final SessionProvider sessionProvider;
	private final ServiceProvider serviceProvider;
	private final DialogManager dialogManager;
	private final TextProvider textProvider;
	// private final FileListExt fileListInterface;
	// private final ViewManager viewManager;
	private final FileSystemActionHandler actionHandler;

	// private FileUploadDialogFactory uploader = null;
	private final NativeViewManager nativeViewManager;

	@Inject
	public DefaultClientInterface(EventDispatcher eventDispatcher,
			ResponseInterceptor responseInterceptor,
			SessionProvider sessionProvider, ServiceProvider serviceProvider,
			DialogManager dialogManager, TextProvider textProvider,
			ViewManager viewManager,
			FileSystemActionHandlerFactory actionHandlerFactory) {
		this.eventDispatcher = eventDispatcher;
		this.responseInterceptor = responseInterceptor;
		// this.itemContextHandler = itemContextProvider;
		this.sessionProvider = sessionProvider;
		this.serviceProvider = serviceProvider;
		this.dialogManager = dialogManager;
		this.textProvider = textProvider;
		// this.viewManager = viewManager;//
		// this.fileListInterface = new FileListExt(textProvider);
		this.actionHandler = actionHandlerFactory.create();

		this.nativeViewManager = new NativeViewManager(viewManager,
				dialogManager);
	}

	public void addResponseProcessor(JavaScriptObject rp) {
		responseInterceptor.addProcessor(new NativeResponseProcessor(rp));
	}

	public void addEventHandler(JavaScriptObject eh) {
		// TODO use proper interface here instead of casting
		((DefaultEventDispatcher) eventDispatcher).addEventHandler(eh);
	}

	public void addUploader(JavaScriptObject uploader) {
		// this.uploader = new NativeUploader(uploader);
	}

	// public void addItemContextProvider(JavaScriptObject dp, JavaScriptObject
	// rq) {
	// // itemContextHandler
	// // .addItemContextProvider(new NativeItemContextProvider(dp, rq));
	// }

	// public void addListColumnSpec(JavaScriptObject spec) {
	// // fileListInterface.addListColumnSpec(spec);
	// }

	protected JavaScriptObject getSession() {
		return new NativeSession(sessionProvider.getSession()).asJs();
	}

	@Override
	public JavaScriptObject asJs(String pluginBaseUrl) {
		return createNativeEnv(this, pluginBaseUrl, getTextProvider(),
				getService(), getViewManager(), getLogger(),
				getSessionActions());
	}

	private JavaScriptObject getSessionActions() {
		return new NativeSessionActions(serviceProvider.getSessionService())
				.asJs();
	}

	protected JavaScriptObject getService() {
		return new NativeService(serviceProvider.getExternalService()).asJs();
	};

	protected JavaScriptObject getViewManager() {
		return nativeViewManager.asJs();
	};

	protected JavaScriptObject getTextProvider() {
		return new NativeTextProvider(textProvider).asJs();
	};

	protected JavaScriptObject getLogger() {
		return new NativeLogger().asJs();
	};

	private native JavaScriptObject createNativeEnv(DefaultClientInterface e,
			String pluginBaseUrl, JavaScriptObject textProvider,
			JavaScriptObject service, JavaScriptObject viewManager,
			JavaScriptObject logger, JavaScriptObject sessionActions) /*-{
		var env = {};

		env.addResponseProcessor = function(cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::addResponseProcessor(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.addUploader = function(cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::addUploader(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.addEventHandler = function(cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::addEventHandler(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.session = {
			get : function() {
				return e.@org.sjarvela.mollify.client.plugin.DefaultClientInterface::getSession()();
			},
			actions : sessionActions
		}

		env.service = function() {
			return service;
		}

		env.texts = function() {
			return textProvider;
		}

		env.log = function() {
			return logger;
		}

		env.views = function() {
			return viewManager;
		}

		env.pluginUrl = function(id) {
			return pluginBaseUrl + id + "/";
		}

		return env;
	}-*/;

	// @Override
	// public FileUploadDialogFactory getCustomUploader() {
	// return uploader;
	// }

}
