/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common.popup;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.BorderedControl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class BubblePopup extends DropdownPopup {
	protected final String styleName;

	public BubblePopup(String styleName, Widget parent,
			PopupPositioner popupPositioner) {
		super(parent, popupPositioner);
		this.styleName = styleName;
		this.setStyleName(styleName);
	}

	protected void initialize() {
		BorderedControl content = new BorderedControl(styleName + "-border");
		content.setContent(createContent());

		addItem(content);
		addItem(createPointer());

		Widget closeButton = createCloseButton();
		if (closeButton != null)
			addItem(closeButton);
	}

	protected abstract Widget createContent();

	protected Widget createPointer() {
		FlowPanel pointer = new FlowPanel();
		pointer.setStyleName(styleName + "-pointer");
		return pointer;
	}

	protected Widget createCloseButton() {
		return null;
	}

	protected ActionButton createButton(String title, String id) {
		String base = styleName + "-action";
		ActionButton button = new ActionButton(title);
		button.addStyleName(base);
		if (id != null)
			button.getElement().setId(base + "-" + id);
		return button;
	}

	protected Button createCallbackButton(String title, String id,
			final Callback callback) {
		ActionButton button = createButton(title, id);
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				callback.onCallback();
			}
		});
		return button;
	}

}
