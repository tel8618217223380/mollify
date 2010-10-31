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

import com.google.gwt.logging.client.LogConfiguration;

public class RequestBuilder {
	private static Logger logger = Logger.getLogger(RequestBuilder.class
			.getName());

	public enum Method {
		GET, PUT, POST, DELETE
	}

	private final boolean limitedHttpMethods;
	private String url = null;
	private int timeout = 0;
	private String data = null;
	private HttpRequestResponseListener listener = null;

	public RequestBuilder(boolean limitedHttpMethods) {
		this.limitedHttpMethods = limitedHttpMethods;
	}

	public RequestBuilder url(String url) {
		this.url = url;
		return this;
	}

	public RequestBuilder url(UrlBuilder urlBuilder) {
		this.url = urlBuilder.build();
		return this;
	}

	public RequestBuilder timeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public RequestBuilder listener(HttpRequestResponseListener listener) {
		this.listener = listener;
		return this;
	}

	public RequestBuilder data(String data) {
		this.data = data;
		return this;
	}

	public void send(
			org.sjarvela.mollify.client.service.request.RequestBuilder.Method method) {
		if (LogConfiguration.loggingIsEnabled())
			logger.log(Level.INFO, "REQUEST (" + method.name() + "): " + url
					+ (data != null ? (" [" + data + "]") : ""));
		new HttpRequestHandler(limitedHttpMethods, method, url, timeout, data,
				listener).process();
	}

	public void get() {
		this.send(Method.GET);
	}

	public void post() {
		this.send(Method.POST);
	}

	public void put() {
		this.send(Method.PUT);
	}

	public void delete() {
		this.send(Method.DELETE);
	}

}
