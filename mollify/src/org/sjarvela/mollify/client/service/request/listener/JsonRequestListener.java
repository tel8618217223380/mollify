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

import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.HttpRequestResponseListener;
import org.sjarvela.mollify.client.service.request.data.ErrorValue;
import org.sjarvela.mollify.client.service.request.data.ReturnValue;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class JsonRequestListener implements HttpRequestResponseListener {
	private final ResultListener listener;

	public JsonRequestListener(final ResultListener listener) {
		this.listener = listener;
	}

	public void onSuccess(Response response) {
		try {
			JSONObject o = JSONParser.parse(response.getText()).isObject();
			if (o == null) {
				onError(new ServiceError(ServiceErrorType.INVALID_RESPONSE));
				return;
			}
			ReturnValue returnValue = (ReturnValue) o.getJavaScriptObject()
					.cast();
			if (returnValue.isResultBoolean())
				listener
						.onSuccess(new Boolean(returnValue.getResultAsBoolean()));
			else
				listener.onSuccess(returnValue.getResult());
		} catch (com.google.gwt.json.client.JSONException e) {
			onError(new ServiceError(ServiceErrorType.DATA_TYPE_MISMATCH,
					"Got malformed JSON response: " + response.getText()));
		}
	}

	public void onFail(Response response) {
		JSONObject o = JSONParser.parse(response.getText()).isObject();
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
		onError(new ServiceError(ServiceErrorType.REQUEST_FAILED,
				"Resource not found: " + url));
	}

	private void onError(ServiceError error) {
		Log.error("Request failed: error=" + error.toString());
		listener.onFail(error);
	}

}
