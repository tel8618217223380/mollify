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

import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public class RequestBuilder {
	public enum Method {
		GET, PUT, POST, DELETE
	}

	private String url = null;
	private int timeout = 0;
	private String data = null;

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

	public RequestBuilder data(String data) {
		this.data = data;
		return this;
	}

	public void send(Method method, ResultListener listener) {
		new HttpRequestHandler(method.name(), url, timeout, data, listener)
				.process();
	}

	public void get(ResultListener resultListener) {
		this.send(Method.GET, resultListener);
	}

	public void post(ResultListener resultListener) {
		this.send(Method.POST, resultListener);
	}

	public void put(ResultListener resultListener) {
		this.send(Method.PUT, resultListener);
	}

	public void delete(ResultListener resultListener) {
		this.send(Method.DELETE, resultListener);
	}

}
