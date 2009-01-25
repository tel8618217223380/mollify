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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HeaderButton extends Label {

	public HeaderButton(String title, String id, String styleClass) {
		super(title);
		setStyleName(styleClass);
		MouseEventDecorator.decorate(this);
		getElement().setId(id);
	}

	public void setAction(final ActionListener actionListener,
			final Action action) {
		this.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				actionListener.onActionTriggered(action);
			}
		});
	}
}
