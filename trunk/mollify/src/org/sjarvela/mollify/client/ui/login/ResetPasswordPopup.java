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

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.service.ExternalService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.common.popup.BubblePopup;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ResetPasswordPopup extends BubblePopup {
	private final TextBox email;
	private final Button reset;
	private final ExternalService service;
	private final DialogManager dialogManager;

	public ResetPasswordPopup(Widget parent, ExternalService service,
			DialogManager dialogManager) {
		super("reset-password", parent, null);
		this.service = service;
		this.dialogManager = dialogManager;
		email = new TextBox();
		reset = createCallbackButton("TODO", "reset-button", new Callback() {
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
		content.add(email);
		content.add(reset);
		return content;
	}

	@Override
	protected void onShow() {
		super.onShow();
		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
				email.setFocus(true);
			}
		});
	}

	protected void onReset() {
		if (email.getText().length() == 0)
			return;
		
		Map<String, String> data = new HashMap();
		data.put("email", email.getText());

		service.post(data, new ResultListener() {
			@Override
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

			@Override
			public void onSuccess(Object result) {
				dialogManager.showInfo("TODO", "todo");
			}
		});
	}

}
