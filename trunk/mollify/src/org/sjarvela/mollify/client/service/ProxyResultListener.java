package org.sjarvela.mollify.client.service;

import java.util.ArrayList;
import java.util.List;


import com.google.gwt.core.client.JavaScriptObject;

public class ProxyResultListener implements ResultListener {
	private List<ResultListener> listeners = new ArrayList<ResultListener>();

	public void addListener(ResultListener listener) {
		listeners.add(listener);
	}

	public void onFail(ServiceError error) {
		for (ResultListener listener : listeners)
			listener.onFail(error);
	}

	public void onSuccess(JavaScriptObject... result) {
		for (ResultListener listener : listeners)
			listener.onSuccess(result);
	}

}
