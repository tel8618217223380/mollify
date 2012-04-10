package org.sjarvela.mollify.client.filesystem.js;

import com.google.gwt.core.client.JavaScriptObject;

public class JsFilesystemItem extends JavaScriptObject {
	protected JsFilesystemItem() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getRootId() /*-{
		return this.root_id;
	}-*/;

	public final native String getParentId() /*-{
		return this.parent_id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getPath() /*-{
		return this.path;
	}-*/;

	public final native boolean isFile() /*-{
		return this.is_file;
	}-*/;
}
