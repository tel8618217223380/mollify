package org.sjarvela.mollify.client.ui.dialog.configuration;

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.FlowPanel;

public class ConfigurationSettingsView extends FlowPanel {
	public ConfigurationSettingsView(String style) {
		setStylePrimaryName(StyleConstants.CONFIGURATION_DIALOG_VIEW);
		addStyleDependentName(style);
	}
}
