/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.foldermodel;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public interface FolderProvider {
	List<JsRootFolder> getRootFolders();

	JsRootFolder getRootFolder(String id);

	void getFolders(JsFolder parent, ResultListener<List<JsFolder>> listener);

}
