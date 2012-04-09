/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.component.preview;

/*import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.ExternalService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextContainer;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextSection;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class PreviewComponent implements ItemContextSection {
	private final TextProvider textProvider;
	private final ExternalService service;

	private Widget component;
	private boolean initalized = false;
	private FileDetails details;

	public PreviewComponent(TextProvider textProvider, ExternalService service) {
		this.textProvider = textProvider;
		this.service = service;
	}

	@Override
	public Comparable getIndex() {
		return 4;
	}

	@Override
	public String getTitle() {
		return textProvider.getText(Texts.filePreviewTitle);
	}

	@Override
	public Widget getComponent() {
		if (component == null)
			component = createContent();
		return component;
	}

	private Widget createContent() {
		FlowPanel content = new FlowPanel();
		content.setStylePrimaryName(StyleConstants.FILE_CONTEXT_PREVIEW_CONTENT);
		content.addStyleDependentName(StyleConstants.LOADING);
		return content;
	}

	@Override
	public boolean onInit(ItemContextContainer container, FileSystemItem item,
			ItemDetails details) {
		this.details = details.cast();
		return hasPreview();
	}

	private boolean hasPreview() {
		return this.details != null
				&& this.details.getFileViewerEditor() != null
				&& this.details.getFileViewerEditor().hasValue("preview");
	}

	@Override
	public void onOpen(FileSystemItem item, ItemDetails details) {
		if (initalized || !hasPreview())
			return;
		initalized = true;

		service.get(this.details.getFileViewerEditor().getAsString("preview"),
				new ResultListener<JsObj>() {
					@Override
					public void onFail(ServiceError error) {
						component.getElement().setInnerHTML(
								error.getType().getMessage(textProvider));
					}

					@Override
					public void onSuccess(JsObj result) {
						component
								.removeStyleDependentName(StyleConstants.LOADING);
						component.getElement().setInnerHTML(
								result.getString("html"));
					}
				});
	}

	@Override
	public void onClose() {
	}

	@Override
	public void onContextClose() {
	}
}*/
