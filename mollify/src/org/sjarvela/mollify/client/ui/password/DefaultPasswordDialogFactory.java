/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.password;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.user.PasswordGenerator;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.session.user.User;

import com.google.inject.Inject;

public class DefaultPasswordDialogFactory implements PasswordDialogFactory {
	private final TextProvider textProvider;
	private final PasswordGenerator passwordGenerator;

	@Inject
	public DefaultPasswordDialogFactory(TextProvider textProvider,
			PasswordGenerator passwordGenerator) {
		this.textProvider = textProvider;
		this.passwordGenerator = passwordGenerator;
	}

	public void openPasswordDialog(PasswordHandler handler) {
		new PasswordDialog(textProvider, handler);
	}

	public void openResetPasswordDialog(User user,
			PasswordHandler passwordHandler) {
		new ResetPasswordDialog(textProvider, passwordGenerator,
				passwordHandler, user);
	}

}
