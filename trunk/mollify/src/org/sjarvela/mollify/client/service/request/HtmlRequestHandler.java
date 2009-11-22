/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request;

import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder.Method;

public class HtmlRequestHandler implements RequestHandler {
	private static final int HTTP_STATUS_OK = 200;
	private static final int HTTP_STATUS_UNAUTHORIZED = 403;
	private static final int HTTP_STATUS_NOT_FOUND = 404;
	private static final int HTTP_STATUS_SERVER_ERROR = 500;

	private ResultListener listener;
	private String url;
	private RequestBuilder requestBuilder;

	public HtmlRequestHandler(Method method, String url,
			final ResultListener<Response> listener, int timeout) {
		this.listener = listener;
		this.url = url;

		requestBuilder = new RequestBuilder(method, url);
		requestBuilder.setTimeoutMillis(timeout * 1000);
		requestBuilder.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				Log.error("Request error", exception);

				if (RequestTimeoutException.class.equals(exception.getClass()))
					HtmlRequestHandler.this.onError(new ServiceError(
							ServiceErrorType.NO_RESPONSE));
				else
					HtmlRequestHandler.this.onError(new ServiceError(
							ServiceErrorType.REQUEST_FAILED));
			}

			public void onResponseReceived(Request request, Response response) {
				int statusCode = response.getStatusCode();
				if (Log.isDebugEnabled())
					Log.debug("Request response: " + statusCode);

				if (statusCode == HTTP_STATUS_OK) {
					listener.onSuccess(response);
					return;
				}

				if (statusCode == HTTP_STATUS_UNAUTHORIZED) {
					HtmlRequestHandler.this.onError(new ServiceError(
							ServiceErrorType.UNAUTHORIZED, response.getText()));
					return;

				}

				if (statusCode == HTTP_STATUS_NOT_FOUND) {
					HtmlRequestHandler.this.onError(new ServiceError(
							ServiceErrorType.INVALID_CONFIGURATION,
							"Service file not found: "
									+ HtmlRequestHandler.this.url));
					return;
				}
				if (statusCode == HTTP_STATUS_SERVER_ERROR) {
					HtmlRequestHandler.this
							.onError(new ServiceError(
									ServiceErrorType.UNKNOWN_ERROR, response
											.getText()));
					return;
				}
				HtmlRequestHandler.this.onError(new ServiceError(
						ServiceErrorType.REQUEST_FAILED));
			}
		});
	}

	protected void onError(ServiceError error) {
		listener.onFail(error);
	}

	public HtmlRequestHandler withData(String data) {
		if (data == null)
			return this;
		requestBuilder.setRequestData(data);
		requestBuilder.setHeader("Content-Type",
				"application/x-www-form-urlencoded-data; charset=utf-8");
		return this;
	}

	public void doRequest() {
		try {
			requestBuilder.send();
		} catch (RequestException e) {
			Log.error("Request failed", e);
			onError(new ServiceError(ServiceErrorType.REQUEST_FAILED));
		}
	}
}
