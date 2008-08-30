package org.sjarvela.mollify.client.service;


import org.sjarvela.mollify.client.service.json.JsonRpcListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class ObjectListListener implements JsonRpcListener {
	ResultListener resultListener;

	public ObjectListListener(ResultListener resultListener) {
		super();
		this.resultListener = resultListener;
	}

	public void onFailure(ServiceError error) {
		GWT.log("Service request failed: " + error.name(), null);
		resultListener.onError(error);
	}

	@SuppressWarnings("unchecked")
	public void onSuccess(JavaScriptObject object) {
		JsArray result = object.cast();
		if (result == null) {
			onFailure(ServiceError.DATA_TYPE_MISMATCH);
			return;
		}

		resultListener.onSuccess(result);
	}
}
