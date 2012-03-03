/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin.response;

import org.sjarvela.mollify.client.service.request.ResponseProcessor;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeResponseProcessor implements ResponseProcessor {
	private final JavaScriptObject cb;

	public NativeResponseProcessor(JavaScriptObject cb) {
		this.cb = cb;
	}

	@Override
	public String processResponse(String response) {
		return invokeNativeProcessor(response);
	}

	private final native String invokeNativeProcessor(String response) /*-{
		cb = this.@org.sjarvela.mollify.client.plugin.response.NativeResponseProcessor::cb;
		return cb(response);
	}-*/;

}
