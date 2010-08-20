/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

@Singleton
public class DefaultHttpResponseProcessorProxy implements
		HttpResponseProcessorProxy {
	private List<HttpResponseProcessor> processors = new ArrayList();

	@Override
	public String processHttpResult(String response) {
		String r = response;
		for (HttpResponseProcessor p : processors)
			r = p.processHttpResult(r);
		return r;
	}

	@Override
	public void addProcessor(HttpResponseProcessor processor) {
		processors.add(processor);
	}

}
