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
		return this.permission;
	}-*/;

	public static FolderDetails create(FilePermission permission,
			String description) {
		FolderDetails result = FolderDetails.createObject().cast();
		result.putValues(permission.getStringValue(), description);
		return result;
	}

	private final native void putValues(String permission, String description) /*-{
		this.permission = permission;
		this.description = description;
	}-*/;

	public final native void setDescription(String description) /*-{
		this.description = description;
	}-*/;

	public final native void removeDescription() /*-{
		this.description = null;
	}-*/;
}
