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

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadStatus;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.JSONBuilder;
import org.sjarvela.mollify.client.service.request.listener.JsonRequestListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.util.DateTime;
import org.sjarvela.mollify.client.util.JsUtil;

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
		return DateTime.getInstance().getInternalExactFormat()
				.format(DateTime.getInstance().currentTime());
	}

	public String getUploadUrl(Folder folder) {
		return serviceUrl().fileItem(folder).item("files").build();
	}

	@Override
	public void checkFiles(Folder folder, List<String> filenames,
			final ResultListener<List<String>> listener) {
		String data = new JSONBuilder().array("files", filenames).toString();

		request().url(serviceUrl().fileItem(folder).item("check"))
				.listener(new ResultListener<JsObj>() {
					@Override
					public void onSuccess(JsObj result) {
						if (!result.hasValue("existing")) {
							onFail(new ServiceError(ServiceErrorType.INVALID_RESPONSE));
							return;
						}
						List<String> names = JsUtil.asList(result.getStringArray("existing"));
						listener.onSuccess(names);
					}

					@Override
					public void onFail(ServiceError error) {
						listener.onFail(error);
					}
				}).data(data).post();
	}

	public void getUploadProgress(String id,
			ResultListener<FileUploadStatus> resultListener) {
		request().url(serviceUrl().item("upload").item(id).item("status"))
				.listener(resultListener).get();
	}

	@Override
	public SubmitCompleteHandler getUploadHandler(final ResultListener listener) {
		final JsonRequestListener jsonHandler = new JsonRequestListener(
				service.getResponseProcessor(), listener);
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
