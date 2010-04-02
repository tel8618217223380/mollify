/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.viewer.impl;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.ResizableDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class FileViewer extends ResizableDialog {
	private final TextProvider textProvider;
	private final String url;

	private FlowPanel viewerPanel;

	public FileViewer(TextProvider textProvider, String title, String url) {
		super(title, StyleConstants.FILE_VIEWER);

		this.textProvider = textProvider;
		this.url = url;

		viewerPanel = new FlowPanel();
		viewerPanel.getElement().setId("mollify-fileviewer-frame");
		viewerPanel.getElement().setAttribute("style", "overflow:auto");

		initialize();

		getSizedWidget().setPixelSize(600, 400);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		loadContent(url);
	}

	private native void loadContent(String url) /*-{
		$wnd.$("#mollify-fileviewer-frame").load(url);
	}-*/;

	@Override
	protected Widget getSizedWidget() {
		return viewerPanel;
	}

	@Override
	protected Widget createContent() {
		Panel p = new FlowPanel();
		p.setStylePrimaryName(StyleConstants.FILE_VIEWER_CONTENT);
		p.add(createHeader());
		p.add(viewerPanel);
		return p;
	}

	private Widget createHeader() {
		Panel p = new FlowPanel();
		p.setStylePrimaryName(StyleConstants.FILE_VIEWER_HEADER);

		Button closeButton = createButton(textProvider.getStrings()
				.dialogCloseButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				FileViewer.this.hide();
			}
		}, StyleConstants.DIALOG_BUTTON_CLOSE);
		p.add(closeButton);

		return p;
	}
}
