package org.sjarvela.mollify.client.request;

import org.sjarvela.mollify.client.service.ServiceError;

public class VoidResultListener implements ResultListener {
	public void onFail(ServiceError error) {
	}

	public void onSuccess(Object... result) {
	}
}
