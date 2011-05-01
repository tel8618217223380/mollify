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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ActionToggleButtonGroup {
	private List<ActionToggleButton> buttons;

	public ActionToggleButtonGroup(ActionToggleButton... buttonList) {
		this.buttons = new ArrayList(Arrays.asList(buttonList));
		this.init();
	}

	private void init() {
		for (final ActionToggleButton b : buttons)
			b.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					onButtonClicked(b);
				}
			});
	}

	protected void onButtonClicked(ActionToggleButton b) {
		for (ActionToggleButton button : buttons) {
			if (button.equals(b))
				continue;
			button.setDown(false);
		}
	}

}
