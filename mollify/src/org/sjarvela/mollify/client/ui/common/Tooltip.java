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

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class Tooltip extends PopupPanel {
	private MouseOutHandler mouseOutHandler = null;
	private ClickHandler clickHandler;

	public Tooltip(String style, String text) {
		// this.setAutoHideEnabled(true);
		this.setStylePrimaryName(StyleConstants.TOOLTIP);
		if (style != null)
			this.addStyleDependentName(style);

		Label content = new Label(text);
		content.setStylePrimaryName(StyleConstants.TOOLTIP_CONTENT);
		this.add(content);

		this.mouseOutHandler = new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				Tooltip.this.hide();
			}
		};
		this.clickHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				Tooltip.this.hide();
			}
		};
	}

	public void attach(final Button target) {
		// this.addAutoHidePartner(target.getElement());

		target.addMouseOverHandler(createMouseOverHandler(target));
		target.addMouseOutHandler(mouseOutHandler);
		target.addClickHandler(clickHandler);
	}

	public void attach(Label target) {
		target.addMouseOverHandler(createMouseOverHandler(target));
		target.addMouseOutHandler(mouseOutHandler);
		target.addClickHandler(clickHandler);
	}

	private MouseOverHandler createMouseOverHandler(final Widget target) {
		return new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				Tooltip.this.setPopupPositionAndShow(new PositionCallback() {
					public void setPosition(int offsetWidth, int offsetHeight) {
						Tooltip.this.setPopupPosition(target.getAbsoluteLeft(),
								target.getAbsoluteTop() + offsetHeight + 5);
					}
				});
			}
		};
	}
}
