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
import org.sjarvela.mollify.client.service.request.ResponseProcessor;

import com.google.gwt.http.client.Response;

public class HtmlRequestListener implements HttpRequestResponseListener {

	private final ResponseProcessor responseProcessor;
	private final ResultListener resultListener;

	public HtmlRequestListener(ResponseProcessor responseProcessor,
			ResultListener resultListener) {
		this.responseProcessor = responseProcessor;
		this.resultListener = resultListener;
	}

	@Override
	public void onSuccess(Response response) {
		String r = responseProcessor.processResponse(response.getText());
		resultListener.onSuccess(r);
	}

	@Override
	public void onFail(Response response) {
		String r = responseProcessor.processResponse(response.getText());
		resultListener.onFail(new ServiceError(ServiceErrorType.REQUEST_FAILED,
				r));
	}

	@Override
	public void onNoResponse() {
		resultListener.onFail(new ServiceError(ServiceErrorType.NO_RESPONSE));
	}

	@Override
	public void onRequestFailed(String reason) {
		resultListener.onFail(new ServiceError(ServiceErrorType.REQUEST_FAILED,
				reason));
	}

	@Override
	public void onResourceNotFound(String url) {
		resultListener.onFail(new ServiceError(
				ServiceErrorType.RESOURCE_NOT_FOUND, url));
	}

}
