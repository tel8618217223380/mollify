/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class ReturnValue extends JavaScriptObject {
	public static ReturnValue success(JavaScriptObject result) {
		ReturnValue val = ReturnValue.createObject().cast();
		val.setValues(true, result);
		return val;
	}

	public static ReturnValue fail() {
		ReturnValue val = ReturnValue.createObject().cast();
		val.setValues(false, null);
		return val;
	}

	protected ReturnValue() {
	}

	public final native boolean isSuccess() /*-{
		return this.success;
	}-*/;

	public final native JavaScriptObject getResult() /*-{
		return this.result;
	}-*/;

	public final native JsArrayString getDebugInfo() /*-{
		return this.trace;
	}-*/;

	private final native JavaScriptObject setValues(boolean success,
			JavaScriptObject result) /*-{
		this.success = success;
		this.result = result;
	}-*/;
}
