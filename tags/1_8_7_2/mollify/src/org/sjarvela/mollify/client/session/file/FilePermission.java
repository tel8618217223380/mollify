package org.sjarvela.mollify.client.session.file;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;

public enum FilePermission {
	None("no"), ReadWrite("rw"), ReadOnly("ro");

	private String stringValue;

	FilePermission(String value) {
		this.stringValue = value;
	}

	public boolean canWrite() {
		return this.equals(ReadWrite);
	}

	public String getLocalizedText(TextProvider textProvider) {
		if (this.equals(None))
			return textProvider.getText(Texts.permissionModeNone);
		if (this.equals(ReadWrite))
			return textProvider.getText(Texts.permissionModeReadWrite);
		if (this.equals(ReadOnly))
			return textProvider.getText(Texts.permissionModeReadOnly);
		throw new RuntimeException("Unlocalized permission: " + this.name());
	}

	public static FilePermission fromString(String value) {
		String val = value.trim().toLowerCase();

		for (FilePermission permission : FilePermission.values()) {
			if (permission.stringValue.equals(val))
				return permission;
		}
		return None;
	}

	public String getStringValue() {
		return stringValue;
	}
}
