/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.php;

import java.util.Map;
import java.util.Map.Entry;

import org.sjarvela.mollify.client.service.ExternalService;
import org.sjarvela.mollify.client.service.request.JSONStringBuilder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public class PhpExternalService extends ServiceBase implements ExternalService {
	public PhpExternalService(PhpService service) {
		super(service, null);
	}

	@Override
	public void get(String path, ResultListener listener) {
		service.request().url(getUrl(path)).listener(listener).get();
	}

	@Override
	public void post(Map<String, String> data, ResultListener listener) {
		post(null, data, listener);
	}

	@Override
	public void post(String path, Map<String, String> data,
			ResultListener listener) {
		JSONStringBuilder dataBuilder = new JSONStringBuilder();
		for (Entry<String, String> e : data.entrySet())
			dataBuilder.add(e.getKey(), e.getValue());
		service.request().url(getUrl(path)).listener(listener).data(
				dataBuilder.toString()).post();
	}

	@Override
	public String getUrl(String path) {
		return serviceUrl().item(path).build();
	}

}
