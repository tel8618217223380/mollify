package org.sjarvela.mollify.client.ui.dialog.configuration.users;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.PasswordGenerator;
import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
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

		init();
		generateNewPassword();
	}

	private void generateNewPassword() {
		password.setText(passwordGenerator.generate());
	}

	public UserDialog(TextProvider textProvider, UserHandler handler, User user) {
		super(textProvider.getStrings().userDialogEditTitle(),
				StyleConstants.USER_DIALOG);
		this.mode = Mode.Edit;
		this.passwordGenerator = null;
		this.textProvider = textProvider;
		this.handler = handler;

		init();
	}

	private void init() {
		initialize();

		for (PermissionMode mode : PermissionMode.values())
			userType.addItem(mode.name(), mode.getStringValue());
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

		Button generatePassword = new Button(textProvider.getStrings()
				.userDialogGeneratePassword());
		generatePassword.setStylePrimaryName(StyleConstants.DIALOG_BUTTON);
		generatePassword
				.addStyleDependentName(StyleConstants.USER_DIALOG_GENERATE_PASSWORD);
		generatePassword.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				generateNewPassword();
			}
		});
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

		buttons.add(createButton(title, new ClickListener() {
			public void onClick(Widget sender) {
				if (mode.equals(Mode.Add))
					onAddUser();
				else
					onEditUser();
			}
		}, StyleConstants.USER_DIALOG_BUTTON_ADD));

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickListener() {

					public void onClick(Widget sender) {
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

		handler.addUser(userName.getText(), password.getText(), PermissionMode
				.fromString(userType.getValue(userType.getSelectedIndex())));
		this.hide();
	}

	protected void onEditUser() {
		if (userName.getText().length() == 0)
			return;
		if (userType.getSelectedIndex() < 0)
			return;

		handler.editUser(userName.getText(), PermissionMode.fromString(userType
				.getValue(userType.getSelectedIndex())));
		this.hide();
	}
}
