package org.sjarvela.mollify.client.filesystem;

import org.sjarvela.mollify.client.session.file.FilePermission;

import com.google.gwt.core.client.JavaScriptObject;

public class FolderDetails extends JavaScriptObject {

	protected FolderDetails() {
	}

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final FilePermission getFilePermission() {
		return FilePermission.fromString(getFilePermissionString());
	}

	public final native String getDescription() /*-{
		return this.description;
	}-*/;

	private final native String getFilePermissionString() /*-{
		return this.permissions;
	}-*/;

	public static FolderDetails create(FilePermission permissions,
			String description) {
		FolderDetails result = FolderDetails.createObject().cast();
		result.putValues(permissions.getStringValue(), description);
		return result;
	}

	private final native void putValues(String permissions, String description) /*-{
		this.permissions = permissions;
		this.description = description;
	}-*/;

	public final native void setDescription(String description) /*-{
		this.description = description;
	}-*/;

	public final native void removeDescription() /*-{
		this.description = null;
	}-*/;
}
