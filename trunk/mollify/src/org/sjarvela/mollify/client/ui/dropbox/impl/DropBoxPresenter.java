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
import org.sjarvela.mollify.client.service.Callback;
import org.sjarvela.mollify.client.session.SessionInfo;

public class DropBoxPresenter {
	private final DropBoxView view;
	private final FileSystemActionHandler fileItemActionHandler;
	private final List<FileSystemItem> items = new ArrayList();
	private final SessionInfo session;

	public DropBoxPresenter(DropBoxView view, SessionInfo session,
			FileSystemActionHandler actionHandler) {
		this.view = view;
		this.session = session;
		this.fileItemActionHandler = actionHandler;
	}

	public void onDropItems(List<FileSystemItem> items) {
		for (FileSystemItem item : items)
			addItem(item);
		refreshContent();
	}

	private void addItem(FileSystemItem item) {
		if (this.items.contains(item))
			return;
		if (!item.isFile() && !session.getFeatures().folderActions())
			return;
		this.items.add(item);
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
		fileItemActionHandler.onAction(items, FileSystemAction.delete, view, new Callback() {
			@Override
			public void onCallback() {
				onClear();
			}});
	}

}
