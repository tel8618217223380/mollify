package org.sjarvela.mollify.client.session;

import org.sjarvela.mollify.client.localization.TextProvider;

public enum FilePermissionMode {
	None(""), ReadWrite("rw"), ReadOnly("ro");

	private String stringValue;

	FilePermissionMode(String value) {
		this.stringValue = value;
	}

	public boolean canWrite() {
		return this.equals(ReadWrite);
	}

	public String getLocalizedText(TextProvider textProvider) {
		if (this.equals(None))
			return textProvider.getStrings().permissionModeNone();
		if (this.equals(ReadWrite))
			return textProvider.getStrings().permissionModeReadWrite();
		if (this.equals(ReadOnly))
			return textProvider.getStrings().permissionModeReadOnly();
		throw new RuntimeException("Unlocalized permission: " + this.name());
	}

	public static FilePermissionMode fromString(String value) {
		String val = value.trim().toLowerCase();

		for (FilePermissionMode permission : FilePermissionMode.values()) {
			if (permission.stringValue.equals(val))
				return permission;
		}
		return None;
	}

	public String getStringValue() {
		return stringValue;
	}
}
