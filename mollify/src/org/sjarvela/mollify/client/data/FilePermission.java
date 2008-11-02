package org.sjarvela.mollify.client.data;

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
		for (FilePermission permission : FilePermission.values()) {
			if (permission.stringValue.equals(value))
				return permission;
		}
		return None;
	}
}
