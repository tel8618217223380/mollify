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
import plupload.client.Pluploader;
import plupload.client.PluploaderBuilder;
import plupload.client.PluploaderListener;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;

public class PluploaderPresenter implements PluploaderListener {
	// private static final String SESSION_ID_PARAM = "MOLLIFY_SESSION_ID";
	private static final String FLASH_FILE_NAME = "plupload.flash.swf";
	private static final String SILVERLIGHT_FILE_NAME = "plupload.silverlight.xap";
	// private static final String UPLOADER_ID = "uploader-flash";

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
		// this.textProvider = textProvider;
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
		String uploadUrl = service.getUploadUrl(folder)
				+ "?raw=1&uploader=plupload&session=" + session.getSessionId();

		return new PluploaderBuilder().runtimes(
				"gears,flash,html5,silverlight,browserplus").flashUrl(
				getUrl(FLASH_FILE_NAME)).silverlightUrl(
				getUrl(SILVERLIGHT_FILE_NAME)).uploadUrl(uploadUrl)
				.allowedFileTypes(getFileTypeList()).browseButton(
						dialog.getBrowseButtonId()).chunk("1mb").listener(this)
				.create();
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
			// cancelFile(f);
		}
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

		if (files.size() > 1) {
			File current = files.get(0);
			File next = files.get(1);

			uploadModel.start(next);
			dialog.onFileUploadCompleted(current);

			File file = next;
			dialog.onActiveUploadFileChanged(file);
			updateProgress(File.create(file.getId(), file.getName(), file
					.getSize(), file.getSize() / 100 * 20));
		}

		// if (files.size() > 3)
		// cancelFile(files.get(3));
	}

	private void updateProgress(File file) {
		double percentage = file.getPercent();
		uploadModel.updateProgress(file.getLoaded());

		if (Log.isDebugEnabled())
			Log.debug("Progress: file " + file.getLoaded() + "/"
					+ file.getSize() + "=" + percentage + ", total "
					+ uploadModel.getTotalProgress() + "="
					+ uploadModel.getTotalPercentage());
		dialog.setProgress(file, percentage, file.getLoaded(), uploadModel
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
		uploader.start();
	}

	private void stopUpload(boolean cancelFiles) {
		if (progressTimer != null) {
			progressTimer.cancel();
			progressTimer = null;
		}

		uploader.stop();
		uploadModel = null;
	}

	private void finish(File file) {
		uploadModel.complete(file);
		dialog.onFileUploadCompleted(file);
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
	public void onInit(Pluploader p, String runtime) {
		Log.debug("Plupload init, runtime=" + runtime);
	}

	@Override
	public void onFilesAdded(Pluploader p, List<File> files) {
		Log.debug("Files added: " + files.size());
		for (File file : files)
			onAddFile(file);
	}

	@Override
	public void onFilesRemoved(Pluploader uploader, List<File> files) {
		Log.debug("Files removed: " + files.size());
	}

	@Override
	public void onFileUpload(Pluploader uploader, File file) {
		Log.debug("File upload started: " + file.getName());
		uploadModel.start(file);
	}

	@Override
	public void onFileUploadProgress(Pluploader uploader, File file) {
		Log.debug("File upload progress: " + file.getName() + " "
				+ file.getLoaded() + "/" + file.getSize() + " "
				+ file.getPercent());
		if (file.getPercent() == 100)
			finish(file);
		if (!updateProgress)
			return;
		updateProgress = false;
		updateProgress(file);
	}

	@Override
	public void onQueueChanged(Pluploader uploader) {
		Log.debug("Queue changed");
	}

	@Override
	public void onRefresh(Pluploader uploader) {
		Log.debug("Refresh");
	}

	@Override
	public void onStateChanged(Pluploader uploader) {
		Log.debug("State changed");
	}

	@Override
	public void postInit(Pluploader uploader) {
		Log.debug("Post init");
	}
}
