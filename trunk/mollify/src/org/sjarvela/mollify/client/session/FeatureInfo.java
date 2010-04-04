/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session;

import com.google.gwt.core.client.JavaScriptObject;

public class FeatureInfo extends JavaScriptObject {
	public static FeatureInfo create(boolean folderActions, boolean fileUpload,
			boolean fileUploadProgress, boolean zipDownload,
			boolean descriptionUpdate, boolean changePassword,
			boolean permissionUpdate, boolean administration,
			boolean userGroups, boolean filePreview, boolean fileView,
			boolean publicLinks) {
		FeatureInfo result = FeatureInfo.createObject().cast();
		result.putValues(folderActions, fileUpload, fileUploadProgress,
				zipDownload, descriptionUpdate, changePassword,
				permissionUpdate, administration, userGroups, filePreview,
				fileView, publicLinks);
		return result;
	}

	protected FeatureInfo() {
	}

	public final native boolean folderActions() /*-{
		return this.folder_actions;
	}-*/;

	public final native boolean fileUpload() /*-{
		return this.file_upload;
	}-*/;

	public final native boolean fileUploadProgress() /*-{
		return this.file_upload_progress;
	}-*/;

	public final native boolean zipDownload() /*-{
		return this.zip_download;
	}-*/;

	public final native boolean descriptionUpdate() /*-{
		return this.description_update;
	}-*/;

	public final native boolean changePassword() /*-{
		return this.change_password;
	}-*/;

	public final native boolean permissionUpdate() /*-{
		return this.permission_update;
	}-*/;

	public final native boolean administration() /*-{
		return this.administration;
	}-*/;

	public final native boolean userGroups() /*-{
		return this.user_groups;
	}-*/;

	public final native boolean filePreview() /*-{
		return this.file_preview;
	}-*/;

	public final native boolean fileView() /*-{
		return this.file_view;
	}-*/;

	public final native boolean publicLinks() /*-{
		return this.public_links;
	}-*/;

	private final native void putValues(boolean folderActions,
			boolean fileUpload, boolean fileUploadProgress,
			boolean zipDownload, boolean descriptionUpdate,
			boolean changePassword, boolean permissionUpdate,
			boolean administration, boolean userGroups, boolean filePreview,
			boolean fileView, boolean publicLinks) /*-{
		this.folder_actions = folderActions;
		this.file_upload = fileUpload;
		this.file_upload_progress = fileUploadProgress;
		this.zip_download = zipDownload;
		this.change_password = changePassword;
		this.description_update = descriptionUpdate;
		this.permission_update = permissionUpdate;
		this.administration = administration;
		this.user_groups = userGroups;
		this.file_preview = filePreview;
		this.file_view = fileView;
		this.public_links = publicLinks;
	}-*/;
}
