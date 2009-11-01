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

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.util.FileUtil;
import org.swfupload.client.File;
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

public class FlashFileUploadPresenter implements UploadStartHandler,
		UploadSuccessHandler, UploadCompleteHandler, UploadErrorHandler,
		UploadProgressHandler, SWFUploadLoadedHandler, DebugHandler {
	private static final String UPLOADER_ID = "uploader-flash";

	private final ResultListener listener;
	private final SWFUpload uploader;
	private final FlashFileUploadDialog dialog;
	private final List<File> files = new ArrayList();
	private final List<String> allowedTypes;

	private File current = null;

	public FlashFileUploadPresenter(SessionInfo session,
			FileUploadService service, ResultListener listener,
			String uploaderSrc, Directory directory,
			FlashFileUploadDialog dialog) {
		this.listener = listener;
		this.dialog = dialog;
		this.allowedTypes = session.getFileSystemInfo()
				.getAllowedFileUploadTypes();
		this.uploader = createUploader(session, service, uploaderSrc, directory);
	}

	private SWFUpload createUploader(SessionInfo session,
			FileUploadService service, String uploaderSrc, Directory directory) {
		UploadBuilder builder = new UploadBuilder();
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

		if (!allowedTypes.isEmpty()) {
			builder.setFileTypes(getFileTypeList());
			builder.setFileTypesDescription("");
		}

		builder.setFileQueuedHandler(new FileQueuedHandler() {
			public void onFileQueued(FileQueuedEvent event) {
				onAddFile(event.getFile());
			}
		});

		builder.setFileQueueErrorHandler(new FileQueueErrorHandler() {
			public void onFileQueueError(FileQueueErrorEvent e) {
				GWT.log("File adding failed: " + e.getFile().getName() + " ("
						+ e.getErrorCode() + ") " + e.getMessage(), null);
				Log.debug("File adding failed: " + e.getFile().getName() + " ("
						+ e.getErrorCode() + ") " + e.getMessage());
			}
		});

		dialog.setVisualProperties(builder);

		return builder.build();
	}

	private String getFileTypeList() {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (String type : allowedTypes) {
			if (!first)
				result.append(";");
			result.append("*.").append(type);
			first = false;
		}
		return result.toString();
	}

	protected void onAddFile(File file) {
		if (!allowedTypes.isEmpty()
				&& !allowedTypes
						.contains(FileUtil.getExtension(file.getName())))
			return;
		for (File f : files)
			if (f.getName().equals(file.getName()))
				return;
		files.add(file);
		dialog.addFile(file);
	}

	public void onRemoveFile(File f) {
		uploader.cancelUpload(f.getId(), false);
		files.remove(f);
		dialog.removeFile(f);
	}

	public void onStartUpload() {
		setDemoMode();
	}

	protected void setDemoMode() {
		dialog.onUploadStarted();

		if (files.size() == 0)
			return;

		dialog.onFileUploadCompleted(files.get(0));
		if (files.size() > 1) {
			dialog.onActiveUploadFileChanged(files.get(1));
			dialog.setProgress(files.get(1), 20.0d);
		}
	}

	public void onUploadStart(UploadStartEvent e) {
		GWT.log("Upload start " + e.getFile().getName(), null);
		dialog.onActiveUploadFileChanged(e.getFile());
	}

	public void onUploadSuccess(UploadSuccessEvent e) {
		GWT.log("Upload succeeded " + e.getFile().getName(), null);
	}

	public void onUploadComplete(UploadCompleteEvent e) {
		GWT.log("Upload completed " + e.getFile().getName(), null);
		dialog.onFileUploadCompleted(e.getFile());

		File nextFile = getNextFile();
		if (nextFile == null) {
			dialog.onUploadEnded();
			listener.onSuccess(null);
			return;
		}
		startFileUpload(nextFile);
	}

	public void onUploadError(UploadErrorEvent e) {
		dialog.hide();
		listener.onFail(new ServiceError(ServiceErrorType.UPLOAD_FAILED, e
				.getMessage()));
	}

	public void onUploadProgress(UploadProgressEvent e) {
		double percentage = 0;
		if (e.getBytesTotal() > 0 && e.getBytesComplete() > 0)
			percentage = (((double) e.getBytesTotal() / (double) e
					.getBytesComplete()) * 100d);
		dialog.setProgress(e.getFile(), percentage);
	}

	public void startUpload() {
		dialog.onUploadStarted();
		current = null;
		startFileUpload(getNextFile());
	}

	private File getNextFile() {
		if (files.size() == 0)
			return null;
		int index = 0;
		if (current != null)
			index = files.indexOf(current) + 1;
		if (index <= files.size())
			return null;
		return files.get(index);
	}

	private void startFileUpload(File f) {
		if (f == null)
			return;
		uploader.startUpload(f.getId());
	}

	public void onSWFUploadLoaded() {
		GWT.log("Flash uploader loaded", null);
		Log.debug("Flash uploader loaded");
	}

	public void onDebug(DebugEvent e) {
		GWT.log("SWF DEBUG " + e.getMessage(), null);
		Log.debug("SWF DEBUG " + e.getMessage());
	}

}
