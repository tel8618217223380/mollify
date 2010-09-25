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

import java.util.Map;

import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public interface ExternalService {
	String getUrl(String s);

	void get(String path, ResultListener listener);

	void post(String path, Map<String, String> data, ResultListener resultListener);

	void post(Map<String, String> data, ResultListener resultListener);
}
