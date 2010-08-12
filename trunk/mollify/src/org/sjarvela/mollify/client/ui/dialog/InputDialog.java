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
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InputDialog extends CenteredDialog {
	private final TextProvider textProvider;

	private final String type;
	private final String text;

	private TextBox input;

	private final InputListener listener;

	public InputDialog(TextProvider textProvider, String title, String type,
			String text, String defaultValue, InputListener listener) {
		super(title, type);
		this.textProvider = textProvider;
		this.type = type;
		this.text = text;
		this.listener = listener;

		this.input = new TextBox();
		this.input.setStylePrimaryName(StyleConstants.INPUT_DIALOG_INPUT);
		this.input.addStyleDependentName(type);
		this.input.setText(defaultValue);

		initialize();
	}

	@Override
	protected Widget createContent() {
		Panel content = new FlowPanel();
		content.setStylePrimaryName(StyleConstants.INPUT_DIALOG_CONTENT);

		Label message = new Label(text);
		message.setStylePrimaryName(StyleConstants.INPUT_DIALOG_MESSAGE);
		message.addStyleDependentName(type);
		content.add(message);

		content.add(input);

		return content;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setStylePrimaryName(StyleConstants.INPUT_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		buttons.add(createButton(textProvider.getStrings().dialogOkButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						if (!listener.isInputAcceptable(input.getText()))
							return;
						InputDialog.this.hide();
						listener.onInput(input.getText());
					}
				}, type));
		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						InputDialog.this.hide();
					}
				}, type));

		return buttons;
	}
}
