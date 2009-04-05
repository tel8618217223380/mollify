package org.sjarvela.mollify.client.session;

public enum PermissionMode {
	Admin("a"), ReadWrite("rw"), ReadOnly("ro");

	private final String value;

	public static PermissionMode fromString(String mode) {
		for (PermissionMode permissionMode : PermissionMode.values())
			if (permissionMode.getStringValue().equals(mode))
				return permissionMode;
		return PermissionMode.ReadOnly;
	}

	private PermissionMode(String value) {
		this.value = value;
	}

	public boolean hasWritePermission() {
		return this.equals(Admin) || this.equals(ReadWrite);
	}

	public String getStringValue() {
		return value;
	}
}