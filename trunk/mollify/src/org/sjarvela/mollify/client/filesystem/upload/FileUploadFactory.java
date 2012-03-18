/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.upload;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.plugin.ClientInterface;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;

public class FileUploadFactory implements FileUploadDialogFactory {

	private final FileUploadDialogFactory def;
	private final ClientInterface pluginEnv;

	public FileUploadFactory(FileUploadDialogFactory def,
			ClientInterface pluginEnv) {
		this.def = def;
		this.pluginEnv = pluginEnv;
	}

	@Override
	public void openFileUploadDialog(Folder current, ResultListener listener) {
		if (pluginEnv.getCustomUploader() != null) {
			pluginEnv.getCustomUploader().openFileUploadDialog(current,
					listener);
		} else {
			def.openFileUploadDialog(current, listener);
		}

	}

}
