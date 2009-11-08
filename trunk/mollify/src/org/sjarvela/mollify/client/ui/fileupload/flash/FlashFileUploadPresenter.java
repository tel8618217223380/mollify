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
import org.sjarvela.mollify.client.localization.TextProvider;
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
import com.google.gwt.user.client.Timer;

public class FlashFileUploadPresenter implements UploadStartHandler,
		UploadSuccessHandler, UploadCompleteHandler, UploadErrorHandler,
		UploadProgressHandler, SWFUploadLoadedHandler, DebugHandler {
	private static final String SESSION_ID_PARAM = "MOLLIFY_SESSION_ID";
	private static final String UPLOADER_ID = "uploader-flash";

	private final TextProvider textProvider;
	private final ResultListener listener;
	private final SWFUpload uploader;
	private final FlashFileUploadDialog dialog;
	private final List<File> files = new ArrayList();
	private final List<String> allowedTypes;

	private UploadModel uploadModel;
	private boolean demo = false;
	private Timer flashLoadTimer;

	public FlashFileUploadPresenter(SessionInfo session,
			FileUploadService service, ResultListener listener,
			String uploaderSrc, Directory directory,
			FlashFileUploadDialog dialog, TextProvider textProvider) {
		this.listener = listener;
		this.dialog = dialog;
		this.textProvider = textProvider;
		this.allowedTypes = session.getFileSystemInfo()
				.getAllowedFileUploadTypes();
		this.uploader = createUploader(session, service, uploaderSrc, directory);

		flashLoadTimer = new Timer() {
			@Override
			public void run() {
				onLoadFailed();
			}
		};
		flashLoadTimer.schedule(10 * 1000);
	}

	protected void onLoadFailed() {
		dialog.hide();
		listener
				.onFail(new ServiceError(
						ServiceErrorType.INVALID_CONFIGURATION,
						"Flash uploader initialization timeout, either it is missing, it has wrong src url or browser cannot load flash components"));
	}

	public void setDemoMode() {
		this.demo = true;
	}

	private SWFUpload createUploader(SessionInfo session,
			FileUploadService service, String uploaderSrc, Directory directory) {
		UploadBuilder builder = new UploadBuilder();
		builder.setDebug(true);
		builder.setUploadURL(service.getUploadUrl(directory));
		if (uploaderSrc != null)
			builder.setFlashURL(uploaderSrc);

		if (session.getSessionId() != null)
			builder.addPostParam(SESSION_ID_PARAM, session.getSessionId());
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
			builder.setFileTypesDescription(textProvider.getStrings()
					.fileUploadDialogSelectFileTypesDescription());
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
		if (files.isEmpty())
			return;

		if (demo) {
			startDemoMode();
			return;
		}
		startUpload();
	}

	protected void startDemoMode() {
		uploadModel = new UploadModel(files);
		dialog.onUploadStarted(uploadModel.getTotalBytes());

		if (files.size() == 0)
			return;

		dialog.onFileUploadCompleted(files.get(0));
		if (files.size() > 1) {
			File file = files.get(1);
			dialog.onActiveUploadFileChanged(file);
			dialog.setProgress(file, 20.0d, (file.getSize() / 100l) * 20l, 25d,
					(uploadModel.getTotalBytes() / 100l) * 25l);
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
		uploadModel.uploadComplete(e.getFile());
		dialog.onFileUploadCompleted(e.getFile());

		if (!uploadModel.hasNext()) {
			dialog.onUploadEnded();
			listener.onSuccess(null);
			return;
		}
		startUpload(uploadModel.nextFile());
	}

	public void onUploadError(UploadErrorEvent e) {
		dialog.hide();
		listener.onFail(new ServiceError(ServiceErrorType.UPLOAD_FAILED, e
				.getMessage()));
	}

	public void onUploadProgress(UploadProgressEvent e) {
		double percentage = 0d;
		if (e.getBytesTotal() > 0d && e.getBytesComplete() > 0d)
			percentage = (((double) e.getBytesComplete() / (double) e
					.getBytesTotal()) * 100d);
		long totalProgress = uploadModel.getTotalProgress(e.getBytesComplete());
		double totalPercentage = (((double) totalProgress / (double) uploadModel
				.getTotalBytes()) * 100d);
		if (Log.isDebugEnabled())
			Log.debug("Progress: file " + e.getBytesComplete() + "/"
					+ e.getBytesTotal() + "=" + percentage + ", total "
					+ totalProgress + "=" + totalPercentage);
		dialog.setProgress(e.getFile(), percentage, e.getBytesComplete(),
				totalPercentage, totalProgress);
	}

	public void startUpload() {
		uploadModel = new UploadModel(files);
		dialog.onUploadStarted(uploadModel.getTotalBytes());
		startUpload(uploadModel.nextFile());
	}

	private void startUpload(File f) {
		if (f == null)
			return;
		uploader.startUpload(f.getId());
	}

	public void onSWFUploadLoaded() {
		flashLoadTimer.cancel();
		flashLoadTimer = null;
		dialog.showUploadButton();
		GWT.log("Flash uploader loaded", null);
		Log.debug("Flash uploader loaded");
	}

	public void onDebug(DebugEvent e) {
		GWT.log("SWF DEBUG " + e.getMessage(), null);
		Log.debug("SWF DEBUG " + e.getMessage());
	}

}
