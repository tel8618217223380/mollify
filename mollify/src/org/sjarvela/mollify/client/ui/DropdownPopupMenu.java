/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DropdownPopupMenu<T> extends DropdownPopup {
	private ActionListener actionListener;

	public DropdownPopupMenu(ActionListener actionListener, Element parent,
			Element opener) {
		super(parent, opener);

		this.actionListener = actionListener;
		this.setStyleName(StyleConstants.DROPDOWN_MENU);

		setWidget(container);
	}

	public void addMenuAction(final ActionId action, T item) {
		addItem(createMenuItemWidget(action, item));
	}

	protected Label createMenuItemWidget(final ActionId action, T item) {
		Label label = createMenuItemWidget(item.toString());

		if (action != null)
			label.addClickListener(new ClickListener() {
				public void onClick(Widget sender) {
					DropdownPopupMenu.this.hide();
					if (actionListener != null)
						actionListener.onActionTriggered(action);
				}
			});
		return label;
	}

	protected Label createMenuItemWidget(String title) {
		Label label = new Label(title);
		label.setStyleName(StyleConstants.DROPDOWN_MENU_ITEM);
		MouseEventDecorator.decorate(label);
		return label;
	}

}
