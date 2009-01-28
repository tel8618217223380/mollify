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

import org.sjarvela.mollify.client.ui.mainview.MainView.Action;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DropdownButton extends Composite {
	private MollifyButton button;
	private DropdownPopupMenu menu;

	public DropdownButton(ActionListener actionListener, String title, String id) {
		button = new MollifyButton(title, null, StyleConstants.DROPDOWN_BUTTON);
		initWidget(button);

		menu = new DropdownPopupMenu<String>(actionListener, this.getElement(),
				button.getElement());

		button.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				menu.show();
			}
		});

		if (id != null)
			getElement().setId(id);
	}

	public void addAction(Action action, String title) {
		menu.addMenuAction(action, title);
	}
}
