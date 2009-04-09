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
import org.sjarvela.mollify.client.session.LoginHandler;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LoginDialog extends CenteredDialog {
	private final TextProvider textProvider;
	private final LoginHandler loginHandler;

	private TextBox userName;
	private PasswordTextBox password;

	public LoginDialog(TextProvider textProvider, LoginHandler loginHandler) {
		super(textProvider.getStrings().loginDialogTitle(),
				StyleConstants.LOGIN_DIALOG);
		this.textProvider = textProvider;
		this.loginHandler = loginHandler;
		initialize();
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.LOGIN_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		buttons.add(createButton(textProvider.getStrings()
				.loginDialogLoginButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				onLogin();
			}
		}, StyleConstants.LOGIN_DIALOG_BUTTON_LOGIN));

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						LoginDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.LOGIN_DIALOG_CONTENT);

		Label usernameTitle = new Label(textProvider.getStrings()
				.loginDialogUsername());
		usernameTitle.setStyleName(StyleConstants.LOGIN_DIALOG_USERNAME_TITLE);
		panel.add(usernameTitle);

		userName = new TextBox();
		userName.setStyleName(StyleConstants.LOGIN_DIALOG_USERNAME_VALUE);
		userName.addKeyboardListener(new KeyboardListenerAdapter() {
			@Override
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				super.onKeyPress(sender, keyCode, modifiers);
				if (keyCode == 13)
					onLogin();
			}
		});
		panel.add(userName);

		Label passwordTitle = new Label(textProvider.getStrings()
				.loginDialogPassword());
		passwordTitle.setStyleName(StyleConstants.LOGIN_DIALOG_PASSWORD_TITLE);
		panel.add(passwordTitle);

		password = new PasswordTextBox();
		password.setStyleName(StyleConstants.LOGIN_DIALOG_PASSWORD_VALUE);
		password.addKeyboardListener(new KeyboardListenerAdapter() {
			@Override
			public void onKeyPress(Widget sender, char keyCode, int modifiers) {
				super.onKeyPress(sender, keyCode, modifiers);
				if (keyCode == 13)
					onLogin();
			}
		});
		panel.add(password);

		return panel;
	}

	private void onLogin() {
		if (userName.getText().length() < 1)
			return;

		if (password.getText().length() < 1)
			return;

		loginHandler.onLogin(userName.getText(), password.getText(),
				new ConfirmationListener() {
					public void onConfirm() {
						LoginDialog.this.hide();
					}
				});
	}
}
