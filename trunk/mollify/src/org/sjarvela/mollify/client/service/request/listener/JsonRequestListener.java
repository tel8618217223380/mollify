/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.HttpRequestResponseListener;
import org.sjarvela.mollify.client.service.request.ResponseProcessor;
import org.sjarvela.mollify.client.service.request.data.ErrorValue;
import org.sjarvela.mollify.client.service.request.data.ReturnValue;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class JsonRequestListener implements HttpRequestResponseListener {
	private static Logger logger = Logger.getLogger(JsonRequestListener.class
			.getName());

	private final ResultListener listener;
	private final ResponseProcessor responseProcessor;

	public JsonRequestListener(ResponseProcessor responseProcessor,
			final ResultListener listener) {
		this.responseProcessor = responseProcessor;
		this.listener = listener;
	}

	public void onSuccess(Response response) {
		onSuccess(response.getText());
	}

	public void onSuccess(String r) {
		String response = responseProcessor.processResponse(r);
		try {
			JSONObject o = JSONParser.parseLenient(response).isObject();
			if (o == null) {
				onError(new ServiceError(ServiceErrorType.INVALID_RESPONSE));
				return;
			}
			ReturnValue returnValue = (ReturnValue) o.getJavaScriptObject()
					.cast();
			if (returnValue.isResultBoolean())
				listener.onSuccess(new Boolean(returnValue.getResultAsBoolean()));
			else
				listener.onSuccess(returnValue.getResult());
		} catch (com.google.gwt.json.client.JSONException e) {
			onError(new ServiceError(ServiceErrorType.DATA_TYPE_MISMATCH,
					"Got malformed JSON response: " + response));
		}
	}

	public void onFail(Response response) {
		onFail(response.getStatusCode(), response.getText());
	}

	public void onFail(int code, String jsonString) {
		if (jsonString.isEmpty()) {
			onError(new ServiceError(ServiceErrorType.INVALID_RESPONSE,
					"Empty response received (status " + code + ")"));
			return;
		}
		JSONObject o = JSONParser.parseLenient(jsonString).isObject();
		if (o == null) {
			onError(new ServiceError(ServiceErrorType.INVALID_RESPONSE));
			return;
		}
		ErrorValue error = o.getJavaScriptObject().cast();
		onError(new ServiceError(ServiceErrorType.getFrom(error), error));
	}

	public void onNoResponse() {
		onError(new ServiceError(ServiceErrorType.NO_RESPONSE));
	}

	public void onRequestFailed(String reason) {
		onError(new ServiceError(ServiceErrorType.REQUEST_FAILED, reason));
	}

	public void onResourceNotFound(String url) {
		onError(new ServiceError(ServiceErrorType.RESOURCE_NOT_FOUND,
				"Resource not found: " + url));
	}

	private void onError(ServiceError error) {
		logger.log(Level.SEVERE, "Request failed: error=" + error.toString());
		listener.onFail(error);
	}

}
