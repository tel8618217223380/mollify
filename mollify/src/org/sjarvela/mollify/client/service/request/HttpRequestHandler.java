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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;

public class HttpRequestHandler extends
		com.google.gwt.http.client.RequestBuilder {
	public static final int HTTP_STATUS_OK = 200;
	private static final int HTTP_STATUS_NOT_FOUND = 404;

	private final HttpRequestResponseListener listener;

	public HttpRequestHandler(String method, final String url, int timeout,
			String data, final HttpRequestResponseListener listener) {
		super(method, url);

		this.listener = listener;
		setTimeoutMillis(timeout * 1000);
		if (data != null)
			setRequestData(data);

		setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				Log.error("Request error", exception);

				if (RequestTimeoutException.class.equals(exception.getClass()))
					listener.onNoResponse();
				else
					listener.onRequestFailed(exception.getMessage());
			}

			public void onResponseReceived(Request request, Response response) {
				int statusCode = response.getStatusCode();
				if (Log.isDebugEnabled())
					Log.debug("Request response: " + statusCode + " "
							+ response.getText());

				if (statusCode == HTTP_STATUS_OK) {
					listener.onSuccess(response);
					return;
				}

				if (statusCode == HTTP_STATUS_NOT_FOUND) {
					listener.onResourceNotFound(url);
					return;
				}

				listener.onFail(response);
			}
		});
	}

	public void process() {
		try {
			send();
		} catch (RequestException e) {
			Log.error("Request failed", e);
			listener.onRequestFailed(e.getMessage());
		}
	}

}
