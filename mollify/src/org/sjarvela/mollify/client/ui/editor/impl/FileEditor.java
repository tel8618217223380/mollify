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
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.ui.common.dialog.ResizableDialog;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

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
	private final DialogManager dialogManager;
	private final ExternalService service;
	private final String url;
	private final String fullUrl;
	private String resizedElementId;

	private FlowPanel editorPanel;
	private FlowPanel progress;

	public FileEditor(TextProvider textProvider, ViewManager viewManager,
			DialogManager dialogManager, ExternalService service, String title,
			String embeddedUrl, String fullUrl) {
		super(title, "mollify-file-editor");

		this.textProvider = textProvider;
		this.viewManager = viewManager;
		this.dialogManager = dialogManager;
		this.service = service;

		this.url = embeddedUrl;
		this.resizedElementId = "mollify-file-editor-content";
		this.fullUrl = fullUrl;

		this.progress = new FlowPanel();
		this.progress.setStylePrimaryName("mollify-file-editor-progress");
		this.progress.setVisible(false);

		editorPanel = new FlowPanel();
		editorPanel.getElement().setId("mollify-file-editor-content");
		editorPanel.getElement().setAttribute("style", "overflow:none");

		editorPanel.setStylePrimaryName("mollify-file-editor-content-panel");

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
								+ "\" width=\"100%\" height:\"100%\" style=\"width:100%;height:100%;border: none;overflow: none;\"></iframe>");
		this.center();
	}

	@Override
	protected Element getSizedElement() {
		return DOM.getElementById(resizedElementId);
	}

	@Override
	protected Widget createContent() {
		Panel p = new FlowPanel();
		p.setStylePrimaryName("mollify-file-editor-content");
		p.add(editorPanel);
		p.add(createTools());
		p.add(progress);
		return p;
	}

	private Widget createTools() {
		Panel p = new FlowPanel();
		p.setStylePrimaryName("mollify-file-editor-header");

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

	protected void onSave() {
		progress.setVisible(true);
		invokeSave(this);
	}

	protected native final void invokeSave(FileEditor e) /*-{
		var s = function() {
			e.@org.sjarvela.mollify.client.ui.editor.impl.FileEditor::onSaveSuccess()();
		}
		var e = function(c, e) {
			e.@org.sjarvela.mollify.client.ui.editor.impl.FileEditor::onSaveFail(ILjava/lang/String;)(c, e);
		}

		$wnd.document.getElementById('editor-frame').contentWindow
				.onEditorSave(s, e);
	}-*/;

	public void onSaveSuccess() {
		this.hide();
	};

	public void onSaveFail(int code, String error) {
		dialogManager.showError(new ServiceError(ServiceErrorType
				.fromCode(code), error));
		this.hide();
	};

}
