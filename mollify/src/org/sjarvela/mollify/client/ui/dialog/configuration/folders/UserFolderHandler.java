/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog.configuration.folders;

import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.filesystem.UserDirectory;
import org.sjarvela.mollify.client.service.request.Callback;

public interface UserFolderHandler {

	void addUserFolder(DirectoryInfo directory, String name,
			Callback successCallback);

	void editUserFolder(UserDirectory edited, String effectiveName,
			Callback successCallback);

}
