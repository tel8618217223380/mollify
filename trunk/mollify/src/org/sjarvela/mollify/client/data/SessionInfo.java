/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class SessionInfo extends JavaScriptObject {
	public static SessionInfo create(boolean authenticationRequired,
			boolean authenticated, String user, PermissionMode permissionMode,
			SessionSettings settings) {
		SessionInfo result = SessionInfo.createObject().cast();
		result.putValues(authenticationRequired, authenticated, user,
				permissionMode.getStringValue(), settings);
		return result;
	}

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

	protected SessionInfo() {
	}

	public final String asString() {
		return "authentication_required=" + isAuthenticationRequired()
				+ ", logged_user=" + getLoggedUser() + ", permission_mode="
				+ getDefaultPermissionModeString() + ", settings=["
				+ getSettings().asString() + "]";
	}

	public final native boolean isAuthenticationRequired() /*-{
		return this.authentication_required;
	}-*/;

	public final native boolean getAuthenticated() /*-{
		return this.authenticated;
	}-*/;

	public final native String getLoggedUser() /*-{
		return this.user;
	}-*/;

	public final native SessionSettings getSettings() /*-{
		return this.settings;
	}-*/;

	public final PermissionMode getPermissionMode() {
		return PermissionMode.fromString(getDefaultPermissionModeString()
				.trim().toLowerCase());
	}

	private final native String getDefaultPermissionModeString() /*-{
		return this.default_permission_mode;
	}-*/;

	private final native void putValues(boolean authenticationRequired,
			boolean authenticated, String user, String permissionMode,
			SessionSettings settings) /*-{
		this.authentication_required = authenticationRequired;
		this.authenticated = authenticated;
		this.user = user;
		this.default_permission_mode = permissionMode;
		this.settings = settings;
	}-*/;
}
