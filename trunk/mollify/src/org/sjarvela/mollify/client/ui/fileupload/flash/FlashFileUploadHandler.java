/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.flash;

import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.swfupload.client.event.UploadCompleteHandler;
import org.swfupload.client.event.UploadErrorHandler;
import org.swfupload.client.event.UploadProgressHandler;
import org.swfupload.client.event.UploadStartHandler;
import org.swfupload.client.event.UploadSuccessHandler;

import com.google.gwt.core.client.GWT;

public class FlashFileUploadHandler implements UploadStartHandler,
		UploadSuccessHandler, UploadCompleteHandler, UploadErrorHandler,
		UploadProgressHandler {

	private final ResultListener listener;

	public FlashFileUploadHandler(ResultListener listener) {
		this.listener = listener;
	}

	public void onUploadStart(UploadStartEvent e) {
		GWT.log("Upload start", null);
	}

	public void onUploadSuccess(UploadSuccessEvent e) {
		GWT.log("Upload succeeded", null);
	}

	public void onUploadComplete(UploadCompleteEvent e) {
		GWT.log("Upload completed", null);
	}

	public void onUploadError(UploadErrorEvent e) {
		GWT.log("Upload error", null);
	}

	public void onUploadProgress(UploadProgressEvent e) {
		GWT.log("Upload progress " + e.getBytesTotal() + " / "
				+ e.getBytesComplete(), null);
	}

}
