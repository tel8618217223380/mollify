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
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.UrlResolver;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dialog.DialogMoveListener;
import org.sjarvela.mollify.client.util.FileUtil;
import org.sjarvela.mollify.client.util.JsUtil;

import plupload.client.File;
import plupload.client.Plupload;
import plupload.client.PluploadBuilder;
import plupload.client.PluploadListener;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class PluploaderPresenter implements PluploadListener {
	private static final String PARAM_PLUPLOAD_RUNTIMES = "plupload-runtimes";
	private static final String PARAM_PLUPLOAD_CHUNK_SIZE = "plupload-chunk-size";

	private static final String FLASH_FILE_NAME = "plupload.flash.swf";
	private static final String SILVERLIGHT_FILE_NAME = "plupload.silverlight.xap";

	private final ClientSettings settings;
	private final DialogManager dialogManager;
	private final TextProvider textProvider;
	private final ResultListener listener;
	private final UrlResolver urlResolver;
	private final PluploaderDialog dialog;

	private final List<File> files = new ArrayList();
	private final Plupload uploader;
	private UploadModel uploadModel;
	private final List<String> allowedTypes;

	private boolean demo = false;

	public PluploaderPresenter(SessionInfo session, FileUploadService service,
			UrlResolver urlResolver, ResultListener listener, Folder directory,
			PluploaderDialog dialog, DialogManager dialogManager,
			TextProvider textProvider, ClientSettings settings) {
		this.urlResolver = urlResolver;
		this.listener = listener;
		this.dialog = dialog;
		this.dialogManager = dialogManager;
		this.textProvider = textProvider;
		this.settings = settings;
		this.allowedTypes = session.getFileSystemInfo()
				.getAllowedFileUploadTypes();
		this.uploader = createUploader(session, service, directory);

		dialog.addMoveListener(new DialogMoveListener() {
			@Override
			public void onDialogMoved() {
				uploader.refresh();
			}
		});
	}

	public void setDemoMode() {
		this.demo = true;
	}

	public void init() {
		try {
			uploader.init();
		} catch (JavaScriptException e) {
			Log.debug(e.getDescription());
		}
	}

	private Plupload createUploader(SessionInfo session,
			FileUploadService service, Folder folder) {
		String uploadUrl = service.getUploadUrl(folder)
				+ "?format=binary&uploader=plupload&session="
				+ session.getSessionId();

		PluploadBuilder builder = new PluploadBuilder().uploadUrl(uploadUrl)
				.browseButton(dialog.getBrowseButtonId()).listener(this);

		if (allowedTypes.size() > 0)
			builder.filter(textProvider.getStrings()
					.fileUploadDialogSelectFileTypesDescription(),
					getFileTypeList());

		addRuntimes(builder);

		String chunk = settings.getString(PARAM_PLUPLOAD_CHUNK_SIZE);
		if (chunk != null)
			builder.chunk(chunk);

		if (Log.isDebugEnabled())
			Log.debug("Pluploader: "
					+ JsUtil.asJsonString(builder.getSettings()));
		return builder.create();
	}

	private void addRuntimes(PluploadBuilder builder) {
		String runtimes = settings.getString(PARAM_PLUPLOAD_RUNTIMES);
		if (runtimes == null)
			runtimes = "gears, browserplus, flash, silverlight, html5, html4";

		for (String r : runtimes.split(",")) {
			final String runtime = r.toLowerCase().trim();

			if (runtime.equals("gears") || runtime.equals("html5")
					|| runtime.equals("html4") || runtime.equals("browserplus")) {
				builder.runtime(runtime);
			} else if (runtime.equals("flash")) {
				builder.runtime("flash").flashUrl(getUrl(FLASH_FILE_NAME));
			} else if (runtime.equals("silverlight")) {
				builder.runtime("silverlight").silverlightUrl(
						getUrl(SILVERLIGHT_FILE_NAME));
			} else {
				DeferredCommand.addCommand(new Command() {
					@Override
					public void execute() {
						dialogManager.showError(new ServiceError(
								ServiceErrorType.INVALID_CONFIGURATION,
								"Invalid plupload runtime: " + runtime));
					}
				});
			}
		}
	}

	private String getUrl(String file) {
		String url = urlResolver.getRelativeModuleUrl(file);
		if (url == null)
			return urlResolver.getModuleUrl(file, false);
		return url;
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
	}

	private void updateProgress(File file) {
		double percentage = ((double) file.getLoaded() / (double) file
				.getSize()) * 100d;
		uploadModel.updateProgress(file.getLoaded());

		if (Log.isDebugEnabled())
			Log.debug("Progress: file " + percentage + "%, total "
					+ uploadModel.getTotalProgress() + "="
					+ uploadModel.getTotalPercentage() + "%");
		dialog.setProgress(file, percentage, file.getLoaded(), uploadModel
				.getTotalPercentage(), uploadModel.getTotalProgress());
	}

	public void startUpload() {
		uploadModel = new UploadModel(files);
		dialog.onUploadStarted(uploadModel.getTotalBytes());
		uploader.start();
	}

	private void stopUpload() {
		uploader.stop();
		uploadModel = null;
	}

	private void complete(File file) {
		if (!uploadModel.complete(file))
			return;
		dialog.onFileUploadCompleted(file);

		if (uploadModel.allComplete()) {
			Log.debug("Upload complete");
			stopUpload();
			dialog.hide();
			listener.onSuccess(null);
			return;
		}
	}

	public void onCancel() {
		Log.debug("Upload cancelled");
		dialog.hide();
	}

	private boolean isUploading() {
		return uploadModel != null;
	}

	public void onCancelUpload() {
		Log.debug("Upload cancelled");
		stopUpload();
		dialog.hide();
	}

	@Override
	public void onInit(Plupload p, String runtime) {
		Log.debug("Plupload init, runtime=" + runtime);
	}

	@Override
	public void onFilesAdded(Plupload p, List<File> files) {
		Log.debug("Files added: " + files.size());
		for (File file : files)
			onAddFile(file);
	}

	@Override
	public void onFilesRemoved(Plupload uploader, List<File> files) {
		Log.debug("Files removed: " + files.size());
	}

	@Override
	public void onFileUpload(Plupload uploader, File file) {
		Log.debug("File upload started: " + file.getName());
		uploadModel.start(file);
		dialog.onActiveUploadFileChanged(file);
	}

	@Override
	public void onFileUploaded(Plupload p, File file, JavaScriptObject response) {
		Log.debug("File uploaded: " + file.getName());
		complete(file);
	}

	@Override
	public void onFileUploadProgress(Plupload uploader, File file) {
		if (file == null)
			return;
		Log.debug("File upload progress: " + JsUtil.asJsonString(file));
		updateProgress(file);
	}

	@Override
	public void onChunkUploaded(Plupload p, File file, JavaScriptObject response) {
		Log.debug("Chunk uploaded: " + file.getName());
	}

	@Override
	public void onQueueChanged(Plupload uploader) {
		Log.debug("Queue changed");
	}

	@Override
	public void onRefresh(Plupload uploader) {
		Log.debug("Refresh");
	}

	@Override
	public void onStateChanged(Plupload uploader) {
		Log.debug("State changed");
	}

	@Override
	public void postInit(Plupload uploader) {
		Log.debug("Post init");
	}

	@Override
	public void onError(Plupload p, JavaScriptObject error) {
		Log.debug("Error: " + JsUtil.asJsonString(error));
	}
}
