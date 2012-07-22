package org.sjarvela.mollify.client.filesystem;

import org.sjarvela.mollify.client.filesystem.js.JsFolder;

import com.google.gwt.core.client.JavaScriptObject;

public class FolderDef extends JavaScriptObject {
	public static FolderDef create(String id, String name, String path) {
		FolderDef result = JsFolder.createObject().cast();
		result.putValues(id, name, path);
		return result;
	}

	protected FolderDef() {
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
