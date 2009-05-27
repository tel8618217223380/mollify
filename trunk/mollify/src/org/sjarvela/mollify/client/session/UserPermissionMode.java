package org.sjarvela.mollify.client.session;

import org.sjarvela.mollify.client.localization.TextProvider;

public enum UserPermissionMode {
	Admin("a"), ReadWrite("rw"), ReadOnly("ro");

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
		return this.equals(Admin) || this.equals(ReadWrite);
	}

	public String getStringValue() {
		return value;
	}

	public String getLocalizedText(TextProvider textProvider) {
		if (this.equals(Admin))
			return textProvider.getStrings().permissionModeAdmin();
		if (this.equals(ReadWrite))
			return textProvider.getStrings().permissionModeReadWrite();
		if (this.equals(ReadOnly))
			return textProvider.getStrings().permissionModeReadOnly();
		throw new RuntimeException("Unlocalized permission mode: "
				+ this.name());
	}

	public boolean isAdmin() {
		return this.equals(Admin);
	}
}