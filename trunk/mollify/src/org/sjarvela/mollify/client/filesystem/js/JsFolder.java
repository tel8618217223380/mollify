/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.js;

public class JsFolder extends JsFilesystemItem {
	public static JsFolder Empty = new JsFolder();
	
	// public static JsFolder create(Folder folder) {
	// return create(folder.getId(), folder.getRootId(), folder.getName(),
	// folder.getParentId());
	// }

	public static JsFolder create(String id, String rootId, String name,
			String parentId) {
		JsFolder result = JsFolder.createObject().cast();
		result.putValues(id, rootId, name, parentId);
		return result;
	}

	protected JsFolder() {
	}

	private final native void putValues(String id, String rootId, String name,
			String parentId) /*-{
		this.id = id;
		this.root_id = rootId;
		this.name = name;
		this.parent_id = parentId;
		this.is_file = false;
	}-*/;

	public boolean isRoot() {
		return this.getId().equals(this.getRootId());
	}

	public boolean isEmpty() {
		return this == Empty;
	}

}
