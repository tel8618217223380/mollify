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
import org.sjarvela.mollify.client.ui.common.HoverDecorator;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class BubblePopup extends DropdownPopup {
	protected final String styleName;
	protected Widget pointer;

	public BubblePopup(Element parent, PopupPositioner popupPositioner,
			String styleName) {
		super(parent, popupPositioner);
		this.styleName = styleName;
		this.setStylePrimaryName("mollify-bubble-popup");
		this.addStyleDependentName(styleName);
	}

	protected void initialize() {
		BorderedControl content = new BorderedControl(
				"mollify-bubble-popup-border");
		content.addStyleDependentName(styleName);
		content.setContent(createContent());

		addItem(content);

		pointer = createPointer();
		addItem(pointer);

		Widget closeButton = createCloseButton();
		if (closeButton != null)
			addItem(closeButton);
	}

	protected abstract Widget createContent();

	protected Widget createPointer() {
		FlowPanel pointer = new FlowPanel();
		pointer.setStylePrimaryName("mollify-bubble-popup-pointer");
		pointer.addStyleDependentName(styleName);
		return pointer;
	}

	protected Widget createCloseButton() {
		final Label close = new Label();
		close.setStyleName("mollify-bubble-popup-close");
		close.addStyleDependentName(styleName);
		HoverDecorator.decorate(close);
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				HoverDecorator.clear(close);
				BubblePopup.this.hide();
			}
		});
		return close;
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
