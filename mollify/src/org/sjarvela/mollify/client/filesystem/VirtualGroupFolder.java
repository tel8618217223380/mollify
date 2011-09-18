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

import java.util.ArrayList;
import java.util.List;


public class VirtualGroupFolder extends Folder {
	List<Folder> children = new ArrayList();

	public VirtualGroupFolder(String name, String path) {
		super(null, null, name, path, null);
	}

	@Override
	public boolean isRoot() {
		return true;
	}

	public void add(RootFolder f) {
		int level = this.getLevel();
		if (f.getGroupParts().size() > level) {
			String next = f.getGroupParts().get(level);
			VirtualGroupFolder groupFolder = new VirtualGroupFolder(name,
					this.path + "/" + next);
			groupFolder.add(f);
			children.add(groupFolder);
		} else {
			children.add(f);
		}
	}

	private int getLevel() {
		int i = 0;
		int count = 0;
		while (true) {
			i = this.path.indexOf("/", i);
			if (i < 0)
				break;
			count++;
		}
		return count + 1;
	}

	public List<Folder> getChildren() {
		return children;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof VirtualGroupFolder))
			return false;

		return path.equals(((VirtualGroupFolder) obj).path);
	}
}
