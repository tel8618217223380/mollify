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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.LogConfiguration;

public class HttpRequestHandler extends
		com.google.gwt.http.client.RequestBuilder {
	private static Logger logger = Logger.getLogger(HttpRequestHandler.class
			.getName());

	public static final int HTTP_STATUS_OK = 200;
	private static final int HTTP_STATUS_NOT_FOUND = 404;

	private final HttpRequestResponseListener listener;

	public HttpRequestHandler(
			boolean limitedHttpMethods,
			String sessionId,
			org.sjarvela.mollify.client.service.request.RequestBuilder.Method method,
			final String url, int timeout, String data,
			final HttpRequestResponseListener listener) {
		super(getMethod(limitedHttpMethods, method), url);

		this.listener = listener;
		setTimeoutMillis(timeout * 1000);
		if (data != null)
			setRequestData(data);

		if (limitedHttpMethods)
			this.setHeader("mollify-http-method", method.name());
		
		if (sessionId != null)
			this.setHeader("mollify-session-id", sessionId);
		
		setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				logger.log(Level.SEVERE, "Request error", exception);

				if (RequestTimeoutException.class.equals(exception.getClass()))
					listener.onNoResponse();
				else
					listener.onRequestFailed(exception.getMessage());
			}

			public void onResponseReceived(Request request, Response response) {
				int statusCode = response.getStatusCode();
				if (LogConfiguration.loggingIsEnabled())
					logger.log(Level.INFO, "Request response: " + statusCode
							+ " " + response.getText());

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

	private static com.google.gwt.http.client.RequestBuilder.Method getMethod(
			boolean limitedHttpMethods,
			org.sjarvela.mollify.client.service.request.RequestBuilder.Method method) {
		if (!limitedHttpMethods)
			return convert(method);
		if (org.sjarvela.mollify.client.service.request.RequestBuilder.Method.GET
				.equals(method))
			return RequestBuilder.GET;
		return RequestBuilder.POST;
	}

	private static com.google.gwt.http.client.RequestBuilder.Method convert(
			org.sjarvela.mollify.client.service.request.RequestBuilder.Method method) {
		if (org.sjarvela.mollify.client.service.request.RequestBuilder.Method.GET
				.equals(method))
			return RequestBuilder.GET;
		if (org.sjarvela.mollify.client.service.request.RequestBuilder.Method.POST
				.equals(method))
			return RequestBuilder.POST;
		if (org.sjarvela.mollify.client.service.request.RequestBuilder.Method.DELETE
				.equals(method))
			return RequestBuilder.DELETE;
		if (org.sjarvela.mollify.client.service.request.RequestBuilder.Method.PUT
				.equals(method))
			return RequestBuilder.PUT;
		throw new RuntimeException("Invalid http method: " + method.name());
	}

	public void process() {
		try {
			send();
		} catch (RequestException e) {
			logger.log(Level.SEVERE, "Request failed", e);
			listener.onRequestFailed(e.getMessage());
		}
	}
}
