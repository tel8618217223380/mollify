package org.sjarvela.mollify.client.service;


import com.google.gwt.core.client.JsArray;

public interface ResultListener {
	@SuppressWarnings("unchecked")
	public void onSuccess(JsArray result);

	public void onError(ServiceError error);
}
