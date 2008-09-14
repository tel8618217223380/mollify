/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.listener;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class ObjectListListener extends ObjectListener {
	ResultListener resultListener;

	public ObjectListListener(ResultListener resultListener) {
		super(resultListener);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean validate(JavaScriptObject result) {
		JsArray array = result.cast();
		return (array != null);
	}
}
