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

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.request.ResultListener;
import org.sjarvela.mollify.client.request.ReturnValue;
import org.sjarvela.mollify.client.request.file.FileUploadService;
import org.sjarvela.mollify.client.service.MollifyServiceException;
import org.sjarvela.mollify.client.service.ServiceErrorType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.json.client.JSONParser;

public class PhpFileUploadService implements FileUploadService {
	private final PhpService service;

	public PhpFileUploadService(PhpService service) {
		this.service = service;
	}

	public String getFileUploadId() {
		return service.getNewUploadId();
	}

	public String getUploadUrl(Directory directory) {
		return service.getActionUrl(directory, FileSystemAction.upload);
	}

	public void getUploadProgress(String id, ResultListener listener) {
		service.getUploadProgress(id, listener);
	}

	public ReturnValue handleResult(String resultString)
			throws MollifyServiceException {
		if (Log.isDebugEnabled())
			Log.debug("File upload result: " + resultString);

		if (resultString == null || resultString.length() < 1)
			throw new MollifyServiceException(ServiceErrorType.NO_RESPONSE);

		return JSONParser.parse(resultString).isObject().getJavaScriptObject()
				.cast();
	}

}
