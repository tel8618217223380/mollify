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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;
import org.sjarvela.mollify.client.ui.common.MultiActionButton;
import org.sjarvela.mollify.client.ui.common.popup.BubblePopup;
import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContextPopupComponent extends BubblePopup {
	public ContextPopupComponent(String styleName, PopupPositioner listener) {
		super(styleName, null, listener);
	}

	@Override
	protected Widget createCloseButton() {
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

	protected Button createActionButton(String title,
			final ActionListener listener, final ResourceId action) {
		ActionButton button = createButton(title, action.name().toLowerCase());
		button.setAction(listener, action);
		return button;
	}

	protected MultiActionButton createMultiActionButton(
			ActionListener listener, String title, String id) {
		return new MultiActionButton(listener, title,
				(styleName + "-multiaction"), id);
	}
}
