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
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ErrorDialog extends CenteredDialog {
	private final TextProvider textProvider;
	private final ServiceError error;

	public ErrorDialog(TextProvider textProvider, ServiceError error) {
		super(textProvider.getText(Texts.infoDialogErrorTitle),
				StyleConstants.INFO_DIALOG_TYPE_ERROR);
		this.textProvider = textProvider;
		this.error = error;

		initialize();
	}

	@Override
	protected Widget createContent() {
		VerticalPanel main = new VerticalPanel();
		main.addStyleName(StyleConstants.INFO_DIALOG_CONTENT);

		HorizontalPanel content = new HorizontalPanel();
		Label icon = new Label();
		icon.addStyleName(StyleConstants.INFO_DIALOG_ICON);
		icon.addStyleName(StyleConstants.INFO_DIALOG_TYPE_ERROR);
		content.add(icon);

		Label message = new Label(error.getType().getMessage(textProvider));
		message.addStyleName(StyleConstants.INFO_DIALOG_MESSAGE);
		message.addStyleName(StyleConstants.INFO_DIALOG_TYPE_ERROR);
		content.add(message);

		main.add(content);

		if (LogConfiguration.loggingIsEnabled()) {
			DisclosurePanel debug = new DisclosurePanel("Debug");
			debug.setOpen(false);
			debug.addStyleName("mollify-error-dialog-debug");
			Panel debugContent = new FlowPanel();
			HTML debugHtml = new HTML();
			debugHtml.setStylePrimaryName("mollify-error-dialog-debug-html");
			createDebugHtml(debugHtml);
			debugContent.add(debugHtml);
			debug.add(debugContent);
			main.add(debug);
		}

		return main;
	}

	private void createDebugHtml(HTML debugHtml) {
		StringBuilder result = new StringBuilder("<p><b>Details</b><br/>");
		result.append(error.getDetails().isEmpty() ? "-" : error.getDetails());
		result.append("</p><p><b>Debug log<b><br/>");

		if (error.getError() != null && error.getError().getDebugInfo() != null) {
			for (String i : JsUtil.asList(error.getError().getDebugInfo())) {
				result.append("<code>").append(i).append("</code><br/>");
			}
		} else {
			result.append("<code>-</code>");
		}
		debugHtml.setHTML(result.toString());
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.INFO_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		buttons.add(createButton(textProvider.getText(Texts.dialogOkButton),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						ErrorDialog.this.hide();
					}
				}, StyleConstants.INFO_DIALOG_TYPE_ERROR));

		return buttons;
	}
}
