package org.sjarvela.mollify.client.session;

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
}
