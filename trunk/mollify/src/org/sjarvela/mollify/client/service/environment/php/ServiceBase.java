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

import org.sjarvela.mollify.client.service.environment.php.PhpService.RequestType;
import org.sjarvela.mollify.client.service.request.RequestBuilder;
import org.sjarvela.mollify.client.service.request.UrlBuilder;

public class ServiceBase {
	protected final PhpService service;
	private final RequestType requestType;

	public ServiceBase(PhpService service, RequestType requestType) {
		this.service = service;
		this.requestType = requestType;
	}

	protected UrlBuilder serviceUrl() {
		return service.url().item(requestType.name());
	}

	protected RequestBuilder request() {
		return service.request();
	}
}
