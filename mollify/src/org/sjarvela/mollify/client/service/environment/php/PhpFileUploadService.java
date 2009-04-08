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

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileUploadStatus;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.service.request.ReturnValue;
import org.sjarvela.mollify.client.util.DateTime;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class PhpFileUploadService extends PhpFileService implements
		FileUploadService {
	public PhpFileUploadService(PhpService service) {
		super(service);
	}

	public String getFileUploadId() {
		// Just any unique id, time in millisecond level is unique enough
		return DateTime.getInstance().getInternalExactFormat().format(
				DateTime.getInstance().currentTime());
	}

	public String getUploadUrl(Directory directory) {
		return getFileActionUrl(directory, FileSystemAction.upload);
	}

	public void getUploadProgress(String id,
			ResultListener<FileUploadStatus> resultListener) {
		service.doRequest(getFileDataUrl(FileDataAction.upload_status, "id=" + id),
				resultListener);
	}

	public void handleResult(String resultString, FileUploadListener listener) {
		if (Log.isDebugEnabled())
			Log.debug("File upload result: " + resultString);

		if (resultString == null || resultString.length() < 1) {
			listener.onUploadFailed(new ServiceError(
					ServiceErrorType.NO_RESPONSE));
		}

		JSONObject object = JSONParser.parse(resultString).isObject();
		if (object == null) {
			listener.onUploadFailed(new ServiceError(
					ServiceErrorType.INVALID_RESPONSE));
		}
		ReturnValue returnValue = object.getJavaScriptObject()
				.<ReturnValue> cast();

		if (!returnValue.isSuccess())
			listener.onUploadFailed(new ServiceError(
					ServiceErrorType.UPLOAD_FAILED));
		else
			listener.onUploadFinished(returnValue.getResult());
	}
}
