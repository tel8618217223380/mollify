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

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainViewHeader extends VerticalPanel {
	private Widget lower;
	private Widget upper;
	private Widget toggle;
	private Timer toggleTimer;

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

	public void setUpper(Widget upper) {
		this.upper = upper;
	}

	public void setLower(Widget lower) {
		this.lower = lower;
	}

	public void build() {
		this.add(upper);
		this.add(lower);
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
			@Override
			public void onClick(ClickEvent event) {
				if (lower.isVisible()) {
					lower.setVisible(false);
					toggle.removeStyleDependentName(StyleConstants.OPEN);
				} else {
					lower.setVisible(true);
					toggle.addStyleDependentName(StyleConstants.OPEN);
				}
			}
		});
		togglePanel.setVisible(false);
		return togglePanel;
	}
}
