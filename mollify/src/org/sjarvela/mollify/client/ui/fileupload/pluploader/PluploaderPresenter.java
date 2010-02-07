/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.pluploader;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.UrlResolver;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.util.FileUtil;

import plupload.client.File;
import plupload.client.InitParams;
import plupload.client.Pluploader;
import plupload.client.PluploaderBuilder;
import plupload.client.PluploaderListener;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;

public class PluploaderPresenter implements PluploaderListener {
	private static final String SESSION_ID_PARAM = "MOLLIFY_SESSION_ID";
	private static final String FLASH_FILE_NAME = "plupload.flash.swf";
	private static final String SILVERLIGHT_FILE_NAME = "plupload.silverlight.xap";
	// private static final String UPLOADER_ID = "uploader-flash";

	private final TextProvider textProvider;
	private final ResultListener listener;
	private final UrlResolver urlResolver;
	private final Pluploader uploader;
	private final PluploaderDialog dialog;
	private final List<File> files = new ArrayList();
	private final List<String> allowedTypes;

	private UploadModel uploadModel;
	private boolean demo = false;

	private Timer progressTimer = null;
	private boolean updateProgress = true;

	public PluploaderPresenter(SessionInfo session, FileUploadService service,
			UrlResolver urlResolver, ResultListener listener, Folder directory,
			PluploaderDialog dialog, TextProvider textProvider) {
		this.urlResolver = urlResolver;
		this.listener = listener;
		this.dialog = dialog;
		this.textProvider = textProvider;
		this.allowedTypes = session.getFileSystemInfo()
				.getAllowedFileUploadTypes();
		this.uploader = createUploader(session, service, directory);
	}

	public void setDemoMode() {
		this.demo = true;
	}

	public void init() {
		uploader.init();
	}

	private Pluploader createUploader(SessionInfo session,
			FileUploadService service, Folder folder) {
		return new PluploaderBuilder().runtimes(
				"gears,html5,flash,silverlight,browserplus").flashUrl(
				getUrl(FLASH_FILE_NAME)).silverlightUrl(
				getUrl(SILVERLIGHT_FILE_NAME)).uploadUrl(
				service.getUploadUrl(folder)).allowedFileTypes(
				getFileTypeList()).browseButton(dialog.getBrowseButtonId())
				.listener(this).create();
	}

	private String getUrl(String file) {
		return urlResolver.getModuleUrl(file, false);
	}

	private String getFileTypeList() {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (String type : allowedTypes) {
			if (!first)
				result.append(",");
			result.append(type);
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
		uploader.removeFile(f);
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

	private void startNextFile() {
		if (!uploadModel.hasNext()) {
			dialog.hide();
			stopUpload(false);
			listener.onSuccess(null);
			return;
		}
		startUpload(uploadModel.nextFile());
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

		uploader.stop();
		uploadModel = null;
	}

	private void startUpload(File f) {
		if (f == null)
			return;
		uploader.start();
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

	@Override
	public void onInit(InitParams p) {
		Log.debug("Plupload init, runtime=" + p.getRuntime());
	}
}
