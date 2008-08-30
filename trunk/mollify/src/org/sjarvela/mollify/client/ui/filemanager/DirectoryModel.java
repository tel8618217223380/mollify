/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.filemanager;

import java.util.ListIterator;
import java.util.Stack;

import org.sjarvela.mollify.client.data.Directory;

import com.google.gwt.core.client.GWT;

public class DirectoryModel {
	private Stack<Directory> folders;

	public DirectoryModel() {
		folders = new Stack<Directory>();
		setRootDirectory(Directory.Empty());
	}

	public Directory getRootDirectory() {
		return folders.firstElement();
	}

	public void setRootDirectory(Directory directory) {
		if (!directory.isEmpty())
			GWT.log("Current root directory: " + directory.getName(), null);
		this.folders.clear();
		this.folders.add(directory);
	}

	public Directory getCurrentFolder() {
		return folders.lastElement();
	}

	public int getLevels() {
		return folders.size();
	}

	public ListIterator<Directory> getDirectoryList() {
		return this.folders.listIterator();
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
		if (level < 0 || level >= getLevels())
			throw new RuntimeException("Invalid directory ("
					+ directory.getName() + ") at level " + level);

		// first back up until level is reached
		while (level < getLevels())
			ascend();

		// then ascend into the selected folder
		descendIntoFolder(directory);
	}
}
