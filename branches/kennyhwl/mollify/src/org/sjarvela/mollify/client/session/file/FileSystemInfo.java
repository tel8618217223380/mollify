package org.sjarvela.mollify.client.session.file;

import java.util.List;

import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public class FileSystemInfo extends JavaScriptObject {
	protected FileSystemInfo() {
	}

	public final native String getFolderSeparator() /*-{
		return this.folder_separator;
	}-*/;

	public final native float getUploadMaxFileSize() /*-{
		return this.max_upload_file_size;
	}-*/;

	public final native float getUploadMaxTotalSize() /*-{
		return this.max_upload_total_size;
	}-*/;

	private final native JsArrayString getAllowedFileUploadTypesArray() /*-{
		return this.allowed_file_upload_types;
	}-*/;

	public final native String getInboxPath() /*-{
		return this.inbox_path;
	}-*/;
	
	public final List<String> getAllowedFileUploadTypes() {
		return JsUtil.asList(getAllowedFileUploadTypesArray());
	}

	public final String asString() {
		return "max_upload_file_size=" + getUploadMaxFileSize()
				+ ", max_upload_total_size=" + getUploadMaxTotalSize() + "]";
	}

	public static FileSystemInfo create(String folderSeparator,
			int maxFileSize, int maxTotalSize,
			List<String> allowedFileUploadTypes) {
		FileSystemInfo result = FileSystemInfo.createObject().cast();
		result.putValues(folderSeparator, maxFileSize, maxTotalSize, JsUtil
				.asArray(allowedFileUploadTypes));
		return result;
	}

	private final native void putValues(String folderSeparator,
			int maxFileSize, int maxTotalSize,
			JsArrayString allowedFileUploadTypes) /*-{
		this.folder_separator = folderSeparator;
		this.max_upload_file_size = maxFileSize;
		this.max_upload_total_size = maxTotalSize;
		this.allowed_file_upload_types = allowedFileUploadTypes;
	}-*/;
}
