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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.MollifyClient;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.session.user.UserNameValidator;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

public class LoginViewHandler {
	private static Logger logger = Logger.getLogger(LoginViewHandler.class
			.getName());

	private final ViewManager viewManager;
	private final DialogManager dialogManager;
	private final SessionService service;
	private final NativeLoginView view;

	private final SessionManager sessionManager;

	public LoginViewHandler(ViewManager viewManager,
			DialogManager dialogManager, SessionService service,
			SessionManager sessionManager) {
		this.viewManager = viewManager;
		this.dialogManager = dialogManager;
		this.service = service;
		this.sessionManager = sessionManager;
		this.view = new NativeLoginView(viewManager.getViewHandler("login"));

		view.init(new LoginViewListener() {
			@Override
			public void onLogin(String username, String password,
					boolean remember) {
				LoginViewHandler.this.onLogin(username, password, remember);
			}
		});

		this.viewManager.render(this.view);

		// this.setModal(false);
		// this.addViewListener(new ViewListener() {
		// public void onShow() {
		// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		// public void execute() {
		// userName.setFocus(true);
		// }
		// });
		// }
		// });
		//
		// initialize();
	}

	// @Override
	// protected Widget createButtons() {
	// HorizontalPanel buttons = new HorizontalPanel();
	// buttons.addStyleName(StyleConstants.LOGIN_DIALOG_BUTTONS);
	// buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
	//
	// buttons.add(createButton(
	// textProvider.getText(Texts.loginDialogLoginButton),
	// new ClickHandler() {
	// public void onClick(ClickEvent event) {
	// onLogin();
	// }
	// }, StyleConstants.LOGIN_DIALOG_BUTTON_LOGIN));
	//
	// buttons.add(createButton(
	// textProvider.getText(Texts.dialogCancelButton),
	// new ClickHandler() {
	// public void onClick(ClickEvent event) {
	// LoginDialog.this.hide();
	// }
	// }, StyleConstants.DIALOG_BUTTON_CANCEL));
	//
	// return buttons;
	// }

	// @Override
	// protected Widget createContent() {
	// KeyPressHandler loginHandler = new KeyPressHandler() {
	// public void onKeyPress(KeyPressEvent event) {
	// if (event.getCharCode() == 13)
	// onLogin();
	// }
	// };
	//
	// VerticalPanel panel = new VerticalPanel();
	// panel.setStyleName(StyleConstants.LOGIN_DIALOG_CONTENT);
	//
	// Label usernameTitle = new Label(
	// textProvider.getText(Texts.loginDialogUsername));
	// usernameTitle.setStyleName(StyleConstants.LOGIN_DIALOG_USERNAME_TITLE);
	// panel.add(usernameTitle);
	//
	// userName = new TextBox();
	// userName.setStyleName(StyleConstants.LOGIN_DIALOG_USERNAME_VALUE);
	// userName.addKeyPressHandler(loginHandler);
	// panel.add(userName);
	//
	// Label passwordTitle = new Label(
	// textProvider.getText(Texts.loginDialogPassword));
	// passwordTitle.setStyleName(StyleConstants.LOGIN_DIALOG_PASSWORD_TITLE);
	// panel.add(passwordTitle);
	//
	// password = new PasswordTextBox();
	// password.setStyleName(StyleConstants.LOGIN_DIALOG_PASSWORD_VALUE);
	// password.addKeyPressHandler(loginHandler);
	// panel.add(password);
	//
	// if (showResetPassword) {
	// final ActionLink link = createLink(
	// textProvider.getText(Texts.loginDialogResetPassword), null,
	// StyleConstants.LOGIN_DIALOG_BUTTON_RESET_PASSWORD);
	// link.setClickHandler(new ClickHandler() {
	// @Override
	// public void onClick(ClickEvent event) {
	// new ResetPasswordPopup(textProvider, link.getElement(),
	// serviceProvider.getExternalService("lostpassword"),
	// dialogManager).showPopup();
	// }
	// });
	// panel.add(link);
	// }
	//
	// rememberMe = new CheckBox(
	// textProvider.getText(Texts.loginDialogRememberMe));
	// rememberMe.setStylePrimaryName("mollify-login-dialog-remember-me");
	// panel.add(rememberMe);
	//
	// return panel;
	// }

	private void onLogin(String username, String password, boolean remember) {
		if (username == null || username.length() < 1)
			return;

		if (password == null || password.length() < 1)
			return;

		if (!new UserNameValidator().validate(username))
			return;

		logger.log(Level.INFO, "User login: " + username);

		service.authenticate(username, password, remember,
				MollifyClient.PROTOCOL_VERSION,
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						if (ServiceErrorType.AUTHENTICATION_FAILED
								.equals(error)) {
							view.showLoginError();
							return;
						}
						dialogManager.showError(error);
					}

					public void onSuccess(SessionInfo session) {
						sessionManager.setSession(session);
					}
				});
	}
}
