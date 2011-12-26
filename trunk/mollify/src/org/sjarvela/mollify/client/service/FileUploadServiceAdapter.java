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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadStatus;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListenerFactory;

import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

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
		service.getUploadProgress(id,
				resultListenerFactory.createListener(listener));
	}

	public String getUploadUrl(Folder folder) {
		return service.getUploadUrl(folder);
	}

	@Override
	public void checkFiles(Folder folder, List<String> filenames,
			ResultListener<List<String>> l) {
		service.checkFiles(folder, filenames, l);
	}

	@Override
	public SubmitCompleteHandler getUploadHandler(ResultListener listener) {
		return service.getUploadHandler(resultListenerFactory
				.createListener(listener));
	}
}
