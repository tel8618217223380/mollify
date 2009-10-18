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

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.login.UiSessionManager;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(ContainerConfiguration.class)
public interface Container extends Ginjector {
	SessionManager getSessionManager();
	
	UiSessionManager getUiSessionManager();

	TextProvider getTextProvider();

	ClientSettings getClientSettings();

	ServiceEnvironment getEnvironment();

	SessionProvider getSessionProvider();

	ViewManager getViewManager();

	DialogManager getDialogManager();

	MainViewFactory getMainViewFactory();

}
