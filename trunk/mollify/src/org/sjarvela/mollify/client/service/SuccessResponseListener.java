/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.data.SuccessResult;
import org.sjarvela.mollify.client.service.json.JsonRpcListener;

import com.google.gwt.core.client.JavaScriptObject;

public class SuccessResponseListener implements JsonRpcListener {
	private ResultListener resultListener;

	public SuccessResponseListener(ResultListener resultListener) {
		super();
		this.resultListener = resultListener;
	}

	public void onFailure(ServiceError error) {
		resultListener.onError(error);
	}

	public void onSuccess(JavaScriptObject jso) {
		SuccessResult result = jso.cast();
		if (result == null) {
			onFailure(ServiceError.DATA_TYPE_MISMATCH);
			return;
		}

		resultListener.onSuccess(result);
	}

}
