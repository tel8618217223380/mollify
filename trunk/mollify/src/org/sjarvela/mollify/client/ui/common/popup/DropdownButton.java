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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;

import com.google.gwt.user.client.ui.Widget;

public class DropdownButton extends ActionButton {
	private DropdownPopupMenu menu;

	public DropdownButton(ActionListener actionListener, String title, String id) {
		this(actionListener, title, id, null, null);
	}

	public DropdownButton(ActionListener actionListener, String title,
			String id, Widget parent) {
		this(actionListener, title, id, parent, null);
	}

	public DropdownButton(ActionListener actionListener, String title,
			String id, Widget parent, PopupPositioner listener) {
		super(title, id == null ? null : id + "-button",
				StyleConstants.DROPDOWN_BUTTON);

		if (id != null)
			getElement().setId(id);

		menu = new DropdownPopupMenu<String>(actionListener,
				parent != null ? parent : this, listener);
		if (id != null)
			menu.getElement().setId(id + "-menu");

		new PopupClickTrigger(this, menu);
	}

	public void addAction(ResourceId action, String title) {
		menu.addMenuAction(action, title);
	}

	public void setActionEnabled(ResourceId action, boolean enabled) {
		menu.setActionEnabled(action, enabled);
	}

	public void addSeparator() {
		menu.addSeparator();
	}

	@Override
	public boolean showTooltip() {
		return !menu.isShowing();
	}
}
