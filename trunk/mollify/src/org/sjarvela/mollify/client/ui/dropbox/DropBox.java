/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dropbox;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.js.JsFilesystemItem;

public interface DropBox {

	// public boolean isVisible();

	// public void toggle(Coords position);

	public void addItems(List<JsFilesystemItem> items);

	// public void close();

//	public Widget getWidget();

}
