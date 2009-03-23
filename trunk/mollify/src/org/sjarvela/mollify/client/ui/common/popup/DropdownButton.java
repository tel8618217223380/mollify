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

import org.sjarvela.mollify.client.ui.ActionId;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.ActionButton;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DropdownButton extends Composite {
	private ActionButton button;
	private DropdownPopupMenu menu;

	public DropdownButton(ActionListener actionListener, String title, String id) {
		this(actionListener, title, id, null);
	}

	public DropdownButton(ActionListener actionListener, String title,
			String id, Element parent) {
		button = new ActionButton(title, id == null ? null : id + "-button",
				StyleConstants.DROPDOWN_BUTTON);
		initWidget(button);

		menu = new DropdownPopupMenu<String>(actionListener,
				parent == null ? this.getElement() : parent, button
						.getElement());
		if (id != null)
			menu.getElement().setId(id + "-menu");

		button.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				menu.show();
			}
		});

		if (id != null)
			getElement().setId(id);
	}

	public void setText(String text) {
		button.setText(text);
	}

	public void addAction(ActionId action, String title) {
		menu.addMenuAction(action, title);
	}

	public void setActionEnabled(ActionId action, boolean enabled) {
		menu.setActionEnabled(action, enabled);
	}

}
