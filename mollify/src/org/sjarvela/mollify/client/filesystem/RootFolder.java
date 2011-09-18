/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;

public class RootFolder extends Folder {
	private final String group;

	public RootFolder(String id, String name, String group) {
		super(id, id, name, "", null);
		this.group = group != null ? group.trim() : null;
	}

	public RootFolder(JsRootFolder folder) {
		this(folder.getId(), folder.getName(), folder.getGroup());
	}

	public boolean hasGroup() {
		return group != null && !group.isEmpty();
	}

	public List<String> getGroupParts() {
		if (!hasGroup())
			return Collections.EMPTY_LIST;
		return Arrays.asList(group.split("/"));
	}

	public String getGroup() {
		return group;
	}
}
