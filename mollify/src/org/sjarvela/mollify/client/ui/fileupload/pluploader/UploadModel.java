/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.pluploader;

import java.util.ArrayList;
import java.util.List;

import plupload.client.File;

public class UploadModel {
	private final List<File> files;
	private final List<File> completed = new ArrayList();

	private File current = null;
	private long totalSize = 0l;
	private long completedBytes = 0l;
	private long lastTotal = 0l;

	public UploadModel(List<File> files) {
		this.files = files;
		for (File f : files)
			totalSize += f.getSize();
	}

	public void start(File file) {
		if (current != null) {
			completed.add(current);
			completedBytes += current.getSize();
		}
		current = file;
	}

	public void complete(File file) {
		completed.add(getFile(file));
		completedBytes += file.getSize();
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

	public void updateProgress(long bytesComplete) {
		if (current == null)
			return;
		lastTotal = completedBytes + bytesComplete;
	}

	public void cancelFile(File f) {
		File file = getFile(f);
		if (file == null)
			return;
		totalSize -= f.getSize();
		files.remove(file);
		if (file.equals(current))
			lastTotal = getCompletedSize();
	}

	private long getCompletedSize() {
		long s = 0l;
		for (File f : completed)
			s += f.getSize();
		return s;
	}

	public boolean isCompleted(File f) {
		return completed.contains(getFile(f));
	}

	public File getCurrentFile() {
		return current;
	}

	public long getTotalProgress() {
		return lastTotal;
	}

	public double getTotalPercentage() {
		return (((double) getTotalProgress() / (double) getTotalBytes()) * 100d);
	}

	public double getPercentage(long bytesComplete, long bytesTotal) {
		if (bytesTotal == 0d || bytesComplete == 0d)
			return 0d;
		return (((double) bytesComplete / (double) bytesTotal) * 100d);
	}

}
