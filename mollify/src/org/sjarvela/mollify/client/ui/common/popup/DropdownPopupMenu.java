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

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DropdownPopupMenu<T> extends DropdownPopup {
	private static final String DISABLED = "disabled";

	private ActionListener actionListener;
	private Map<ResourceId, Widget> items = new HashMap();
	private Map<ResourceId, Boolean> itemsEnabled = new HashMap();

	public DropdownPopupMenu(ActionListener actionListener, Element parent,
			Element opener, DropdownPopupListener dropdownListener) {
		super(parent, opener, dropdownListener);

		this.actionListener = actionListener;
		this.setStylePrimaryName(StyleConstants.DROPDOWN_MENU);

		setWidget(container);
	}

	public void addMenuAction(final ResourceId action, T item) {
		Widget itemWidget = createMenuItemWidget(action, item);
		items.put(action, itemWidget);
		itemsEnabled.put(action, true);
		addItem(itemWidget);
	}

	public void setActionEnabled(ResourceId action, boolean enabled) {
		Widget itemWidget = items.get(action);

		if (enabled)
			itemWidget.removeStyleDependentName(DISABLED);
		else
			itemWidget.addStyleDependentName(DISABLED);
		itemsEnabled.put(action, enabled);
	}

	protected Label createMenuItemWidget(final ResourceId action, T item) {
		Label label = createMenuItemWidget(item.toString());

		if (action != null)
			label.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (actionListener != null && itemsEnabled.get(action))
						actionListener.onAction(action);
				}
			});
		return label;
	}

	protected Label createMenuItemWidget(String title) {
		final Label label = new Label(title);
		label.setStyleName(StyleConstants.DROPDOWN_MENU_ITEM);
		HoverDecorator.decorate(label);

		label.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				label.removeStyleDependentName(StyleConstants.HOVER);
				DropdownPopupMenu.this.hide();
			}
		});
		return label;
	}

}
