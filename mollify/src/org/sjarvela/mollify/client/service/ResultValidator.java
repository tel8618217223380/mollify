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

	public void onSuccess(JavaScriptObject resultValue) {
		ReturnValue result = resultValue.cast();
		if (!result.isSuccess()) {
			ErrorValue error = resultValue.cast();
			onFail(ServiceError.getFrom(error));
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
