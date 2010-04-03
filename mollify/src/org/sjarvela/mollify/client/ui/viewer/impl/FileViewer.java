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
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.common.dialog.ResizableDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class FileViewer extends ResizableDialog {
	private final TextProvider textProvider;
	private final ViewManager viewManager;
	private final String url;
	private final String fullUrl;
	private final String elementId;

	private FlowPanel viewerPanel;

	public FileViewer(TextProvider textProvider, ViewManager viewManager,
			String title, String embeddedUrl, String resizedElementId, int w,
			int h, String fullUrl) {
		super(title, StyleConstants.FILE_VIEWER);

		this.textProvider = textProvider;
		this.viewManager = viewManager;

		this.url = embeddedUrl;
		this.elementId = resizedElementId == null ? "mollify-fileviewer-frame"
				: resizedElementId;
		this.fullUrl = fullUrl;

		viewerPanel = new FlowPanel();
		viewerPanel.getElement().setId("mollify-fileviewer-frame");
		if (resizedElementId == null)
			viewerPanel.getElement().setAttribute("style", "overflow:auto");

		initialize();
		setElementSize(w, h);
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
	protected Element getSizedElement() {
		return DOM.getElementById(elementId);
	}

	@Override
	protected Widget createContent() {
		Panel p = new FlowPanel();
		p.setStylePrimaryName(StyleConstants.FILE_VIEWER_CONTENT);
		p.add(viewerPanel);
		p.add(createTools());
		return p;
	}

	private Widget createTools() {
		Panel p = new FlowPanel();
		p.setStylePrimaryName(StyleConstants.FILE_VIEWER_HEADER);

		if (fullUrl != null) {
			Button openInNewWindowButton = createButton(textProvider
					.getStrings().fileViewerOpenInNewWindowTitle(),
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							viewManager.openUrlInNewWindow(fullUrl);
							FileViewer.this.hide();
						}
					}, StyleConstants.FILE_VIEWER_BUTTON_OPEN);
			p.add(openInNewWindowButton);
		}

		Button closeButton = createButton(textProvider.getStrings()
				.dialogCloseButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				FileViewer.this.hide();
			}
		}, StyleConstants.FILE_VIEWER_BUTTON_CLOSE);
		p.add(closeButton);

		return p;
	}
}
