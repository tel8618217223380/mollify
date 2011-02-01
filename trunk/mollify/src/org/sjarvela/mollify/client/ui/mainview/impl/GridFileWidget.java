/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class GridFileWidget extends Composite {
	private final FileSystemItem item;
	private final Widget widget;

	public GridFileWidget(FileSystemItem item) {
		this.item = item;
		this.widget = createContent();
		initWidget(widget);
	}

	private Widget createContent() {
		Panel panel = new FlowPanel();
		panel.setStylePrimaryName("mollify-file-grid-item");
		panel.addStyleDependentName(item.isFile() ? "file" : "folder");
		if (item.isFile())
			panel.addStyleDependentName(((File) item).getExtension());

		Panel icon = new FlowPanel();
		icon.setStylePrimaryName("mollify-file-grid-item-icon");
		panel.add(icon);

		Label label = new Label(item.getName());
		label.setStylePrimaryName("mollify-file-grid-item-label");
		panel.add(label);

		return panel;
	}

	public void select(boolean b) {
		if (b)
			widget.addStyleDependentName("selected");
		else
			widget.removeStyleDependentName("selected");
	}

}
