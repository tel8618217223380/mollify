/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin.service;

import org.sjarvela.mollify.client.service.ExternalService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeService {
	private final ExternalService externalService;

	public NativeService(ExternalService externalService) {
		this.externalService = externalService;
	}

	public JavaScriptObject asJs() {
		return createJs(this);
	}

	private native JavaScriptObject createJs(NativeService service) /*-{
		var env = {};

		env.getUrl = function(s) {
			return service.@org.sjarvela.mollify.client.plugin.service.NativeService::getUrl(Ljava/lang/String;)(s);
		}

		env.get = function(path, success, fail) {
			service.@org.sjarvela.mollify.client.plugin.service.NativeService::get(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(path, success, fail);
		}

		return env;
	}-*/;

	protected String getUrl(String s) {
		return externalService.getUrl(s);
	}

	protected void get(String path, final JavaScriptObject success,
			final JavaScriptObject fail) {
		externalService.get(path, new ResultListener<String>() {
			@Override
			public void onFail(ServiceError error) {
				invokeFail(fail, error.getError().getCode(), error.getError()
						.getError());
			}

			@Override
			public void onSuccess(String result) {
				invokeSuccess(success, result);
			}
		});
	}

	protected native void invokeSuccess(JavaScriptObject cb, String result) /*-{
		if (!cb) return;
		cb(result);
	}-*/;

	protected native void invokeFail(JavaScriptObject cb, int code, String error) /*-{
		if (!cb) return;
		cb(code, error);
	}-*/;
}
