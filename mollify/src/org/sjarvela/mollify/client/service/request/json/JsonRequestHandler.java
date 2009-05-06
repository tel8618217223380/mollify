/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request.json;

import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.ErrorValue;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.service.request.ReturnValue;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

public class JsonRequestHandler {
	private static final int HTTP_STATUS_NOT_FOUND = 404;

	private final ResultListener listener;
	private final String url;
	private final RequestBuilder requestBuilder;

	public JsonRequestHandler(String url, final ResultListener listener,
			int timeout) {
		this.listener = listener;
		this.url = url;

		requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setTimeoutMillis(timeout * 1000);
		requestBuilder.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				Log.error("Request error", exception);
				GWT.log("Request error", exception);

				if (RequestTimeoutException.class.equals(exception.getClass()))
					JsonRequestHandler.this.onError(new ServiceError(
							ServiceErrorType.NO_RESPONSE));
				else
					JsonRequestHandler.this.onError(new ServiceError(
							ServiceErrorType.REQUEST_FAILED));
			}

			public void onResponseReceived(Request request, Response response) {
				if (Log.isDebugEnabled())
					Log.debug("Request response: " + response.getStatusCode()
							+ "/" + response.getText());
				if (response.getStatusCode() == HTTP_STATUS_NOT_FOUND) {
					Log.error("Service file not found: "
							+ JsonRequestHandler.this.url);
					JsonRequestHandler.this.onError(new ServiceError(
							ServiceErrorType.INVALID_CONFIGURATION));
					return;
				}

				try {
					JSONObject o = JSONParser.parse(response.getText())
							.isObject();
					if (o == null) {
						JsonRequestHandler.this.onError(new ServiceError(
								ServiceErrorType.INVALID_RESPONSE));
						return;
					}
					onResponse((ReturnValue) o.getJavaScriptObject().cast());
				} catch (com.google.gwt.json.client.JSONException e) {
					GWT.log("Invalid JSON response: "
							+ response.getStatusCode() + "/"
							+ response.getText(), e);
					Log.error("Invalid JSON response: "
							+ response.getStatusCode() + "/"
							+ response.getText(), e);
					JsonRequestHandler.this.onError(new ServiceError(
							ServiceErrorType.DATA_TYPE_MISMATCH));
				}
			}
		});
	}

	public void doRequest() {
		try {
			requestBuilder.send();
		} catch (RequestException e) {
			Log.error("Request failed", e);
			JsonRequestHandler.this.onError(new ServiceError(
					ServiceErrorType.REQUEST_FAILED));

		}
	}

	private void onResponse(ReturnValue result) {
		if (!result.isSuccess()) {
			ErrorValue error = result.cast();
			JsonRequestHandler.this.onError(new ServiceError(ServiceErrorType
					.getFrom(error), error.getDetails()));
			return;
		}
		listener.onSuccess(result.getResult());
	}

	private void onError(ServiceError error) {
		Log.error("Request failed: url=[" + url + "] msg=" + error.toString());
		listener.onFail(error);
	}
}
