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

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;

public class HoverDecorator {
	private static MouseOverHandler mouseOverHandler;
	private static MouseOutHandler mouseOutHandler;

	static MouseOverHandler getMouseOverHandler() {
		if (mouseOverHandler == null) {
			mouseOverHandler = new MouseOverHandler() {
				public void onMouseOver(MouseOverEvent event) {
					((Widget) event.getSource())
							.addStyleDependentName(StyleConstants.HOVER);
				}
			};
		}
		return mouseOverHandler;
	}

	static MouseOutHandler getMouseOutHandler() {
		if (mouseOutHandler == null) {
			mouseOutHandler = new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent event) {
					clear((Widget) event.getSource());
				}
			};
		}
		return mouseOutHandler;
	}

	// public static void decorate(Button decorated) {
	// decorated.addMouseOverHandler(getMouseOverHandler());
	// decorated.addMouseOutHandler(getMouseOutHandler());
	// }
	//
	// public static void decorate(Label decorated) {
	// decorated.addMouseOverHandler(getMouseOverHandler());
	// decorated.addMouseOutHandler(getMouseOutHandler());
	// }

	public static void clear(Widget decorated) {
		decorated.removeStyleDependentName(StyleConstants.HOVER);
	}

	public static void decorate(Widget decorated) {
		decorated.sinkEvents(Event.ONCLICK | Event.ONMOUSEOVER
				| Event.ONMOUSEOUT);
		decorated.addHandler(getMouseOverHandler(), MouseOverEvent.getType());
		decorated.addHandler(getMouseOutHandler(), MouseOutEvent.getType());
	}

}
