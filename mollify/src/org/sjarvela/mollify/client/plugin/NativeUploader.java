/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin;

/*import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeUploader implements FileUploadDialogFactory {

	private final JavaScriptObject uploader;

	public NativeUploader(JavaScriptObject uploader) {
		this.uploader = uploader;
	}

	@Override
	public void openFileUploadDialog(Folder folder, ResultListener listener) {
		invokeUploaderCallback(uploader, folder.asJs(),
				createNativeListener(this, listener));
	}

	private native final JavaScriptObject createNativeListener(
			NativeUploader uploader, ResultListener listener) /*-{
		var o = {};
		o.success = function() {
			uploader.@org.sjarvela.mollify.client.plugin.NativeUploader::onSuccess(Lorg/sjarvela/mollify/client/service/request/listener/ResultListener;)(listener);
		};
		o.fail = function(d) {
			uploader.@org.sjarvela.mollify.client.plugin.NativeUploader::onFail(Lorg/sjarvela/mollify/client/service/request/listener/ResultListener;Ljava/lang/String;)(listener,d);
		};
		return o;
	}-;

	protected void onSuccess(ResultListener listener) {
		listener.onSuccess(null);
	}

	protected void onFail(ResultListener listener, String details) {
		listener.onFail(new ServiceError(ServiceErrorType.UPLOAD_FAILED,
				details));
	}

	protected static native final void invokeUploaderCallback(
			JavaScriptObject cb, JavaScriptObject folder,
			JavaScriptObject listener) /*-{
		if (cb)
			cb(folder, listener);
	}-
}*/
