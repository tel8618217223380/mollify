/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.login;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.ExternalService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.JSONBuilder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.common.popup.BubblePopup;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ResetPasswordPopup extends BubblePopup {
	private final ExternalService service;
	private final DialogManager dialogManager;
	private final TextProvider textProvider;

	private final TextBox email;
	private final Button reset;

	public ResetPasswordPopup(TextProvider textProvider, Element parent,
			ExternalService service, DialogManager dialogManager) {
		super(parent, null, "reset-password");
		this.textProvider = textProvider;
		this.service = service;
		this.dialogManager = dialogManager;

		email = new TextBox();
		email.setStylePrimaryName("mollify-reset-password-popup-email");

		reset = createCallbackButton(
				textProvider.getText(Texts.resetPasswordPopupButton),
				"reset-button", new Callback() {
					@Override
					public void onCallback() {
						onReset();
					}
				});
		initialize();
	}

	@Override
	protected Widget createContent() {
		Panel content = new FlowPanel();
		content.setStylePrimaryName("mollify-reset-password-popup-content");

		Label label = new Label(
				textProvider.getText(Texts.resetPasswordPopupMessage));
		label.setStylePrimaryName("mollify-reset-password-popup-label");
		content.add(label);

		content.add(email);
		content.add(reset);

		return content;
	}

	@Override
	protected void onShow() {
		super.onShow();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				email.setFocus(true);
			}
		});
	}

	protected void onReset() {
		if (email.getText().length() == 0)
			return;

		String data = new JSONBuilder("email", email.getText())
				.toString();

		service.post(data, new ResultListener() {
			@Override
			public void onFail(ServiceError error) {
				if (error.getType().equals(ServiceErrorType.INVALID_REQUEST)) {
					dialogManager.showInfo(
							textProvider.getText(Texts.resetPasswordPopupTitle),
							textProvider
									.getText(Texts.resetPasswordPopupInvalidEmail));
					email.setFocus(true);
				} else if (error.getType().equals(
						ServiceErrorType.REQUEST_FAILED)) {
					ResetPasswordPopup.this.hide();
					dialogManager.showInfo(
							textProvider.getText(Texts.resetPasswordPopupTitle),
							textProvider
									.getText(Texts.resetPasswordPopupResetFailed));
				} else {
					ResetPasswordPopup.this.hide();
					dialogManager.showError(error);
				}

			}

			@Override
			public void onSuccess(Object result) {
				ResetPasswordPopup.this.hide();
				dialogManager.showInfo(textProvider
						.getText(Texts.resetPasswordPopupTitle), textProvider
						.getText(Texts.resetPasswordPopupResetSuccess));
			}
		});
	}

	@Override
	protected Widget createCloseButton() {
		return null;
	}

}
