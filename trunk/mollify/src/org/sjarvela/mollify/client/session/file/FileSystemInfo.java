package org.sjarvela.mollify.client.session.file;

import com.google.gwt.core.client.JavaScriptObject;

public class FileSystemInfo extends JavaScriptObject {
	protected FileSystemInfo() {
	}

	public final native int getUploadMaxFileSize() /*-{
		return this.max_upload_file_size;
	}-*/;

	public final native int getUploadMaxTotalSize() /*-{
		return this.max_upload_total_size;
	}-*/;

	public final String asString() {
		return "max_upload_file_size=" + getUploadMaxFileSize()
				+ ", max_upload_total_size=" + getUploadMaxTotalSize() + "]";
	}

	public static FileSystemInfo create(int maxFileSize, int maxTotalSize) {
		FileSystemInfo result = FileSystemInfo.createObject().cast();
		result.putValues(maxFileSize, maxTotalSize);
		return result;
	}
	
	private final native void putValues(int maxFileSize, int maxTotalSize) /*-{
		this.max_upload_file_size = maxFileSize;
		this.max_upload_total_size = maxTotalSize;
	}-*/;
}
