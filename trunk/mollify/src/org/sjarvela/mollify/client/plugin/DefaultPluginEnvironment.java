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
import org.sjarvela.mollify.client.plugin.itemcontext.NativeItemContextProvider;
import org.sjarvela.mollify.client.plugin.response.NativeResponseProcessor;
import org.sjarvela.mollify.client.plugin.service.NativeService;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.request.ResponseInterceptor;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultPluginEnvironment implements PluginEnvironment {
	private final EventDispatcher eventDispatcher;
	private final ResponseInterceptor responseInterceptor;
	private final ItemContextProvider itemContextProvider;
	private final SessionProvider sessionProvider;
	private final ServiceProvider serviceProvider;
	@SuppressWarnings("unused")
	private final String locale;
	private final DialogManager dialogManager;

	@Inject
	public DefaultPluginEnvironment(EventDispatcher eventDispatcher,
			ResponseInterceptor responseInterceptor,
			ItemContextProvider itemDetailsProvider,
			SessionProvider sessionProvider, ServiceProvider serviceProvider,
			DialogManager dialogManager) {
		this.eventDispatcher = eventDispatcher;
		this.responseInterceptor = responseInterceptor;
		this.itemContextProvider = itemDetailsProvider;
		this.sessionProvider = sessionProvider;
		this.serviceProvider = serviceProvider;
		this.dialogManager = dialogManager;
		this.locale = LocaleInfo.getCurrentLocale().getLocaleName();
	}

	public void addResponseProcessor(JavaScriptObject rp) {
		responseInterceptor.addProcessor(new NativeResponseProcessor(rp));
	}

	public void addEventHandler(JavaScriptObject eh) {
		// TODO use proper interface here instead of casting
		((DefaultEventDispatcher) eventDispatcher).addEventHandler(eh);
	}

	public void addItemContextProvider(JavaScriptObject dp) {
		((ItemContextHandler) itemContextProvider)
				.addItemContextProvider(new NativeItemContextProvider(dp));
	}

	protected SessionInfo getSession() {
		return sessionProvider.getSession();
	}

	public JavaScriptObject getJsEnv() {
		return createNativeEnv(this);
	}

	protected JavaScriptObject getService() {
		return new NativeService(serviceProvider.getExternalService()).asJs();
	};

	protected JavaScriptObject getDialogManager() {
		return new NativeDialogManager(dialogManager).asJs();
	};

	private native JavaScriptObject createNativeEnv(DefaultPluginEnvironment e) /*-{
		var env = {};

		env.addResponseProcessor = function (cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::addResponseProcessor(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.addEventHandler = function (cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::addEventHandler(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.addItemContextProvider = function (cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::addItemContextProvider(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.getSession = function() {
			return e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::getSession()();
		}

		env.getLocale = function() {
			return e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::locale;
		}

		env.getService = function() {
			return e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::getService()();
		}

		env.getDialogManager = function() {
			return e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::getDialogManager()();
		}

		return env;
	}-*/;

}
