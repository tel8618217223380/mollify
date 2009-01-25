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
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.Widget;

public class Button extends Label {

	public Button(String title, String id, String styleClass) {
		super(title);
		setStyleName(styleClass);
		HoverDecorator.decorate(this);

		this.addMouseListener(new MouseListenerAdapter() {
			private boolean mousePressed = false;

			@Override
			public void onMouseDown(Widget sender, int x, int y) {
				mousePressed = true;
				sender.addStyleDependentName(StyleConstants.PRESSED);
			}

			@Override
			public void onMouseUp(Widget sender, int x, int y) {
				mousePressed = false;
				sender.removeStyleDependentName(StyleConstants.PRESSED);
			}

			@Override
			public void onMouseEnter(Widget sender) {
				if (mousePressed)
					sender.addStyleDependentName(StyleConstants.PRESSED);
			}

			@Override
			public void onMouseLeave(Widget sender) {
				sender.removeStyleDependentName(StyleConstants.PRESSED);
			}

		});
		if (id != null)
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
