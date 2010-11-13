/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DefaultWaitDialog extends CenteredDialog implements WaitDialog {
	private final String message;

	public DefaultWaitDialog(TextProvider textProvider, String title,
			String message) {
		super(title, "wait-dialog");
		this.message = message;

		initialize();
	}

	@Override
	protected Widget createContent() {
		Panel icon = new FlowPanel();
		Label messageLabel = new Label(message);

		Panel content = new FlowPanel();
		content.add(icon);
		content.add(messageLabel);
		return content;
	}

	@Override
	public void close() {
		this.hide();
	}

}
