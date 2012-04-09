/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session.file;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.js.JsFilesystemItem;

public class FileSystemItemCache {

	private final Map<String, JsFilesystemItem> itemsById = new HashMap();

	public FileSystemItemCache(List<JsFilesystemItem> items) {
		for (JsFilesystemItem item : items)
			itemsById.put(item.getId(), item);
	}

	public JsFilesystemItem getItem(String itemId) {
		return itemsById.get(itemId);
	}

}
