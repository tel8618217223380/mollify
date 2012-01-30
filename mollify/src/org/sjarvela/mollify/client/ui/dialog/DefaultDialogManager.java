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

import com.google.gwt.user.client.ui.HTML;
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

	@Override
	public void showError(ServiceError error) {
		new ErrorDialog(textProvider, error);
	}

	@Override
	public void showInfo(String title, String text) {
		showInfo(title, text, null);
	}

	@Override
	public void showInfo(String title, String text, String info) {
		new InfoDialog(textProvider, title, text, info,
				StyleConstants.INFO_DIALOG_TYPE_INFO);
	}

	@Override
	public void showConfirmationDialog(String title, String message,
			String style, ConfirmationListener listener, Widget p) {
		ConfirmationDialog confirmationDialog = new ConfirmationDialog(
				textProvider, title, message, style, listener);
		if (p != null)
			viewManager.align(confirmationDialog, p);
	}

	@Override
	public void showInputDialog(String title, String message,
			String defaultValue, InputListener listener) {
		new InputDialog(textProvider, title, message, defaultValue, listener);
	}

	@Override
	public WaitDialog openWaitDialog(String title, String message) {
		return new DefaultWaitDialog(textProvider, title, message);
	}

	@Override
	public CustomContentDialog showCustomDialog(String title, String style, boolean modal,
			HTML html, CustomDialogListener listener) {
		return new DefaultCustomContentDialog(title, style, modal, html, listener);
	}
}
