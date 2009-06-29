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
import org.sjarvela.mollify.client.session.user.PasswordGenerator;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ResetPasswordDialog extends CenteredDialog {
	private final TextProvider textProvider;
	private final PasswordGenerator passwordGenerator;
	private final PasswordHandler handler;
	private final User user;

	private TextBox password;

	public ResetPasswordDialog(TextProvider textProvider,
			PasswordGenerator passwordGenerator, PasswordHandler handler,
			User user) {
		super(textProvider.getStrings().resetPasswordDialogTitle(),
				StyleConstants.RESET_PASSWORD_DIALOG);
		this.textProvider = textProvider;
		this.passwordGenerator = passwordGenerator;
		this.handler = handler;
		this.user = user;

		initialize();
		generateNewPassword();
	}

	private void generateNewPassword() {
		password.setText(passwordGenerator.generate());
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.RESET_PASSWORD_DIALOG_CONTENT);
		panel.add(createPasswordGenerator());
		return panel;
	}

	private Panel createPasswordGenerator() {
		Panel panel = new FlowPanel();
		Label passwordTitle = new Label(textProvider.getStrings()
				.resetPasswordDialogPassword());
		passwordTitle
				.setStyleName(StyleConstants.RESET_PASSWORD_DIALOG_PASSWORD_TITLE);
		panel.add(passwordTitle);

		password = new TextBox();
		password
				.addStyleName(StyleConstants.RESET_PASSWORD_DIALOG_PASSWORD_VALUE);
		password.setReadOnly(true);
		panel.add(password);

		Button generatePassword = createButton(textProvider.getStrings()
				.resetPasswordDialogGeneratePassword(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				generateNewPassword();
			}
		}, StyleConstants.RESET_PASSWORD_DIALOG_GENERATE_PASSWORD);

		panel.add(generatePassword);

		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.RESET_PASSWORD_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		buttons.add(createButton(textProvider.getStrings()
				.resetPasswordDialogResetButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				onReset();
			}
		}, StyleConstants.RESET_PASSWORD_DIALOG_BUTTON_RESET));

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						ResetPasswordDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	protected void onReset() {
		handler.resetPassword(user, password.getText());
		this.hide();
	}
}
