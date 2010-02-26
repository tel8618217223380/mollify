/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.demo;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadStatus;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class DemoFileUploadHandler implements FileUploadService {

	public void addListener(FileUploadListener listener) {
	}

	public String getFileUploadId() {
		return "";
	}

	public void getUploadProgress(String id, ResultListener listener) {
		listener.onSuccess(FileUploadStatus.create(100, 50, 100, "Example.txt",
				0));
	}

	public String getUploadUrl(Folder directory) {
		return "";
	}

	@Override
	public SubmitCompleteHandler getUploadHandler(final ResultListener listener) {
		return new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				listener.onSuccess(null);
			}
		};
	}

}
