package org.sjarvela.mollify.client.session.file;

import java.util.List;

import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class FileSystemInfo extends JavaScriptObject {
	protected FileSystemInfo() {
	}

	public final native int getUploadMaxFileSize() /*-{
		return this.max_upload_file_size;
	}-*/;

	public final native int getUploadMaxTotalSize() /*-{
		return this.max_upload_total_size;
	}-*/;

	private final native JsArrayString getAllowedFileUploadTypesArray() /*-{
		return this.allowed_file_upload_types;
	}-*/;

	public final List<String> getAllowedFileUploadTypes() {
		return JsUtil.asList(getAllowedFileUploadTypesArray());
	}

	public final String asString() {
		return "max_upload_file_size=" + getUploadMaxFileSize()
				+ ", max_upload_total_size=" + getUploadMaxTotalSize() + "]";
	}

	public static FileSystemInfo create(int maxFileSize, int maxTotalSize,
			List<String> allowedFileUploadTypes) {
		FileSystemInfo result = FileSystemInfo.createObject().cast();
		result.putValues(maxFileSize, maxTotalSize, allowedFileUploadTypes
				.toArray(new String[0]));
		return result;
	}

	private final native void putValues(int maxFileSize, int maxTotalSize,
			String[] allowedFileUploadTypes) /*-{
		this.max_upload_file_size = maxFileSize;
		this.max_upload_total_size = maxTotalSize;
		this.allowed_file_upload_types = allowedFileUploadTypes;
	}-*/;
}
