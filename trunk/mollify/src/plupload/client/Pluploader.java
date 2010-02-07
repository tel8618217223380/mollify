/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package plupload.client;

import java.util.List;

import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public final class Pluploader extends JavaScriptObject {
	static native Pluploader create(JavaScriptObject settings) /*-{
		return new $wnd.plupload.Uploader(settings);
	}-*/;

	protected Pluploader() {
	}

	public native void init() /*-{
		this.init();
	}-*/;

	public native void refresh() /*-{
		this.refresh();
	}-*/;

	public native File getFile(String id) /*-{
		return this.getFile(id);
	}-*/;

	public native void removeFile(File file) /*-{
		this.removeFile(file);
	}-*/;

	public final List<File> remove(int start, int count) {
		return JsUtil.asList(splice(start, count), File.class);
	}

	private native JsArray splice(int start, int count) /*-{
		return this.splice(start, count);
	}-*/;

	public native void bind(String name, JavaScriptObject func) /*-{
		return this.bind(name, func);
	}-*/;

	public native void unbind(String name, JavaScriptObject func) /*-{
		return this.unbind(name, func);
	}-*/;

	public native void start() /*-{
		this.start();
	}-*/;

	public native void stop() /*-{
		this.stop();
	}-*/;

	/*
	 * Uploader bind(name:String, func:function, scope:Object):void Adds an
	 * event listener by name. Uploader trigger(name:String,
	 * Multiple:Object..):void Dispatches the specified event name and it's
	 * arguments to all listeners. Uploader unbind(name:String,
	 * func:function):void Removes the specified event listener.
	 */
}
