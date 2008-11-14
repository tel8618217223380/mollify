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

import org.sjarvela.mollify.client.data.ErrorValue;
import org.sjarvela.mollify.client.data.ReturnValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

public class ResultValidator implements ResultListener {
	private ResultListener resultListener;

	public ResultValidator(ResultListener resultListener) {
		super();
		this.resultListener = resultListener;
	}

	public void onFail(ServiceError error) {
		GWT.log("Service request failed: " + error.name(), null);
		resultListener.onFail(error);
	}

	public void onSuccess(Object... resultValue) {
		JavaScriptObject jso = (JavaScriptObject) resultValue[0];
		ReturnValue result = jso.cast();
		if (!result.isSuccess()) {
			ErrorValue error = jso.cast();
			onFail(ServiceError.getFrom(error));
			return;
		}
		if (!validate(result)) {
			onFail(ServiceError.DATA_TYPE_MISMATCH);
			return;
		}

		resultListener.onSuccess(result.getResult());
	}

	protected boolean validate(JavaScriptObject result) {
		return result != null;
	}
}
