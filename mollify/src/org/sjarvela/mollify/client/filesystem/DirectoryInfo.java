package org.sjarvela.mollify.client.filesystem;

import org.sjarvela.mollify.client.filesystem.js.JsDirectory;

import com.google.gwt.core.client.JavaScriptObject;

public class DirectoryInfo extends JavaScriptObject {
	public static DirectoryInfo create(String id, String name, String path) {
		DirectoryInfo result = JsDirectory.createObject().cast();
		result.putValues(id, name, path);
		return result;
	}

	protected DirectoryInfo() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getPath() /*-{
		return this.path;
	}-*/;

	protected final native void putValues(String id, String name, String path) /*-{
		this.id = id;
		this.name = name;
		this.path = path
	}-*/;
}
