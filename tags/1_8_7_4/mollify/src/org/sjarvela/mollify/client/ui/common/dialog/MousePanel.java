/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common.dialog;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;

public class MousePanel extends FlowPanel {
	private boolean mouseDown;
	private final MousePanelListener listener;

	public MousePanel(MousePanelListener listener) {
		this.listener = listener;
		this.add(new FlowPanel());
		sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONMOUSEMOVE);
	}

	@Override
	public void onBrowserEvent(Event event) {
		switch (event.getTypeInt()) {
		case Event.ONMOUSEDOWN:
			mouseDown = true;
			Event.setCapture(getElement());
			event.preventDefault();
			listener.onMouseDown(event.getClientX(), event.getClientY());
			break;

		case Event.ONMOUSEUP:
			mouseDown = false;
			Event.releaseCapture(getElement());
			event.preventDefault();
			listener.onMouseUp(event.getClientX(), event.getClientY());
			break;

		case Event.ONMOUSEMOVE:
			if (mouseDown)
				listener.onMouseDrag(event.getClientX(), event.getClientY());
			break;
		}
	}
}
