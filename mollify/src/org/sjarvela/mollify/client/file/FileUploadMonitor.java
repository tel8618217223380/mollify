/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.FileUploadStatus;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;

class FileUploadMonitor {
	static int INTERVAL = 1000;

	private final FileUploadHandler uploadHandler;
	private final Timer timer;
	private final String uploadId;
	private final ProgressListener listener;

	private boolean stop = false;

	public FileUploadMonitor(String uploadId, ProgressListener listener,
			FileUploadHandler uploadHandler) {

		this.uploadId = uploadId;
		this.listener = listener;
		this.uploadHandler = uploadHandler;

		timer = new Timer() {
			public void run() {
				if (!stop)
					onTimer();
			}
		};
	}

	public void start() {
		timer.schedule(INTERVAL);
	}

	public void stop() {
		stop = true;
		timer.cancel();
	}

	private void onTimer() {
		uploadHandler.getUploadProgress(uploadId, new ResultListener() {
			public void onFail(ServiceError error) {
				listener.onProgressUpdateFail(error);
			}

			public void onSuccess(JavaScriptObject... result) {
				FileUploadStatus status = result[0].cast();
				listener.onProgressUpdate(status);
				if (!stop)
					timer.schedule(INTERVAL);
			}
		});

	}
}