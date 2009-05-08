/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.upload;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileUploadStatus;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.ProgressDisplayer;

import com.google.gwt.core.client.JavaScriptObject;

public class DefaultFileUploadListener implements FileUploadListener {
	private final TextProvider textProvider;
	private final boolean isProgressEnabled;
	private final FileUploadService service;
	private final DialogManager dialogManager;

	private FileUploadMonitor uploadMonitor;
	private ProgressDisplayer uploadListener;
	private final ResultListener listener;

	public DefaultFileUploadListener(FileUploadService service,
			boolean isProgressEnabled, DialogManager dialogManager,
			TextProvider textProvider, ResultListener listener) {
		this.service = service;
		this.isProgressEnabled = isProgressEnabled;
		this.dialogManager = dialogManager;
		this.textProvider = textProvider;
		this.listener = listener;
	}

	public void onUploadStarted(String uploadId, List<String> filenames) {
		String info = filenames.size() == 1 ? filenames.get(0) : textProvider
				.getMessages().uploadingNFilesInfo(filenames.size());

		uploadListener = dialogManager.openProgressDialog(textProvider
				.getStrings().fileUploadProgressTitle(), false);
		uploadListener.setInfo(info);
		uploadListener.setDetails(textProvider.getStrings()
				.fileUploadProgressPleaseWait());

		if (!isProgressEnabled)
			return;

		uploadMonitor = new FileUploadMonitor(uploadId,
				new FileUploadProgressListener() {
					public void onProgressUpdate(FileUploadStatus status) {
						int percentage = (int) status.getUploadedPercentage();
						uploadListener.setProgressBarVisible(true);
						uploadListener.setProgress(percentage);
						uploadListener.setDetails(String.valueOf(percentage)
								+ "%");
					}

					public void onProgressUpdateFail(ServiceError error) {
						uploadListener.setProgress(0);
						uploadMonitor.stop();
					}
				}, service);

		uploadMonitor.start();
	}

	public void onUploadFinished(JavaScriptObject result) {
		stopUploaders();
		listener.onSuccess(result);
	}

	public void onUploadFailed(ServiceError error) {
		stopUploaders();
		listener.onFail(error);
	}

	private void stopUploaders() {
		uploadListener.setProgress(100);
		uploadListener.setDetails("");
		uploadListener.onFinished();

		if (uploadMonitor != null)
			uploadMonitor.stop();
	}

}
