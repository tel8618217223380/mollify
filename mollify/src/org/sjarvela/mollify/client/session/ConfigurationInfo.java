package org.sjarvela.mollify.client.session;

import com.google.gwt.core.client.JavaScriptObject;

public class ConfigurationInfo extends JavaScriptObject {
	public static ConfigurationInfo create(
			boolean isConfigurationUpdateSupported,
			boolean isPermissionUpdateSupported) {
		ConfigurationInfo result = ConfigurationInfo.createObject().cast();
		result.putValues(isConfigurationUpdateSupported,
				isPermissionUpdateSupported);
		return result;
	}

	protected ConfigurationInfo() {
	}

	public final native boolean isConfigurationUpdateSupported() /*-{
		return this.configuration_update_support;
	}-*/;

	public final native boolean isPermissionUpdateSupported() /*-{
		return this.permission_update_support;
	}-*/;

	private final native void putValues(boolean isConfigurationUpdateSupported,
			boolean isPermissionUpdateSupported) /*-{
		this.configuration_update_support = isConfigurationUpdateSupported;
		this.permission_update_support = isPermissionUpdateSupported;
	}-*/;

}
