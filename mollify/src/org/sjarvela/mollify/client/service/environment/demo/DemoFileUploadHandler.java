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

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.FileUploadStatus;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.request.data.ReturnValue;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

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

	public String getUploadUrl(Directory directory) {
		return "";
	}

	public void handleResult(String results, FileUploadListener listener) {
		listener.onUploadFinished(ReturnValue.success(null));
	}

}
