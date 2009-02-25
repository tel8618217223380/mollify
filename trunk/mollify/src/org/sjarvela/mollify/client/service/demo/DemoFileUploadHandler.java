/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.demo;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.FileUploadStatus;
import org.sjarvela.mollify.client.request.ResultListener;
import org.sjarvela.mollify.client.request.file.FileUploadController;
import org.sjarvela.mollify.client.request.file.FileUploadHandler;
import org.sjarvela.mollify.client.request.file.FileUploadListener;

import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;

public class DemoFileUploadHandler implements FileUploadHandler {

	public void addListener(FileUploadListener listener) {
	}

	public String getFileUploadId() {
		return "";
	}

	public FormHandler getUploadFormHandler(FileUploadController controller,
			String uploadId) {
		return new FormHandler() {

			public void onSubmit(FormSubmitEvent event) {
			}

			public void onSubmitComplete(FormSubmitCompleteEvent event) {
			}
		};
	}

	public void getUploadProgress(String id, ResultListener listener) {
		listener.onSuccess(FileUploadStatus.create(100, 50, 100, "Example.txt",
				0));
	}

	public String getUploadUrl(Directory directory) {
		return "";
	}

}
