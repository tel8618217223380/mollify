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

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DropdownPopup extends PopupPanel {
	private PopupPositioner positioner;

	protected final Panel container;
	private Widget parent;

	public DropdownPopup(Widget parent) {
		this(parent, null);
	}

	public DropdownPopup(Widget parent, PopupPositioner positioner) {
		super(true);

		this.parent = parent;
		this.positioner = positioner;

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

	public void setPositioner(PopupPositioner positioner) {
		this.positioner = positioner;
	}

	public void showMenu() {
		this.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				if (positioner != null)
					positioner.setPositionOnShow(DropdownPopup.this, parent,
							offsetWidth, offsetHeight);
				else if (parent != null)
					showRelativeTo(parent);
			}
		});
	}

	@Override
	public void show() {
		onShow();
		super.show();
	}

	protected void onShow() {
	}

	public Widget getParentWidget() {
		return parent;
	}

	public void setParentWidget(Widget parent) {
		this.parent = parent;
	}
}
