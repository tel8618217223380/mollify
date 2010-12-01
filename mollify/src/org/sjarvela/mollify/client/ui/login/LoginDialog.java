/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.login;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.ConfirmationListener;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.session.LoginHandler;
import org.sjarvela.mollify.client.session.user.UserNameValidator;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.common.ActionLink;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LoginDialog extends CenteredDialog {
	private final TextProvider textProvider;
	private final LoginHandler loginHandler;
	private final DialogManager dialogManager;
	private final ServiceProvider serviceProvider;
	private final boolean showResetPassword;

	private TextBox userName;
	private PasswordTextBox password;

	public LoginDialog(TextProvider textProvider, DialogManager dialogManager,
			LoginHandler loginHandler, ServiceProvider serviceProvider,
			boolean showResetPassword) {
		super(textProvider.getText(Texts.loginDialogTitle),
				StyleConstants.LOGIN_DIALOG);
		this.textProvider = textProvider;
		this.dialogManager = dialogManager;
		this.loginHandler = loginHandler;
		this.serviceProvider = serviceProvider;
		this.showResetPassword = showResetPassword;
		this.setModal(false);
		this.addViewListener(new ViewListener() {
			public void onShow() {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					public void execute() {
						userName.setFocus(true);
					}
				});
			}
		});

		initialize();
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.LOGIN_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		buttons.add(createButton(
				textProvider.getText(Texts.loginDialogLoginButton),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						onLogin();
					}
				}, StyleConstants.LOGIN_DIALOG_BUTTON_LOGIN));

		buttons.add(createButton(
				textProvider.getText(Texts.dialogCancelButton),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						LoginDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	@Override
	protected Widget createContent() {
		KeyPressHandler loginHandler = new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == 13)
					onLogin();
			}
		};

		VerticalPanel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.LOGIN_DIALOG_CONTENT);

		Label usernameTitle = new Label(
				textProvider.getText(Texts.loginDialogUsername));
		usernameTitle.setStyleName(StyleConstants.LOGIN_DIALOG_USERNAME_TITLE);
		panel.add(usernameTitle);

		userName = new TextBox();
		userName.setStyleName(StyleConstants.LOGIN_DIALOG_USERNAME_VALUE);
		userName.addKeyPressHandler(loginHandler);
		panel.add(userName);

		Label passwordTitle = new Label(
				textProvider.getText(Texts.loginDialogPassword));
		passwordTitle.setStyleName(StyleConstants.LOGIN_DIALOG_PASSWORD_TITLE);
		panel.add(passwordTitle);

		password = new PasswordTextBox();
		password.setStyleName(StyleConstants.LOGIN_DIALOG_PASSWORD_VALUE);
		password.addKeyPressHandler(loginHandler);
		panel.add(password);

		if (showResetPassword) {
			final ActionLink link = createLink(
					textProvider.getText(Texts.loginDialogResetPassword), null,
					StyleConstants.LOGIN_DIALOG_BUTTON_RESET_PASSWORD);
			link.setClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					new ResetPasswordPopup(textProvider, link, serviceProvider
							.getExternalService("lostpassword"), dialogManager)
							.showPopup();
				}
			});
			panel.add(link);
		}

		return panel;
	}

	private void onLogin() {
		if (userName.getText().length() < 1)
			return;

		if (password.getText().length() < 1)
			return;

		if (!new UserNameValidator().validate(userName.getText()))
			return;

		loginHandler.login(userName.getText(), password.getText(),
				new ConfirmationListener() {
					public void onConfirm() {
						LoginDialog.this.hide();
					}
				});
	}
}
