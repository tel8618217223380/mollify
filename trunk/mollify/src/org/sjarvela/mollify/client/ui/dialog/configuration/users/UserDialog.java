/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog.configuration.users;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.PasswordGenerator;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.session.UserNameValidator;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserDialog extends CenteredDialog {
	public enum Mode {
		Add, Edit
	};

	private final TextProvider textProvider;
	private final PasswordGenerator passwordGenerator;
	private final UserHandler handler;
	private final Mode mode;
	private final User user;

	private TextBox userName;
	private TextBox password;
	private ListBox userType;

	public UserDialog(TextProvider textProvider,
			PasswordGenerator passwordGenerator, UserHandler handler) {
		super(textProvider.getStrings().userDialogAddTitle(),
				StyleConstants.USER_DIALOG);
		this.passwordGenerator = passwordGenerator;
		this.mode = Mode.Add;
		this.textProvider = textProvider;
		this.handler = handler;
		this.user = null;

		initialize();
		generateNewPassword();
	}

	public UserDialog(TextProvider textProvider, UserHandler handler, User user) {
		super(textProvider.getStrings().userDialogEditTitle(),
				StyleConstants.USER_DIALOG);
		this.user = user;
		this.mode = Mode.Edit;
		this.passwordGenerator = null;
		this.textProvider = textProvider;
		this.handler = handler;

		initialize();
		setUserData();
	}

	private void setUserData() {
		userName.setText(user.getName());
		userType.setSelectedIndex(user.getType().ordinal());
	}

	@Override
	protected void initialize() {
		super.initialize();

		for (PermissionMode mode : PermissionMode.values())
			userType.addItem(mode.getLocalizedText(textProvider), mode
					.getStringValue());
	}

	private void generateNewPassword() {
		password.setText(passwordGenerator.generate());
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.USER_DIALOG_CONTENT);

		Label nameTitle = new Label(textProvider.getStrings()
				.userDialogUserName());
		nameTitle.setStyleName(StyleConstants.USER_DIALOG_NAME_TITLE);
		panel.add(nameTitle);

		userName = new TextBox();
		userName.addStyleName(StyleConstants.USER_DIALOG_NAME_VALUE);
		panel.add(userName);

		Label userTypeTitle = new Label(textProvider.getStrings()
				.userDialogUserType());
		userTypeTitle.setStyleName(StyleConstants.USER_DIALOG_TYPE_TITLE);
		panel.add(userTypeTitle);

		userType = new ListBox();
		userType.addStyleName(StyleConstants.USER_DIALOG_TYPE_VALUE);
		panel.add(userType);

		if (mode.equals(Mode.Add))
			panel.add(addPasswordGenerator());

		return panel;
	}

	private Panel addPasswordGenerator() {
		Panel panel = new FlowPanel();
		Label passwordTitle = new Label(textProvider.getStrings()
				.userDialogPassword());
		passwordTitle.setStyleName(StyleConstants.USER_DIALOG_PASSWORD_TITLE);
		panel.add(passwordTitle);

		password = new TextBox();
		password.addStyleName(StyleConstants.USER_DIALOG_PASSWORD_VALUE);
		password.setReadOnly(true);
		panel.add(password);

		Button generatePassword = createButton(textProvider.getStrings()
				.userDialogGeneratePassword(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				generateNewPassword();
			}
		}, StyleConstants.USER_DIALOG_GENERATE_PASSWORD);

		panel.add(generatePassword);

		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.USER_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		String title = mode.equals(Mode.Add) ? textProvider.getStrings()
				.userDialogAddButton() : textProvider.getStrings()
				.userDialogEditButton();

		buttons.add(createButton(title, new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (mode.equals(Mode.Add))
					onAddUser();
				else
					onEditUser();
			}
		}, StyleConstants.USER_DIALOG_BUTTON_ADD_EDIT));

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						UserDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	protected void onAddUser() {
		if (userName.getText().length() == 0)
			return;
		if (password.getText().length() == 0)
			return;
		if (userType.getSelectedIndex() < 0)
			return;
		if (!new UserNameValidator().validate(userName.getText()))
			return;

		handler.addUser(userName.getText(), password.getText(), PermissionMode
				.fromString(userType.getValue(userType.getSelectedIndex())));
		this.hide();
	}

	protected void onEditUser() {
		if (userName.getText().length() == 0)
			return;
		if (userType.getSelectedIndex() < 0)
			return;

		handler.editUser(user, userName.getText(), PermissionMode
				.fromString(userType.getValue(userType.getSelectedIndex())));
		this.hide();
	}
}
