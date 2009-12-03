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

import org.sjarvela.mollify.client.service.request.RequestBuilder;
import org.sjarvela.mollify.client.service.request.listener.JsonRequestListener;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public class PhpRequestBuilder extends RequestBuilder {
	@Override
	public void send(Method method, ResultListener listener) {
		super.send(method, new JsonRequestListener(listener));
	}
}
