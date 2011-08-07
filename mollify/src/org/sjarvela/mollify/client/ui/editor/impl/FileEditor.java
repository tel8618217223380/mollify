/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.editor.impl;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.ExternalService;
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

public class FileEditor extends ResizableDialog {
	private final TextProvider textProvider;
	private final ViewManager viewManager;
	private final ExternalService service;
	private final String url;
	private final String fullUrl;
	private String resizedElementId;

	private FlowPanel editorPanel;

	public FileEditor(TextProvider textProvider, ViewManager viewManager,
			ExternalService service, String title, String embeddedUrl,
			String fullUrl) {
		super(title, "mollify-file-editor");

		this.textProvider = textProvider;
		this.viewManager = viewManager;
		this.service = service;

		this.url = embeddedUrl;
		this.resizedElementId = "mollify-file-editor-content";
		this.fullUrl = fullUrl;

		editorPanel = new FlowPanel();
		editorPanel.getElement().setId("mollify-file-editor-content");
		editorPanel.getElement().setAttribute("style", "overflow:none");

		editorPanel.setStylePrimaryName("mollify-file-editor-content-panel");
		// editorPanel.addStyleDependentName(StyleConstants.LOADING);

		initialize();
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		setMinimumSize(editorPanel.getElement().getClientWidth(), editorPanel
				.getElement().getClientHeight());
		setElementSize(600, 400);
		editorPanel
				.getElement()
				.setInnerHTML(
						"<iframe id=\"editor-frame\" src=\""
								+ url
								+ "\" width=\"100%\" height:\"100%\" style=\"width:100%;height:100%;border: none;overflow:none;\"></iframe>");
		this.center();
		// Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		// @Override
		// public void execute() {
		// service.get(url, new ResultListener<JsObj>() {
		// @Override
		// public void onFail(ServiceError error) {
		// editorPanel.getElement().setInnerHTML(
		// error.getDetails());
		// }
		//
		// @Override
		// public void onSuccess(JsObj result) {
		// setContent(result);
		// }
		// });
		//
		// }
		// });
	}

	// private void setContent(JsObj result) {
	// editorPanel.removeStyleDependentName(StyleConstants.LOADING);
	// editorPanel.getElement().setInnerHTML(result.getString("html"));
	//
	// String resizedElementId = result.getString("resized_element_id");
	// if (resizedElementId != null)
	// this.resizedElementId = resizedElementId;
	//
	// String size = result.getString("size");
	// if (size != null) {
	// String[] s = size.split(";");
	// int w = Integer.parseInt(s[0]);
	// int h = Integer.parseInt(s[1]);
	// setElementSize(w, h);
	// } else {
	// setElementSize(600, 400);
	// }
	// this.center();
	// }

	@Override
	protected Element getSizedElement() {
		return DOM.getElementById(resizedElementId);
	}

	@Override
	protected Widget createContent() {
		Panel p = new FlowPanel();
		p.setStylePrimaryName(StyleConstants.FILE_VIEWER_CONTENT);
		p.add(editorPanel);
		p.add(createTools());
		return p;
	}

	private Widget createTools() {
		Panel p = new FlowPanel();
		p.setStylePrimaryName(StyleConstants.FILE_VIEWER_HEADER);

		if (fullUrl != null) {
			// Button openInNewWindowButton = createButton(
			// textProvider.getText(Texts.fileViewerOpenInNewWindowTitle),
			// new ClickHandler() {
			// public void onClick(ClickEvent event) {
			// viewManager.openUrlInNewWindow(fullUrl);
			// FileEditor.this.hide();
			// }
			// }, StyleConstants.FILE_VIEWER_BUTTON_OPEN);
			// p.add(openInNewWindowButton);
		}

		Button saveButton = createButton(
				textProvider.getText(Texts.fileEditorSave), new ClickHandler() {
					public void onClick(ClickEvent event) {
						FileEditor.this.onSave();
					}
				}, "file-editor-save");
		p.add(saveButton);

		Button closeButton = createButton(
				textProvider.getText(Texts.dialogCloseButton),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						FileEditor.this.hide();
					}
				}, StyleConstants.FILE_VIEWER_BUTTON_CLOSE);
		p.add(closeButton);

		return p;
	}

	protected native final void onSave() /*-{
		$wnd.document.getElementById('editor-frame').contentWindow
				.onEditorSave();
	}-*/;
}
