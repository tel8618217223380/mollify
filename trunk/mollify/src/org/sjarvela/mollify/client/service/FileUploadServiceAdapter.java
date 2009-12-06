/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.filesystem.FileUploadStatus;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListenerFactory;

public class FileUploadServiceAdapter implements FileUploadService {
	private final FileUploadService service;
	private final ResultListenerFactory resultListenerFactory;

	public FileUploadServiceAdapter(FileUploadService service,
			ResultListenerFactory resultListenerFactory) {
		this.service = service;
		this.resultListenerFactory = resultListenerFactory;
	}

	public String getFileUploadId() {
		return service.getFileUploadId();
	}

	public void getUploadProgress(String id,
			ResultListener<FileUploadStatus> listener) {
		service.getUploadProgress(id, resultListenerFactory
				.createListener(listener));
	}

	public String getUploadUrl(Folder folder) {
		return service.getUploadUrl(folder);
	}

	public void handleResult(String resultString, FileUploadListener listener) {
		service.handleResult(resultString, listener);
	}
}
