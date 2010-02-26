/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.php;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadStatus;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.request.listener.JsonRequestListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.util.DateTime;

import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class PhpFileUploadService extends PhpFileService implements
		FileUploadService {
	enum FileUploadAction implements ActionId {
		upload_status
	};

	public PhpFileUploadService(PhpService service) {
		super(service);
	}

	public String getFileUploadId() {
		// Just any unique id, time in millisecond level is unique enough
		return DateTime.getInstance().getInternalExactFormat().format(
				DateTime.getInstance().currentTime());
	}

	public String getUploadUrl(Folder directory) {
		return serviceUrl().fileItem(directory).item("files").build();
	}

	public void getUploadProgress(String id,
			ResultListener<FileUploadStatus> resultListener) {
		request().url(serviceUrl().item("upload").item(id).item("status"))
				.listener(resultListener).get();
	}

	@Override
	public SubmitCompleteHandler getUploadHandler(final ResultListener listener) {
		final JsonRequestListener jsonHandler = new JsonRequestListener(
				listener);
		SubmitCompleteHandler handler = new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				String response = event.getResults();

				if (isError(response))
					jsonHandler.onFail(0, response);
				else
					jsonHandler.onSuccess(response);
			}

			private boolean isError(String response) {
				return response.indexOf("\"error\":") >= 0;
			}
		};
		return handler;
	}
}
