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

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import org.sjarvela.mollify.client.filesystem.js.JsFolder;

public class FolderModel implements CurrentFolderProvider {
	private Stack<JsFolder> folders;

	public FolderModel() {
		folders = new Stack<JsFolder>();
	}

	public JsFolder getRootFolder() {
		return folders.firstElement();
	}

	public void setRootFolder(JsFolder folder) {
		this.folders.clear();
		if (folder != null)
			this.folders.add(folder);
	}

	public JsFolder getCurrentFolder() {
		if (folders.isEmpty())
			return null;
		return folders.lastElement();
	}

	public int getLevels() {
		return folders.size();
	}

	public ListIterator<JsFolder> getFolders() {
		return this.folders.listIterator();
	}

	public List<JsFolder> getFolderList() {
		return Arrays.asList(this.folders.toArray(new JsFolder[0]));
	}

	public void descendIntoFolder(JsFolder folder) {
		this.folders.add(folder);
	}

	public JsFolder ascend() {
		return this.folders.pop();
	}

	public boolean canAscend() {
		return this.folders.size() > 0;
	}

	public boolean isRoot() {
		return this.folders.size() == 0;
	}

	public void changeFolder(int level, JsFolder folder) {
		if (level < 1 || level > (getLevels() + 1))
			throw new RuntimeException("Invalid folder (" + folder.getName()
					+ ") at level " + level);

		// first back up until target level is reached
		while (level <= getLevels())
			ascend();

		// then descend into the selected folder
		descendIntoFolder(folder);
	}

	public void setFolderHierarchy(List<JsFolder> hierarchy) {
		folders.clear();
		for (JsFolder f : hierarchy)
			descendIntoFolder(f);
	}

}
