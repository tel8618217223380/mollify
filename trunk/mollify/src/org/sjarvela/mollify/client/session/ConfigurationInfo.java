package org.sjarvela.mollify.client.session;

import com.google.gwt.core.client.JavaScriptObject;

public class ConfigurationInfo extends JavaScriptObject {
	public static ConfigurationInfo create(
			boolean isConfigurationUpdateSupported) {
		ConfigurationInfo result = ConfigurationInfo.createObject().cast();
		result.putValues(isConfigurationUpdateSupported);
		return result;
	}

	protected ConfigurationInfo() {
	}

	public final native boolean isConfigurationUpdateSupported() /*-{
		return this.configuration_update_support;
	}-*/;

	private final native void putValues(boolean isConfigurationUpdateSupported) /*-{
		this.configuration_update_support = isConfigurationUpdateSupported;
	}-*/;

}
