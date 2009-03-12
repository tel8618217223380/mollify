/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.directorymodel;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import org.sjarvela.mollify.client.filesystem.Directory;

public class DirectoryModel {
	private Stack<Directory> folders;

	public DirectoryModel() {
		folders = new Stack<Directory>();
		setRootDirectory(Directory.Empty);
	}

	public Directory getRootDirectory() {
		return folders.firstElement();
	}

	public void setRootDirectory(Directory directory) {
		this.folders.clear();
		this.folders.add(directory);
	}

	public Directory getCurrentFolder() {
		return folders.lastElement();
	}

	public int getLevels() {
		return folders.size();
	}

	public ListIterator<Directory> getDirectories() {
		return this.folders.listIterator();
	}

	public List<Directory> getDirectoryList() {
		return Arrays.asList(this.folders.toArray(new Directory[0]));
	}

	public void descendIntoFolder(Directory folder) {
		this.folders.add(folder);
	}

	public Directory ascend() {
		return this.folders.pop();
	}

	public boolean canAscend() {
		return this.folders.size() > 1;
	}

	public void changeDirectory(int level, Directory directory) {
		if (level < 1 || level > (getLevels() + 1))
			throw new RuntimeException("Invalid directory ("
					+ directory.getName() + ") at level " + level);

		// first back up until target level is reached
		while (level <= getLevels())
			ascend();

		// then descend into the selected folder
		descendIntoFolder(directory);
	}
}
