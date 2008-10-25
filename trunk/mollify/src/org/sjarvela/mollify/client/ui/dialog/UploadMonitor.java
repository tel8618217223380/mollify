/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.data.UploadStatus;
import org.sjarvela.mollify.client.file.FileUploadHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;

class UploadMonitor {
	static int INTERVAL = 1000;

	private final FileUploadHandler uploadHandler;
	private final Timer timer;
	private final String uploadId;
	private final ProgressListener listener;

	private boolean stop = false;

	public UploadMonitor(String uploadId, ProgressListener listener,
			FileUploadHandler uploadHandler) {

		this.uploadId = uploadId;
		this.listener = listener;
		this.uploadHandler = uploadHandler;

		timer = new Timer() {
			public void run() {
				GWT.log("MONITOR TIMER", null);
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
		uploadHandler.getUploadProgress(uploadId, new ResultCallback() {
			public void onCallback(JavaScriptObject result) {
				UploadStatus status = result.cast();
				listener.onUpdateProgress(status);
				if (!stop)
					timer.schedule(INTERVAL);
			}
		});

	}
}