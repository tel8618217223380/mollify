package org.sjarvela.mollify.client.ui.password;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PasswordDialog extends CenteredDialog {
	private final TextProvider textProvider;
	private final PasswordHandler passwordHandler;

	private PasswordTextBox originalPassword;
	private PasswordTextBox newPassword;
	private PasswordTextBox confirmNewPassword;

	public PasswordDialog(TextProvider textProvider,
			PasswordHandler passwordHandler) {
		super(textProvider.getText(Texts.passwordDialogTitle),
				StyleConstants.PASSWORD_DIALOG);
		this.textProvider = textProvider;
		this.passwordHandler = passwordHandler;

		initialize();
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.PASSWORD_DIALOG_CONTENT);

		Label originalPasswordTitle = new Label(
				textProvider.getText(Texts.passwordDialogOriginalPassword));
		originalPasswordTitle
				.setStyleName(StyleConstants.PASSWORD_ORIGINAL_PASSWORD_TITLE);
		panel.add(originalPasswordTitle);

		originalPassword = new PasswordTextBox();
		originalPassword
				.addStyleName(StyleConstants.PASSWORD_ORIGINAL_PASSWORD_VALUE);
		panel.add(originalPassword);

		Label newPasswordTitle = new Label(
				textProvider.getText(Texts.passwordDialogNewPassword));
		newPasswordTitle
				.setStyleName(StyleConstants.PASSWORD_NEW_PASSWORD_TITLE);
		panel.add(newPasswordTitle);

		newPassword = new PasswordTextBox();
		newPassword.addStyleName(StyleConstants.PASSWORD_NEW_PASSWORD_VALUE);
		panel.add(newPassword);

		Label confirmNewPasswordTitle = new Label(
				textProvider.getText(Texts.passwordDialogConfirmNewPassword));
		confirmNewPasswordTitle
				.setStyleName(StyleConstants.PASSWORD_CONFIRM_NEW_PASSWORD_TITLE);
		panel.add(confirmNewPasswordTitle);

		confirmNewPassword = new PasswordTextBox();
		confirmNewPassword
				.addStyleName(StyleConstants.PASSWORD_CONFIRM_NEW_PASSWORD_VALUE);
		panel.add(confirmNewPassword);

		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.PASSWORD_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		buttons.add(createButton(
				textProvider.getText(Texts.passwordDialogChangeButton),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						onRename();
					}
				}, StyleConstants.PASSWORD_DIALOG_BUTTON_CHANGE));

		buttons.add(createButton(
				textProvider.getText(Texts.dialogCancelButton),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						PasswordDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	private void onRename() {
		this.newPassword.removeStyleDependentName(StyleConstants.INVALID);
		this.confirmNewPassword
				.removeStyleDependentName(StyleConstants.INVALID);

		String oldPassword = this.originalPassword.getText();
		String newPassword = this.newPassword.getText();
		String confirmPassword = this.confirmNewPassword.getText();

		if (oldPassword.length() == 0 || newPassword.length() == 0
				|| confirmPassword.length() == 0) {
			return;
		}

		if (!newPassword.equals(confirmPassword)) {
			this.newPassword.addStyleDependentName(StyleConstants.INVALID);
			this.confirmNewPassword
					.addStyleDependentName(StyleConstants.INVALID);
			return;
		}
		this.hide();
		passwordHandler.changePassword(oldPassword, newPassword);
	}
}