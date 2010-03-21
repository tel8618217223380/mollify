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

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ConfirmationListener;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewManager;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultDialogManager implements DialogManager {
	private final TextProvider textProvider;
	private final ViewManager viewManager;

	@Inject
	public DefaultDialogManager(TextProvider textProvider,
			ViewManager viewManager) {
		this.textProvider = textProvider;
		this.viewManager = viewManager;
	}

	public void showError(ServiceError error) {
		new ErrorDialog(textProvider, error);
	}

	public void showInfo(String title, String text) {
		new InfoDialog(textProvider, title, text,
				StyleConstants.INFO_DIALOG_TYPE_INFO);
	}

	public void showConfirmationDialog(String title, String message,
			String style, ConfirmationListener listener, Widget p) {
		ConfirmationDialog confirmationDialog = new ConfirmationDialog(
				textProvider, title, message, style, listener);
		if (p != null)
			viewManager.align(confirmationDialog, p);
	}

}
