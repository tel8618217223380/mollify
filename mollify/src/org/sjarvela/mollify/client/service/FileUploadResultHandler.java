package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.data.SuccessResult;

import com.google.gwt.json.client.JSONParser;

public class FileUploadResultHandler {
	ResultListener listener;

	public FileUploadResultHandler(ResultListener listener) {
		super();
		this.listener = listener;
	}

	public void handleResult(String resultString) {
		if (resultString == null || resultString.length() < 1)
			listener.onError(ServiceError.NO_RESPONSE);

		SuccessResult result = JSONParser.parse(resultString).isObject()
				.getJavaScriptObject().cast();
		listener.onSuccess(result);
	}
}
