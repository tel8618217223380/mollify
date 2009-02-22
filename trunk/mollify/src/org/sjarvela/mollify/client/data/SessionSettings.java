/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class SessionSettings extends JavaScriptObject {
	public static SessionSettings create(boolean folderActionsEnabled,
			boolean fileUploadEnabled, boolean fileUploadProgressEnabled,
			boolean zipDownloadEnabled) {
		SessionSettings result = SessionSettings.createObject().cast();
		result.putValues(folderActionsEnabled, fileUploadEnabled,
				fileUploadProgressEnabled, zipDownloadEnabled);
		return result;
	}

	protected SessionSettings() {
	}

	public final native boolean isFolderActionsEnabled() /*-{
		return this.enable_folder_actions;
	}-*/;

	public final native boolean isFileUploadEnabled() /*-{
		return this.enable_file_upload;
	}-*/;

	public final native boolean isFileUploadProgressEnabled() /*-{
		return this.enable_file_upload_progress;
	}-*/;

	public final native boolean isZipDownloadEnabled() /*-{
		return this.enable_zip_download;
	}-*/;

	public final String asString() {
		return "folder_actions_enabled=" + isFolderActionsEnabled()
				+ ", file_upload_enabled=" + isFileUploadEnabled()
				+ ", file_upload_progress_enabled="
				+ isFileUploadProgressEnabled() + ", zip_download_enabled="
				+ isZipDownloadEnabled();
	}

	private final native void putValues(boolean folderActionsEnabled,
			boolean fileUploadEnabled, boolean fileUploadProgressEnabled,
			boolean zipDownloadEnabled) /*-{
		this.enable_folder_actions = folderActionsEnabled;
		this.enable_file_upload = fileUploadEnabled;
		this.enable_file_upload_progress = fileUploadProgressEnabled;
		this.enable_zip_download = zipDownloadEnabled;
	}-*/;
}
