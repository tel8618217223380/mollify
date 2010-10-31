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
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
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
	private final InputListener listener;

	private final String text;
	private final TextBox input;

	public InputDialog(TextProvider textProvider, String title, String text,
			String defaultValue, InputListener listener) {
		super(title, "input");
		this.textProvider = textProvider;
		this.text = text;
		this.listener = listener;

		this.input = new TextBox();
		this.input.setStylePrimaryName(StyleConstants.INPUT_DIALOG_INPUT);
		this.input.setText(defaultValue);

		addViewListener(new ViewListener() {
			@Override
			public void onShow() {
				input.setFocus(true);
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						input.selectAll();
					}
				});
			}
		});

		initialize();
	}

	@Override
	protected Widget createContent() {
		Panel content = new FlowPanel();
		content.setStylePrimaryName(StyleConstants.INPUT_DIALOG_CONTENT);

		Label message = new Label();
		message.getElement().setInnerHTML(text);
		message.setStylePrimaryName(StyleConstants.INPUT_DIALOG_MESSAGE);
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
				}, "input-ok"));
		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						InputDialog.this.hide();
					}
				}, "input-cancel"));

		return buttons;
	}
}
