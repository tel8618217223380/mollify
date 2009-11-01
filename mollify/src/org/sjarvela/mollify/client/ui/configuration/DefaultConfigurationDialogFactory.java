/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.session.user.PasswordGenerator;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

import com.google.inject.Inject;

public class DefaultConfigurationDialogFactory implements
		ConfigurationDialogFactory {
	private final TextProvider textProvider;
	private final DialogManager dialogManager;
	private final SessionProvider sessionProvider;
	private final ServiceEnvironment env;
	private final PasswordGenerator passwordGenerator;

	@Inject
	public DefaultConfigurationDialogFactory(TextProvider textProvider,
			DialogManager dialogManager, SessionProvider sessionProvider,
			ServiceEnvironment env, PasswordGenerator passwordGenerator) {
		this.textProvider = textProvider;
		this.dialogManager = dialogManager;
		this.sessionProvider = sessionProvider;
		this.env = env;
		this.passwordGenerator = passwordGenerator;
	}

	public void openConfigurationDialog(PasswordHandler passwordHandler) {
		new ConfigurationDialog(textProvider, dialogManager, sessionProvider
				.getSession(), env.getConfigurationService(), passwordHandler,
				passwordGenerator);
	}

}
