/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dropbox.impl;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;

public class DropBoxPresenter {
	private final DropBoxView view;
	private final FileSystemActionHandler fileItemActionHandler;
	private final List<FileSystemItem> items = new ArrayList();

	public DropBoxPresenter(DropBoxView view,
			FileSystemActionHandler actionHandler) {
		this.view = view;
		this.fileItemActionHandler = actionHandler;
	}

	public void onDropItems(List<FileSystemItem> items) {
		for (FileSystemItem item : items)
			if (!this.items.contains(item))
				this.items.add(item);
		refreshContent();
	}

	public void onRemove(FileSystemItem item) {
		items.remove(item);
		refreshContent();
	}

	private void refreshContent() {
		view.setContent(this.items);
	}

	public void onDragEnter() {
		view.onDragEnter();
	}

	public void onDragLeave() {
		view.onDragLeave();
	}

	public void onClear() {
		this.items.clear();
		refreshContent();
	}

	public void onDeleteItems() {
		fileItemActionHandler.onAction(items, FileSystemAction.delete, view);
	}

}
