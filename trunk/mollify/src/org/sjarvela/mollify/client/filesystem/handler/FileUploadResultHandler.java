/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.handler;

import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.data.ReturnValue;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

import com.google.gwt.json.client.JSONParser;

public class FileUploadResultHandler {
	ResultListener listener;

	public FileUploadResultHandler(ResultListener listener) {
		super();
		this.listener = listener;
	}

	public void handleResult(String resultString) {
		if (resultString == null || resultString.length() < 1)
			listener.onFail(new ServiceError(ServiceErrorType.NO_RESPONSE));

		ReturnValue result = JSONParser.parse(resultString).isObject()
				.getJavaScriptObject().cast();
		listener.onSuccess(result);
	}
}
