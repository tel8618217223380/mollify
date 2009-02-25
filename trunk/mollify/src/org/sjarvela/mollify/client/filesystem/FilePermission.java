package org.sjarvela.mollify.client.filesystem;

public enum FilePermission {
	None(""), ReadWrite("rw"), ReadOnly("ro");

	private String stringValue;

	FilePermission(String value) {
		this.stringValue = value;
	}

	public boolean canWrite() {
		return this.equals(ReadWrite);
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
