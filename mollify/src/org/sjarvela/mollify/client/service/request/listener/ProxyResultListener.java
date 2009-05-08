/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request.listener;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.service.ServiceError;

public class ProxyResultListener<T> implements ResultListener<T> {
	private List<ResultListener<T>> listeners = new ArrayList();

	public void addListener(ResultListener listener) {
		listeners.add(listener);
	}

	public void onFail(ServiceError error) {
		for (ResultListener listener : listeners)
			listener.onFail(error);
	}

	public void onSuccess(T result) {
		for (ResultListener listener : listeners)
			listener.onSuccess(result);
	}

}
