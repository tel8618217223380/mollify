/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common.dialog;

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class CenteredDialog extends DialogBox {
	public CenteredDialog(String title, String style) {
		super(false, true);
		this.setStylePrimaryName(StyleConstants.DIALOG);
		this.addStyleDependentName(style);
		this.setText(title);
	}

	protected void initialize() {
		VerticalPanel content = new VerticalPanel();
		content.add(createContent());

		Widget buttons = createButtons();
		if (buttons != null)
			content.add(buttons);
		this.add(content);

		this.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});
	}

	protected Widget createButtons() {
		return null;
	}

	protected abstract Widget createContent();

	@Override
	public void show() {
		super.show();
		onShow();
	}

	protected void onShow() {
	}

	protected Button createButton(String title, ClickListener listener,
			String style) {
		Button button = new Button(title);
		button.setStylePrimaryName(StyleConstants.DIALOG_BUTTON);
		button.addStyleDependentName(style);
		button.addClickListener(listener);
		return button;
	}
}
