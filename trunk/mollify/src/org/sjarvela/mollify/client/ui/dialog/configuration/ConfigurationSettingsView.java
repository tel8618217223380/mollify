/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog.configuration;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.FlowPanel;

public abstract class ConfigurationSettingsView extends FlowPanel {
	protected final TextProvider textProvider;

	public ConfigurationSettingsView(TextProvider textProvider, String style) {
		this.textProvider = textProvider;
		setStylePrimaryName(StyleConstants.CONFIGURATION_DIALOG_VIEW);
		addStyleDependentName(style);
	}

	public abstract String getTitle();
}
