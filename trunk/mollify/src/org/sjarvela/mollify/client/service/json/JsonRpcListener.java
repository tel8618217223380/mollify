package org.sjarvela.mollify.client.service.json;

import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.JavaScriptObject;

public interface JsonRpcListener {
	void onSuccess(JavaScriptObject jso);

	void onFailure(ServiceError error);
}
