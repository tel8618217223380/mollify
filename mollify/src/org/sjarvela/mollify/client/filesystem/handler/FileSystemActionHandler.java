/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.handler;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.mainview.impl.FileSystemActionListener;

import com.google.gwt.user.client.ui.Widget;

public interface FileSystemActionHandler {

	void onAction(FileSystemItem item, FileSystemAction action, Widget source);

	void onAction(List<FileSystemItem> items, FileSystemAction action,
			Widget source);

	void addListener(FileSystemActionListener fileSystemActionListener);

}
