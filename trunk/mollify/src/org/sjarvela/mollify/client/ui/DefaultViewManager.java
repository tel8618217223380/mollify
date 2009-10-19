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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

@Singleton
public class DefaultViewManager implements ViewManager {
	private static final String FILEMANAGER_DOWNLOAD_PANEL_ID = "mollify-download-panel";
	private static final String FILEMANAGER_DOWNLOAD_FRAME_ID = "mollify-download-frame";

	private final RootPanel rootPanel;

	public DefaultViewManager(RootPanel rootPanel) {
		this.rootPanel = rootPanel;
	}

	public void openView(Widget mainView) {
		empty();
		rootPanel.add(mainView);
		rootPanel.add(createDownloadFrame());
	}

	public void empty() {
		rootPanel.clear();
	}

	private Widget createDownloadFrame() {
		SimplePanel downloadPanel = new SimplePanel();
		downloadPanel.getElement().setId(FILEMANAGER_DOWNLOAD_PANEL_ID);
		downloadPanel.getElement().setAttribute("style",
				"visibility:collapse; height: 0px;");

		Element downloadFrame = DOM.createIFrame();
		downloadFrame
				.setAttribute("style", "visibility:collapse; height: 0px;");
		downloadFrame.setId(FILEMANAGER_DOWNLOAD_FRAME_ID);
		downloadPanel.getElement().appendChild(downloadFrame);

		return downloadPanel;
	}

	public void openDownloadUrl(String url) {
		setFrameUrl(FILEMANAGER_DOWNLOAD_FRAME_ID, url);
	}

	public void openUrlInNewWindow(String url) {
		Window.open(url, "_blank", "");
	}

	public void showPlainError(String error) {
		empty();
		rootPanel.add(new HTML(error));
	}

	/* UTILITIES */

	private native void setFrameUrl(String id, String url) /*-{
		$doc.getElementById(id).src=url;
	}-*/;

}
