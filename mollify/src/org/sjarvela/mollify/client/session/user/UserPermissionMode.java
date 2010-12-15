package org.sjarvela.mollify.client.session.user;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;

public enum UserPermissionMode {
	Admin("a"), Staff("st"), ReadWrite("rw"), ReadOnly("ro"), None("-");

	private final String value;

	public static UserPermissionMode fromString(String mode) {
		for (UserPermissionMode permissionMode : UserPermissionMode.values())
			if (permissionMode.getStringValue().equalsIgnoreCase(mode))
				return permissionMode;
		return UserPermissionMode.ReadOnly;
	}

	private UserPermissionMode(String value) {
		this.value = value;
	}

	public boolean hasWritePermission() {
		return this.equals(Admin) || this.equals(Staff) || this.equals(ReadWrite);
	}

	public String getStringValue() {
		return value;
	}

	public String getLocalizedText(TextProvider textProvider) {
		if (this.equals(Admin))
			return textProvider.getText(Texts.permissionModeAdmin);
		if (this.equals(ReadWrite))
			return textProvider.getText(Texts.permissionModeReadWrite);
		if (this.equals(ReadOnly))
			return textProvider.getText(Texts.permissionModeReadOnly);
		throw new RuntimeException("Unlocalized permission mode: "
				+ this.name());
	}

	public boolean isAdmin() {
		return this.equals(Admin);
	}

	public boolean isStaff() {
		return this.equals(Staff);
	}
}