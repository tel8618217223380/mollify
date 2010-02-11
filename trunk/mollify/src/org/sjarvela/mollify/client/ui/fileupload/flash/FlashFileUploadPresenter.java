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

import org.sjarvela.mollify.client.filesystem.Folder;
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
import com.google.gwt.user.client.Timer;

public class FlashFileUploadPresenter implements UploadStartHandler,
		UploadSuccessHandler, UploadCompleteHandler, UploadErrorHandler,
		UploadProgressHandler, SWFUploadLoadedHandler, DebugHandler {
	private static final String SESSION_ID_PARAM = "session";
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

	private Timer progressTimer = null;
	private boolean updateProgress = true;

	public FlashFileUploadPresenter(SessionInfo session,
			FileUploadService service, ResultListener listener,
			String uploaderSrc, Folder directory, FlashFileUploadDialog dialog,
			TextProvider textProvider) {
		flashLoadTimer = new Timer() {
			@Override
			public void run() {
				onLoadFailed();
			}
		};
		flashLoadTimer.schedule(10 * 1000);

		this.listener = listener;
		this.dialog = dialog;
		this.textProvider = textProvider;
		this.allowedTypes = session.getFileSystemInfo()
				.getAllowedFileUploadTypes();
		this.uploader = createUploader(session, service, uploaderSrc, directory);
	}

	protected void onLoadFailed() {
		dialog.hide();
		listener
				.onFail(new ServiceError(
						ServiceErrorType.INVALID_CONFIGURATION,
						"Flash uploader initialization timeout, either uploader component is missing, it has wrong src url or browser cannot load flash components"));
	}

	public void setDemoMode() {
		this.demo = true;
	}

	private SWFUpload createUploader(SessionInfo session,
			FileUploadService service, String uploaderSrc, Folder directory) {
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
		if (!isUploading()) {
			files.remove(f);
			dialog.removeFile(f);
		} else {
			cancelFile(f);
		}
	}

	private void cancelFile(File f) {
		if (uploadModel.isCompleted(f))
			return;
		File current = uploadModel.getCurrentFile();
		uploadModel.cancelFile(f);

		dialog.cancelFile(f, uploadModel.getTotalBytes(), uploadModel
				.getTotalProgress(), uploadModel.getTotalPercentage());

		if (current != null && current.getId().equals(f.getId()))
			startNextFile();
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

		uploadModel.uploadComplete(files.get(0));
		dialog.onFileUploadCompleted(files.get(0));

		if (files.size() > 1) {
			uploadModel.nextFile();
			File file = files.get(1);
			dialog.onActiveUploadFileChanged(file);
			updateProgress(file, (file.getSize() / 100l) * 20l);
		}

		if (files.size() > 3)
			cancelFile(files.get(3));
	}

	public void onUploadStart(UploadStartEvent e) {
		Log.debug("Upload start " + e.getFile().getName());
		dialog.onActiveUploadFileChanged(e.getFile());
	}

	public void onUploadSuccess(UploadSuccessEvent e) {
		Log.debug("Upload succeeded " + e.getFile().getName());
	}

	public void onUploadComplete(UploadCompleteEvent e) {
		if (uploadModel == null)
			return;

		Log.debug("Upload completed " + e.getFile().getName());
		uploadModel.uploadComplete(e.getFile());
		dialog.onFileUploadCompleted(e.getFile());
		startNextFile();
	}

	private void startNextFile() {
		if (!uploadModel.hasNext()) {
			dialog.hide();
			stopUpload(false);
			listener.onSuccess(null);
			return;
		}
		startUpload(uploadModel.nextFile());
	}

	public void onUploadError(UploadErrorEvent e) {
		stopUpload(true);
		dialog.hide();
		listener.onFail(new ServiceError(ServiceErrorType.UPLOAD_FAILED, e
				.getMessage()));
	}

	public void onUploadProgress(UploadProgressEvent e) {
		if (!updateProgress)
			return;
		updateProgress = false;
		updateProgress(e.getFile(), e.getBytesComplete());
	}

	private void updateProgress(File file, long bytesComplete) {
		double percentage = uploadModel.getPercentage(bytesComplete, file
				.getSize());
		uploadModel.updateProgress(bytesComplete);

		if (Log.isDebugEnabled())
			Log.debug("Progress: file " + bytesComplete + "/" + file.getSize()
					+ "=" + percentage + ", total "
					+ uploadModel.getTotalProgress() + "="
					+ uploadModel.getTotalPercentage());
		dialog.setProgress(file, percentage, bytesComplete, uploadModel
				.getTotalPercentage(), uploadModel.getTotalProgress());
	}

	public void startUpload() {
		if (progressTimer != null) {
			progressTimer.cancel();
			progressTimer = null;
		}

		uploadModel = new UploadModel(files);
		dialog.onUploadStarted(uploadModel.getTotalBytes());

		progressTimer = new Timer() {
			@Override
			public void run() {
				updateProgress = true;
			}
		};
		progressTimer.scheduleRepeating(500);
		startUpload(uploadModel.nextFile());
	}

	private void stopUpload(boolean cancelFiles) {
		if (progressTimer != null) {
			progressTimer.cancel();
			progressTimer = null;
		}

		if (cancelFiles)
			for (File f : files)
				uploader.cancelUpload(f.getId(), false);

		uploadModel = null;
	}

	private void startUpload(File f) {
		if (f == null)
			return;
		uploader.startUpload(f.getId());
	}

	public void onSWFUploadLoaded() {
		Log.debug("Flash uploader loaded");
		if (flashLoadTimer != null) {
			flashLoadTimer.cancel();
			flashLoadTimer = null;
		}
		dialog.showUploadButton();
	}

	public void onDebug(DebugEvent e) {
		Log.debug(e.getMessage());
	}

	public void onCancel() {
		dialog.hide();
	}

	private boolean isUploading() {
		return uploadModel != null;
	}

	public void onCancelUpload() {
		stopUpload(true);
		dialog.hide();
	}
}
