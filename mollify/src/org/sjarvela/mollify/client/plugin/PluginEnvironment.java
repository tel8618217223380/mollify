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

import java.util.List;

import org.sjarvela.mollify.client.FileView;
import org.sjarvela.mollify.client.plugin.filelist.NativeFileListInterface;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;

import com.google.gwt.core.client.JavaScriptObject;

public interface PluginEnvironment {

	void onPluginsInitialized(List<Plugin> plugins);

	JavaScriptObject getJsEnv(FileView filesystem, String pluginBaseUrl);

	FileUploadDialogFactory getCustomUploader();

	NativeFileListInterface getFileListExt();

}
