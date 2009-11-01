/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.data.ErrorValue;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultDialogManager implements DialogManager {
	private final TextProvider textProvider;

	@Inject
	public DefaultDialogManager(TextProvider textProvider) {
		this.textProvider = textProvider;
	}

	public void showError(ServiceError error) {
		new InfoDialog(textProvider, textProvider.getStrings()
				.infoDialogErrorTitle(), error.getType().getMessage(
				textProvider), StyleConstants.INFO_DIALOG_TYPE_ERROR);
	}

	public void showError(ErrorValue errorResult) {
		new InfoDialog(textProvider, textProvider.getStrings()
				.infoDialogErrorTitle(), textProvider
				.getErrorMessage(errorResult),
				StyleConstants.INFO_DIALOG_TYPE_ERROR);
	}

	public void showInfo(String title, String text) {
		new InfoDialog(textProvider, title, text,
				StyleConstants.INFO_DIALOG_TYPE_INFO);
	}

	public void showConfirmationDialog(String title, String message,
			String style, ConfirmationListener listener) {
		new ConfirmationDialog(textProvider, title, message, style, listener);
	}

}
