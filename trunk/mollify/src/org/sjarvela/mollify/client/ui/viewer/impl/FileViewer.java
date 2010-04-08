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

import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ExternalService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.common.dialog.ResizableDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class FileViewer extends ResizableDialog {
	private final TextProvider textProvider;
	private final ViewManager viewManager;
	private final ExternalService service;
	private final String url;
	private final String fullUrl;
	private String resizedElementId;

	private FlowPanel viewerPanel;

	public FileViewer(TextProvider textProvider, ViewManager viewManager,
			ExternalService service, String title, String embeddedUrl,
			String fullUrl) {
		super(title, StyleConstants.FILE_VIEWER);

		this.textProvider = textProvider;
		this.viewManager = viewManager;
		this.service = service;

		this.url = embeddedUrl;
		this.resizedElementId = "mollify-fileviewer-frame";
		this.fullUrl = fullUrl;

		viewerPanel = new FlowPanel();
		viewerPanel.getElement().setId("mollify-fileviewer-frame");
		viewerPanel.getElement().setAttribute("style", "overflow:auto");

		viewerPanel
				.setStylePrimaryName(StyleConstants.FILE_VIEWER_CONTENT_PANEL);
		viewerPanel.addStyleDependentName(StyleConstants.LOADING);

		initialize();
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		setMinimumSize(viewerPanel.getElement().getClientWidth(), viewerPanel
				.getElement().getClientHeight());
		DeferredCommand.addCommand(new Command() {
			@Override
			public void execute() {
				service.get(url, new ResultListener<JsObj>() {
					@Override
					public void onFail(ServiceError error) {
						viewerPanel.getElement().setInnerHTML(
								error.getDetails());
					}

					@Override
					public void onSuccess(JsObj result) {
						setContent(result);
					}
				});

			}
		});
	}

	private void setContent(JsObj result) {
		viewerPanel.removeStyleDependentName(StyleConstants.LOADING);
		viewerPanel.getElement().setInnerHTML(result.getString("html"));

		String resizedElementId = result.getString("resized_element_id");
		if (resizedElementId != null)
			this.resizedElementId = resizedElementId;

		String size = result.getString("size");
		if (size != null) {
			String[] s = size.split(";");
			int w = Integer.parseInt(s[0]);
			int h = Integer.parseInt(s[1]);
			setElementSize(w, h);
		} else {
			setElementSize(600, 400);
		}
		this.center();
	}

	@Override
	protected Element getSizedElement() {
		return DOM.getElementById(resizedElementId);
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
