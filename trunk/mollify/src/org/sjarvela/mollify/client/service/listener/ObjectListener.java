package org.sjarvela.mollify.client.service.listener;

import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;

public abstract class ObjectListener implements ResultListener {
	private ResultListener resultListener;

	public ObjectListener(ResultListener resultListener) {
		super();
		this.resultListener = resultListener;
	}

	public void onError(ServiceError error) {
		GWT.log("Service request failed: " + error.name(), null);
		resultListener.onError(error);
	}

	public void onSuccess(JavaScriptObject result) {
		if (!validate(result)) {
			onError(ServiceError.DATA_TYPE_MISMATCH);
			return;
		}

		resultListener.onSuccess(result);
	}

	protected abstract boolean validate(JavaScriptObject result);
}
