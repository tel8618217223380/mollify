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

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmationDialog extends CenteredDialog {
	private DefaultTextProvider localizator;
	private String text;
	private String type;
	private ConfirmationListener listener;

	public ConfirmationDialog(DefaultTextProvider localizator, String title,
			String text, String type, ConfirmationListener listener) {
		super(title, type);
		this.localizator = localizator;
		this.text = text;
		this.type = type;
		this.listener = listener;

		initialize();
	}

	@Override
	Widget createContent() {
		HorizontalPanel content = new HorizontalPanel();
		content.addStyleName(StyleConstants.CONFIRMATION_DIALOG_CONTENT);

		Label icon = new Label();
		icon.addStyleName(StyleConstants.CONFIRMATION_DIALOG_ICON);
		icon.addStyleName(type);
		content.add(icon);

		Label message = new Label(text);
		message.addStyleName(StyleConstants.CONFIRMATION_DIALOG_MESSAGE);
		message.addStyleName(type);
		content.add(message);

		return content;
	}

	@Override
	Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.CONFIRMATION_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		Button yesButton = createButton(localizator.getStrings()
				.confirmationDialogYesButton(), new ClickListener() {
			public void onClick(Widget sender) {
				ConfirmationDialog.this.hide();
				listener.onConfirm();
			}
		}, type + "-yes");
		buttons.add(yesButton);

		Button noButton = createButton(localizator.getStrings()
				.confirmationDialogNoButton(), new ClickListener() {
			public void onClick(Widget sender) {
				ConfirmationDialog.this.hide();
			}
		}, type + "-no");
		buttons.add(noButton);

		return buttons;
	}

}
