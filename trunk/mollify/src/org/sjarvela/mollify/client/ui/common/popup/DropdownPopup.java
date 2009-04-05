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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DropdownPopup extends PopupPanel {
	private final DropdownPopupListener listener;
	private Element parent;
	private Element opener;
	protected Panel container;
	boolean cancelShow = false;

	public DropdownPopup(Element parent, Element opener) {
		this(parent, opener, null);
	}

	public DropdownPopup(Element parent, Element opener,
			DropdownPopupListener listener) {
		super(true);

		this.opener = opener;
		this.parent = parent;
		this.listener = listener;

		this.container = createContainer();
		setWidget(container);
	}

	protected Panel createContainer() {
		return new FlowPanel();
	}

	protected void addItem(Widget item) {
		container.add(item);
	}

	public void removeAllMenuItems() {
		container.clear();
	}

	public void showMenu() {
		this.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				if (listener != null)
					listener.setPosition(DropdownPopup.this, parent,
							offsetWidth, offsetHeight);
				else if (parent != null)
					setPopupPosition(parent.getAbsoluteLeft(), parent
							.getAbsoluteTop());
			}
		});
	}

	@Override
	public void show() {
		if (cancelShow) {
			cancelShow = false;
			return;
		}

		onShow();
		super.show();
	}

	protected void onShow() {
	}

	public Element getParentElement() {
		return parent;
	}

	public void setParentElement(Element parent) {
		this.parent = parent;
	}

	public Element getOpenerElement() {
		return opener;
	}

	public void setOpenerElement(Element opener) {
		this.opener = opener;
	}

	@Override
	public boolean onEventPreview(Event event) {
		int type = DOM.eventGetType(event);
		Element target = DOM.eventGetTarget(event);

		if (type == Event.ONMOUSEDOWN && target != null && opener != null) {
			boolean eventTargetsOpener = DOM.isOrHasChild(opener, target);

			if (eventTargetsOpener) {
				hide(true);
				cancelShow = true;
				return false;
			}
		}

		return super.onEventPreview(event);
	}

}
