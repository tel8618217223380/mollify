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

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.swfupload.client.SWFUpload;
import org.swfupload.client.UploadBuilder;
import org.swfupload.client.SWFUpload.ButtonAction;
import org.swfupload.client.event.DebugHandler;
import org.swfupload.client.event.FileQueueErrorHandler;
import org.swfupload.client.event.FileQueuedHandler;
import org.swfupload.client.event.SWFUploadLoadedHandler;
import org.swfupload.client.event.UploadCompleteHandler;
import org.swfupload.client.event.UploadErrorHandler;
import org.swfupload.client.event.UploadProgressHandler;
import org.swfupload.client.event.UploadStartHandler;
import org.swfupload.client.event.UploadSuccessHandler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;

public class FlashFileUploadHandler implements UploadStartHandler,
		UploadSuccessHandler, UploadCompleteHandler, UploadErrorHandler,
		UploadProgressHandler, SWFUploadLoadedHandler, DebugHandler {
	private static final String UPLOADER_ID = "uploader-flash";

	private final UploadBuilder builder;
	private final ResultListener listener;

	private SWFUpload uploader;

	private FlashProgressDisplayer progressDisplayer;

	public FlashFileUploadHandler(SessionInfo session,
			FileUploadService service, ResultListener listener,
			String uploaderSrc, Directory directory) {
		this.listener = listener;

		builder = new UploadBuilder();
		builder.setDebug(true);
		builder.setUploadURL(service.getUploadUrl(directory));
		if (uploaderSrc != null)
			builder.setFlashURL(uploaderSrc);

		if (session.getSessionId() != null)
			builder.addPostParam("MOLLIFY_SESSION_ID", session.getSessionId());
		builder.setFilePostName(UPLOADER_ID);
		builder.setButtonAction(ButtonAction.SELECT_FILES);

		builder.setSWFUploadLoadedHandler(this);
		builder.setUploadStartHandler(this);
		builder.setUploadErrorHandler(this);
		builder.setUploadCompleteHandler(this);
		builder.setUploadProgressHandler(this);
		builder.setUploadSuccessHandler(this);
		builder.setDebugHandler(this);
	}

	public void initialize() {
		uploader = builder.build();
	}

	public void setButtonProperties(String elementId, int w, int h, String text) {
		builder.setButtonPlaceholderID(elementId);
		builder.setButtonWidth(w);
		builder.setButtonHeight(h);
		builder.setButtonText(text);
	}

	public void onUploadStart(UploadStartEvent e) {
		GWT.log("Upload start " + e.getFile().getName(), null);
		progressDisplayer.onUploadStarted();
	}

	public void onUploadSuccess(UploadSuccessEvent e) {
		GWT.log("Upload succeeded " + e.getFile().getName(), null);
		progressDisplayer.onUploadEnded();
	}

	public void onUploadComplete(UploadCompleteEvent e) {
		GWT.log("Upload completed " + e.getFile().getName(), null);
	}

	public void onUploadError(UploadErrorEvent e) {
		listener.onFail(new ServiceError(ServiceErrorType.UPLOAD_FAILED, e
				.getMessage()));
	}

	public void onUploadProgress(UploadProgressEvent e) {
		GWT.log("Upload progress " + e.getBytesTotal() + " / "
				+ e.getBytesComplete(), null);
	}

	public UploadBuilder getBuilder() {
		return builder;
	}

	public void startUpload() {
		uploader.startUpload();

	}

	public void removeFile(String id) {
		uploader.cancelUpload(id, false);
	}

	public void onSWFUploadLoaded() {
		GWT.log("Flash uploader loaded", null);
		Log.debug("Flash uploader loaded");
	}

	public void onDebug(DebugEvent e) {
		GWT.log("SWF DEBUG " + e.getMessage(), null);
		Log.debug("SWF DEBUG " + e.getMessage());
	}

	public void setFileQueueListener(final FileQueueListener listener) {
		builder.setFileQueuedHandler(new FileQueuedHandler() {
			public void onFileQueued(FileQueuedEvent event) {
				listener.onFileAdded(event.getFile());
			}
		});

		builder.setFileQueueErrorHandler(new FileQueueErrorHandler() {
			public void onFileQueueError(FileQueueErrorEvent e) {
				listener.onFileAddFailed(e.getFile(), e.getErrorCode(), e
						.getMessage());
			}
		});
	}

	public void setProgressDisplayer(FlashProgressDisplayer progressDisplayer) {
		this.progressDisplayer = progressDisplayer;
	}
}
