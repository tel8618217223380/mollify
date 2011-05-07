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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class Tooltip extends PopupPanel {
	private MouseOutHandler mouseOutHandler;
	private ClickHandler clickHandler;

	public Tooltip(String style, String content) {
		this.setStylePrimaryName(StyleConstants.TOOLTIP);
		if (style != null)
			this.addStyleDependentName(style);

		this.add(createContent(content));

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

	protected Widget createContent(String text) {
		Label content = new Label(text);
		content.setStylePrimaryName(StyleConstants.TOOLTIP_CONTENT);
		return content;
	}

	public void attach(TooltipTarget target) {
		if (target.getWidget() != null) {
			target.getWidget().addMouseOverHandler(
					createMouseOverHandler(target));
			target.getWidget().addMouseOutHandler(mouseOutHandler);
			target.getWidget().addClickHandler(clickHandler);
		}
	}

	private MouseOverHandler createMouseOverHandler(final TooltipTarget target) {
		return new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				if (!target.showTooltip())
					return;

				Tooltip.this.setPopupPositionAndShow(new PositionCallback() {
					public void setPosition(int offsetWidth, int offsetHeight) {
						Tooltip.this.setPopupPosition(target.getWidget()
								.getAbsoluteLeft(), target.getWidget()
								.getAbsoluteTop()
								+ target.getWidget().getOffsetHeight() + 5);
					}
				});
			}
		};
	}

	public void attachTo(Widget target) {
		attachTo(target, null);
	}

	public void attachTo(Widget target, TooltipPositioner p) {
		target.sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
		target.addHandler(createMouseOverHandler(target, p),
				MouseOverEvent.getType());
		target.addHandler(mouseOutHandler, MouseOutEvent.getType());
		target.addHandler(clickHandler, ClickEvent.getType());
	}

	private MouseOverHandler createMouseOverHandler(final Widget target,
			final TooltipPositioner p) {
		return new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				Tooltip.this.setPopupPositionAndShow(new PositionCallback() {
					public void setPosition(int offsetWidth, int offsetHeight) {
						int left = target.getAbsoluteLeft();
						int top = target.getAbsoluteTop()
								+ target.getOffsetHeight() + 5;
						if (p != null) {
							Coords c = p.getPosition(target, top, left,
									offsetWidth, offsetHeight);
							left = c.x;
							top = c.y;
						}
						Tooltip.this.setPopupPosition(left, top);
					}
				});
			}
		};
	}
}
