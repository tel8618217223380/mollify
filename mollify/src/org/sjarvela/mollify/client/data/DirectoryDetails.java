package org.sjarvela.mollify.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class DirectoryDetails extends JavaScriptObject {

	protected DirectoryDetails() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final FilePermission getFilePermission() {
		return FilePermission.fromString(getFilePermissionString());
	}

	private final native String getFilePermissionString() /*-{
		return this.permissions;
	}-*/;

}
