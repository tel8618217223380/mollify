/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.flash;

import java.util.ArrayList;
import java.util.List;

import org.swfupload.client.File;

public class UploadModel {
	private final List<File> files;
	private final List<File> completed = new ArrayList();

	private File current = null;
	private long totalSize = 0l;
	private long completedBytes = 0l;

	public UploadModel(List<File> files) {
		this.files = files;
		for (File f : files)
			totalSize += f.getSize();
	}

	public File nextFile() {
		if (files.size() == 0)
			return null;
		int index = 0;
		if (current != null)
			index = files.indexOf(current) + 1;
		if (index >= files.size())
			return null;
		if (current != null)
			completedBytes += current.getSize();
		current = files.get(index);
		return current;
	}

	public boolean hasNext() {
		if (current == null)
			return !files.isEmpty();
		return files.indexOf(current) < (files.size() - 1);
	}

	public void uploadComplete(File file) {
		File f = getFile(file);
		completed.add(f);
		completedBytes += f.getSize();
	}

	private File getFile(File file) {
		for (File f : files)
			if (f.getId().equals(file.getId()))
				return f;
		return null;
	}

	public long getTotalBytes() {
		return totalSize;
	}

	public long getTotalProgress(long bytesComplete) {
		if (current == null)
			return 0l;
		return completedBytes + bytesComplete;
	}

}
