/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.log;

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HtmlLogger implements MollifyLogger {
	private static final String MOLLIFY_LOG_PANEL_ID = "mollify-log";

	private Panel log;

	public void initialize(RootPanel rootPanel) {
		Panel panel = RootPanel.get(MOLLIFY_LOG_PANEL_ID);
		if (panel == null) {
			panel = new FlowPanel();
			rootPanel.add(panel);
		}
		panel.setStyleName(StyleConstants.MOLLIFY_LOG);

		log = new VerticalPanel();
		panel.add(log);
	}

	public void logError(String error) {
		GWT.log(error, null);
		add("<b>ERROR:</b>&nbsp;" + error);
	}

	public void logInfo(String info) {
		GWT.log(info, null);
		add("<b>INFO:</b>&nbsp;" + info);
	}

	private void add(String entry) {
		log.add(new HTML(entry));
	}
}
