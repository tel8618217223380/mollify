/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

/*import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MainViewHeader extends FlowPanel {
	private Widget upper;
	private Widget lower;
	private Widget lowerContainer;
	private Widget toggle;

	private Timer toggleTimer;
	private boolean visible = false;

	public MainViewHeader() {
		setStyleName(StyleConstants.MAIN_VIEW_HEADER_CONTAINER);
		toggle = createToggle();
		sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);

		toggleTimer = new Timer() {
			@Override
			public void run() {
				toggle.setVisible(false);
			}
		};
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);

		if (event.getTypeInt() == Event.ONMOUSEOVER) {
			toggleTimer.cancel();
			toggle.setVisible(true);
		} else if (event.getTypeInt() == Event.ONMOUSEOUT) {
			toggleTimer.schedule(500);
		}
	}

	public void build(Widget upper, Widget lower, Widget lowerContainer) {
		this.upper = upper;
		this.lower = lower;
		this.lowerContainer = lowerContainer;

		lower.getElement().setId("header-lower");
		lowerContainer.getElement().setId("header-lower-panel");
		toggle.getElement().setId("header-lower-toggle");

		this.add(upper);
		this.add(lowerContainer);
		this.add(toggle);
	}

	private Widget createToggle() {
		final Label togglePanel = new Label() {
			@Override
			public void onBrowserEvent(Event event) {
				super.onBrowserEvent(event);
				if (event.getTypeInt() == Event.ONMOUSEOVER)
					toggleTimer.cancel();
			}
		};
		togglePanel.sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT);
		togglePanel.setStylePrimaryName(StyleConstants.MAIN_VIEW_HEADER_TOGGLE);
		togglePanel.addClickHandler(new ClickHandler() {
			int h = -1;
			int t = -1;

			@Override
			public void onClick(ClickEvent event) {
				if (h < 0)
					h = lower.getElement().getOffsetHeight();
				if (t < 0)
					t = lowerContainer.getElement().getOffsetTop();

				if (visible) {
					toggle(false, h, t);
					toggle.removeStyleDependentName(StyleConstants.OPEN);
					visible = false;
				} else {
					toggle(true, h, t);
					toggle.addStyleDependentName(StyleConstants.OPEN);
					visible = true;
				}
			}
		});
		togglePanel.setVisible(false);
		return togglePanel;
	}

	protected native void toggle(boolean open, int h, int t) /*-{
		var s = open ? h + "px" : "0px";
		$wnd.$("#header-lower-panel").stop().animate({'height':s}, 200);

		var m = open ? "0" : "-" + h + "px";
		$wnd.$("#header-lower").stop().animate({'marginTop':m}, 200);

		var t = open ? (t + h) + "px" : t + "px";
		$wnd.$("#header-lower-toggle").stop().animate({'top':t}, 200);
	}-;

	public int getTotalHeight() {
		return upper.getOffsetHeight() + lower.getOffsetHeight();
	}
}*/
