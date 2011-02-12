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

import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.service.FileSystemService;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class GridFileWidget extends Composite {
	private static final List<String> extensions = Arrays.asList("png", "gif",
			"jpg");

	private final FileSystemItem item;
	private final Widget widget;
	private final boolean thumbnails;
	private final FileSystemService service;

	public GridFileWidget(FileSystemItem item, boolean thumbnails,
			FileSystemService service) {
		this.item = item;
		this.thumbnails = thumbnails;
		this.service = service;
		this.widget = createContent();
		initWidget(widget);
	}

	private Widget createContent() {
		Panel panel = new FlowPanel();
		panel.setStylePrimaryName("mollify-file-grid-item");
		panel.addStyleDependentName(item.isFile() ? "file" : "folder");
		if (item.isFile())
			panel.addStyleDependentName(((File) item).getExtension());
		else if (Folder.Parent.equals(item))
			panel.addStyleDependentName("folder-parent");

		if (showThumbnais()) {
			String url = service.getThumbnailUrl(item);
			panel.add(new HTML(
					"<div class='mollify-file-grid-item-thumbnail-container'><img src='"
							+ url
							+ "' class='mollify-file-grid-item-thumbnail'></img></div>"));
		} else {
			Panel icon = new FlowPanel();
			icon.setStylePrimaryName("mollify-file-grid-item-icon");
			panel.add(icon);
		}

		Label label = new Label(item.getName());
		label.setStylePrimaryName("mollify-file-grid-item-label");
		panel.add(label);

		return panel;
	}

	private boolean showThumbnais() {
		if (!thumbnails || !item.isFile())
			return false;
		String ext = ((File) item).getExtension().trim().toLowerCase();
		if (ext.isEmpty())
			return false;
		return GridFileWidget.extensions.contains(ext);
	}

	public void select(boolean b) {
		if (b)
			widget.addStyleDependentName("selected");
		else
			widget.removeStyleDependentName("selected");
	}

	public void hilight(boolean b) {
		if (b)
			widget.addStyleDependentName("hilighted");
		else
			widget.removeStyleDependentName("hilighted");
	}

}
