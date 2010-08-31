/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.BorderedControl;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;
import org.sjarvela.mollify.client.ui.common.MultiActionButton;
import org.sjarvela.mollify.client.ui.common.popup.DropdownPopup;
import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContextPopupComponent extends DropdownPopup {
	private final String styleName;

	public ContextPopupComponent(String styleName, PopupPositioner listener) {
		super(null, listener);
		this.styleName = styleName;
		this.setStyleName(styleName);
	}

	protected void initialize() {
		BorderedControl content = new BorderedControl(styleName + "-border");
		content.setContent(createContent());

		addItem(content);
		addItem(createPointer());
		addItem(createCloseButton());
	}

	protected abstract Widget createContent();

	private Widget createPointer() {
		FlowPanel pointer = new FlowPanel();
		pointer.setStyleName(styleName + "-pointer");
		return pointer;
	}

	private Widget createCloseButton() {
		final Label close = new Label();
		close.setStyleName(styleName + "-close");
		HoverDecorator.decorate(close);
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				HoverDecorator.clear(close);
				ContextPopupComponent.this.hide();
			}
		});
		return close;
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

	protected Button createActionButton(String title,
			final ActionListener listener, final ResourceId action) {
		ActionButton button = createButton(title, action.name().toLowerCase());
		button.setAction(listener, action);
		return button;
	}

	private ActionButton createButton(String title, String id) {
		String base = styleName + "-action";
		ActionButton button = new ActionButton(title);
		button.addStyleName(base);
		if (id != null)
			button.getElement().setId(base + "-" + id);
		return button;
	}

	protected MultiActionButton createMultiActionButton(
			ActionListener listener, String title, String id) {
		return new MultiActionButton(listener, title,
				(styleName + "-multiaction"), id);
	}
}
