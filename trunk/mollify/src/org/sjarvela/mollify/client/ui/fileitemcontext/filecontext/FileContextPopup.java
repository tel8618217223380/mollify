/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.filecontext;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.handler.FileItemDescriptionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;

import com.google.gwt.user.client.Element;

public interface FileContextPopup {

	void setFileActionHandler(FileSystemActionHandler actionHandler);

	void setFileItemDescriptionHandler(
			FileItemDescriptionHandler descriptionHandler);

	void update(File file, Element element);

	void showMenu();

}
