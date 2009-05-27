/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration;

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationMenu extends VerticalPanel {
	private final ConfigurationMenuSelectionListener listener;
	private Widget selected = null;
	private Map<ResourceId, Widget> items = new HashMap();

	public ConfigurationMenu(ConfigurationMenuSelectionListener listener) {
		super();
		this.listener = listener;
		setStyleName(StyleConstants.CONFIGURATION_DIALOG_MENU);
	}

	public void addItem(ResourceId id, String title, String style) {
		this.add(createItem(id, title, style));
	}

	private Widget createItem(final ResourceId id, String title, String style) {
		final Label item = new Label(title);
		item.setStylePrimaryName(StyleConstants.CONFIGURATION_DIALOG_MENU_ITEM);
		item.addStyleDependentName(style);
		HoverDecorator.decorate(item);

		items.put(id, item);
		item.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				selectItem(id);
			}
		});
		return item;
	}

	public void selectItem(ResourceId id) {
		if (selected != null)
			selected.removeStyleDependentName(StyleConstants.SELECTED);

		selected = items.get(id);
		selected.addStyleDependentName(StyleConstants.SELECTED);

		listener.onConfigurationItemSelected(id);
	}

}
