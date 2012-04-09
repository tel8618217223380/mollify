/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.http;

/*import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dialog.ProgressDialogFactory;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;

public class HttpFileUploadDialogFactory implements FileUploadDialogFactory {
	private final TextProvider textProvider;
	private final FileUploadService service;
	private final SessionProvider sessionProvider;
	private final ServiceEnvironment env;
	private final DialogManager dialogManager;

	public HttpFileUploadDialogFactory(ServiceEnvironment env,
			TextProvider textProvider, FileUploadService service,
			SessionProvider sessionProvider, DialogManager dialogManager) {
		this.env = env;
		this.textProvider = textProvider;
		this.service = service;
		this.sessionProvider = sessionProvider;
		this.dialogManager = dialogManager;
	}

	public void openFileUploadDialog(Folder directory, ResultListener listener) {
		FileUploadListener fileUploadHandler = new HttpFileUploadHandler(env
				.getFileUploadService(), sessionProvider.getSession()
				.getFeatures().fileUploadProgress(), textProvider, listener,
				new ProgressDialogFactory());
		new HttpFileUploadDialog(directory, textProvider, service,
				sessionProvider.getSession().getFileSystemInfo(),
				fileUploadHandler, dialogManager).center();
	}

}*/
