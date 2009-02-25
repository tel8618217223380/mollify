/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.php;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.request.ResultListener;
import org.sjarvela.mollify.client.request.file.FileUploadController;
import org.sjarvela.mollify.client.request.file.FileUploadHandler;
import org.sjarvela.mollify.client.request.file.FileUploadListener;
import org.sjarvela.mollify.client.request.file.FileUploadResultHandler;
import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;

public class PhpFileUploadHandler implements FileUploadHandler {
	private final PhpService service;
	private final List<FileUploadListener> listeners = new ArrayList<FileUploadListener>();

	public PhpFileUploadHandler(PhpService service) {
		this.service = service;
	}

	public void addListener(FileUploadListener listener) {
		listeners.add(listener);
	}

	public String getFileUploadId() {
		return service.getNewUploadId();
	}

	public void getUploadProgress(String id, ResultListener listener) {
		service.getUploadProgress(id, listener);
	}

	public FormHandler getUploadFormHandler(
			final FileUploadController controller, final String uploadId) {
		return new FormHandler() {

			public void onSubmit(FormSubmitEvent event) {
				if (!controller.onStartUpload()) {
					event.setCancelled(true);
					return;
				}

				List<String> filenames = controller.getFileNames();
				for (FileUploadListener listener : listeners)
					listener.onUploadStarted(uploadId, filenames);
			}

			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				controller.onUploadFinished();
				getFileUploadResultHandler().handleResult(event.getResults());
			}
		};
	}

	private FileUploadResultHandler getFileUploadResultHandler() {
		return new FileUploadResultHandler(new ResultListener() {
			public void onFail(ServiceError error) {
				for (FileUploadListener listener : listeners)
					listener.onUploadFailed(error);
			}

			public void onSuccess(Object... result) {
				for (FileUploadListener listener : listeners)
					listener.onUploadFinished();
			}
		});
	}

	public String getUploadUrl(Directory directory) {
		return service.getActionUrl(directory, FileSystemAction.upload);
	}
}
