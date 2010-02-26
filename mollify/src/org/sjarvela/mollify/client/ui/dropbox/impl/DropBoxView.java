/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dropbox.impl;

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DropBoxView extends DialogBox {
	boolean attached = true;
	private FlowPanel dropTarget;

	public DropBoxView() {
		super(false, false);
		this.setText("TODO");
		this.setStylePrimaryName(StyleConstants.DROPBOX_VIEW);
		this.add(createContent());
		this.show();
		this.setVisible(false);
	}

	private Widget createContent() {
		Panel panel = new FlowPanel();
		panel.setStylePrimaryName(StyleConstants.DROPBOX_VIEW_CONTENT);
		panel.add(new Label("TODO"));
		dropTarget = new FlowPanel();
		// dropTarget.getElement().setPropertyString("style",
		// "background-color:red");
		dropTarget.setWidth("100px");
		dropTarget.setHeight("100px");
		panel.add(dropTarget);
		return panel;
	}

	public Widget getDropTarget() {
		return dropTarget;
	}

	public boolean isShown() {
		if (!attached)
			return false;
		return isVisible();
	}

	public void toggleShow() {
		if (!isShown()) {
			if (!attached) {
				show();
				attached = true;
			} else {
				setVisible(true);
			}
		} else {
			setVisible(false);
		}
	}

}
