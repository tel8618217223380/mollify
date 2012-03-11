package org.sjarvela.mollify.client.service.request.listener;

import org.sjarvela.mollify.client.service.ServiceError;

public class VoidResultListener implements ResultListener<Object> {
	public void onFail(ServiceError error) {
	}

	public void onSuccess(Object result) {
	}
}
