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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.UrlParam;

public class ServiceBase {
	protected final PhpService service;
	private final RequestType requestType;

	public ServiceBase(PhpService service, RequestType requestType) {
		this.service = service;
		this.requestType = requestType;
	}

	protected String getUrl(ActionId action) {
		return getUrl(action, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}

	protected String getUrl(ActionId action, String... pathItems) {
		return getUrl(action, Arrays.asList(pathItems), Collections.EMPTY_LIST);
	}

	protected String getUrl(ActionId action, UrlParam... params) {
		return getUrl(action, Collections.EMPTY_LIST, Arrays.asList(params));
	}

	protected String getUrl(ActionId action, List<UrlParam> params) {
		return getUrl(action, Collections.EMPTY_LIST, params);
	}

	protected String getUrl(ActionId action, List<String> pathItems,
			List<UrlParam> params) {
		List<String> path = new ArrayList();
		path.add(requestType.name());
		path.add(action.name());
		path.addAll(pathItems);
		return service.getUrl(path, params);
	}
}
