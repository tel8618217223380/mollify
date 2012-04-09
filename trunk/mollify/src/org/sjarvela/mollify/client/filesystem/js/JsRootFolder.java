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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JsRootFolder extends JsFolder {
	// public static JsRootFolder create(RootFolder folder) {
	// return create(folder.getId(), folder.getName(), folder.getGroup());
	// }

	public static final JsRootFolder create(String id, String name, String group) {
		JsRootFolder result = JsFolder.createObject().cast();
		result.putValues(id, id, name, null, group);
		return result.cast();
	};

	private final native void putValues(String id, String rootId, String name,
			String parentId, String group) /*-{
		this.id = id;
		this.root_id = rootId;
		this.name = name;
		this.parent_id = parentId;
		this.is_file = false;
		this.group = group;
	}-*/;

	protected JsRootFolder() {
	}

	public final native String getGroup() /*-{
		return this.group;
	}-*/;

	public boolean hasGroup() {
		return getGroup() != null && !getGroup().isEmpty();
	}

	public List<String> getGroupParts() {
		if (!hasGroup())
			return Collections.EMPTY_LIST;
		return Arrays.asList(getGroup().split("/"));
	}
}
