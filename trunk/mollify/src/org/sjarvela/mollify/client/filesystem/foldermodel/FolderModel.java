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

import org.sjarvela.mollify.client.filesystem.Folder;

public class FolderModel implements CurrentFolderProvider {
	private Stack<Folder> folders;

	public FolderModel() {
		folders = new Stack<Folder>();
	}

	public Folder getRootFolder() {
		return folders.firstElement();
	}

	public void setRootFolder(Folder folder) {
		this.folders.clear();
		if (folder != null)
			this.folders.add(folder);
	}

	public Folder getCurrentFolder() {
		if (folders.isEmpty())
			return null;
		return folders.lastElement();
	}

	public int getLevels() {
		return folders.size();
	}

	public ListIterator<Folder> getFolders() {
		return this.folders.listIterator();
	}

	public List<Folder> getFolderList() {
		return Arrays.asList(this.folders.toArray(new Folder[0]));
	}

	public void descendIntoFolder(Folder folder) {
		this.folders.add(folder);
	}

	public Folder ascend() {
		return this.folders.pop();
	}

	public boolean canAscend() {
		return this.folders.size() > 0;
	}

	public boolean isRoot() {
		return this.folders.size() == 0;
	}

	public void changeFolder(int level, Folder folder) {
		if (level < 1 || level > (getLevels() + 1))
			throw new RuntimeException("Invalid folder (" + folder.getName()
					+ ") at level " + level);

		// first back up until target level is reached
		while (level <= getLevels())
			ascend();

		// then descend into the selected folder
		descendIntoFolder(folder);
	}

}
