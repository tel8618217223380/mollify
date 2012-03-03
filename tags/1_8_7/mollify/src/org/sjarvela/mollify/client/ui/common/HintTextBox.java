/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.TextBox;

public class HintTextBox extends TextBox {
	private final String hint;
	private String value = "";

	public HintTextBox(String hint) {
		this.hint = hint;
		this.setStylePrimaryName("mollify-hint-textbox");

		this.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				HintTextBox.this.value = getText();
			}
		});

		this.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				showHint(value.isEmpty());
			}
		});

		this.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				showHint(false);
			}
		});

		showHint(true);
	}

	private void showHint(boolean b) {
		super.setText(b ? this.hint : this.value);
		if (b)
			this.addStyleDependentName("hinted");
		else
			this.removeStyleDependentName("hinted");
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		value = HintTextBox.this.getText();
		showHint(value.isEmpty());
	}

	public void clear() {
		setText("");
		this.setFocus(false);
	}
}
