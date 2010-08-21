/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.event;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultEventDispatcher implements EventDispatcher {
	@SuppressWarnings("unused")
	private JavaScriptObject callbacks = JavaScriptObject.createArray();

	@Inject
	public DefaultEventDispatcher() {
	}

	@Override
	public void onEvent(Event event) {
		handleEvent(event);
	}

	public native void addEventHandler(JavaScriptObject cb) /*-{
		this.@org.sjarvela.mollify.client.event.DefaultEventDispatcher::callbacks.push(cb);
	}-*/;

	private native void handleEvent(Event event) /*-{
		for (i=0; i < this.@org.sjarvela.mollify.client.event.DefaultEventDispatcher::callbacks.length; i++)
			this.@org.sjarvela.mollify.client.event.DefaultEventDispatcher::callbacks[i](event);
	}-*/;

}
