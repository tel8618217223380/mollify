/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.php;

import org.sjarvela.mollify.client.service.request.RequestBuilder;
import org.sjarvela.mollify.client.service.request.ResponseProcessor;
import org.sjarvela.mollify.client.service.request.UrlBuilder;
import org.sjarvela.mollify.client.service.request.listener.HtmlRequestListener;
import org.sjarvela.mollify.client.service.request.listener.JsonRequestListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public class PhpRequestBuilder extends RequestBuilder {

	private final ResponseProcessor responseProcessor;

	public PhpRequestBuilder(boolean limitedHttpMethods,
			ResponseProcessor responseProcessor) {
		super(limitedHttpMethods);
		this.responseProcessor = responseProcessor;
	}

	public PhpRequestBuilder url(String url) {
		return (PhpRequestBuilder) super.url(url);
	}

	public PhpRequestBuilder url(UrlBuilder urlBuilder) {
		return (PhpRequestBuilder) super.url(urlBuilder);
	}

	public PhpRequestBuilder timeout(int timeout) {
		return (PhpRequestBuilder) super.timeout(timeout);
	}

	public PhpRequestBuilder data(String data) {
		return (PhpRequestBuilder) super.data(data);
	}

	public PhpRequestBuilder listener(ResultListener resultListener) {
		return listener(resultListener, true);
	}

	public PhpRequestBuilder listener(ResultListener resultListener,
			boolean json) {
		if (json)
			return (PhpRequestBuilder) super.listener(new JsonRequestListener(
					responseProcessor, resultListener));
		else
			return (PhpRequestBuilder) super.listener(new HtmlRequestListener(
					responseProcessor, resultListener));
	}

	public PhpRequestBuilder sessionId(String sessionId) {
		return (PhpRequestBuilder) super.sessionId(sessionId);
	}

}
