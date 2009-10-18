/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.flash;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.upload.FileUploadListener;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.session.file.FileSystemInfo;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;

public class FlashFileUploadDialogFactory implements FileUploadDialogFactory {
	private final TextProvider textProvider;
	private final FileUploadService service;
	private final FileSystemInfo info;

	public FlashFileUploadDialogFactory(TextProvider textProvider,
			FileUploadService fileUploadHandler, FileSystemInfo info) {
		this.textProvider = textProvider;
		this.service = fileUploadHandler;
		this.info = info;
	}

	public void create(Directory directory, FileUploadListener listener) {
		new FlashFileUploadDialog(directory, textProvider, service, info, null);
	}

}
