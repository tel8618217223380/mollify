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
import org.sjarvela.mollify.client.filesystem.foldermodel.CurrentFolderProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.service.Callback;
import org.sjarvela.mollify.client.session.SessionInfo;

public class DropBoxPresenter {
	private final DropBoxView view;
	private final FileSystemActionHandler fileItemActionHandler;
	private final List<FileSystemItem> items = new ArrayList();
	private final SessionInfo session;
	private final CurrentFolderProvider currentFolderProvider;

	public DropBoxPresenter(DropBoxView view, SessionInfo session,
			FileSystemActionHandler actionHandler,
			CurrentFolderProvider currentFolderProvider) {
		this.view = view;
		this.session = session;
		this.fileItemActionHandler = actionHandler;
		this.currentFolderProvider = currentFolderProvider;
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
		fileItemActionHandler.onAction(items, FileSystemAction.delete, null,
				view.getActionButton(), createSuccessCallback());
	}

	public void onCopyItems() {
		fileItemActionHandler.onAction(items, FileSystemAction.copy, null, view
				.getActionButton(), createSuccessCallback());
	}

	public void onCopyHereItems() {
		fileItemActionHandler.onAction(items, FileSystemAction.copy,
				currentFolderProvider.getCurrentFolder(), view
						.getActionButton(), createSuccessCallback());
	}

	public void onMoveItems() {
		fileItemActionHandler.onAction(items, FileSystemAction.move, null, view
				.getActionButton(), createSuccessCallback());
	}

	public void onMoveHereItems() {
		fileItemActionHandler.onAction(items, FileSystemAction.move,
				currentFolderProvider.getCurrentFolder(), view
						.getActionButton(), createSuccessCallback());
	}

	public void onDownloadAsZip() {
		fileItemActionHandler.onAction(items, FileSystemAction.download_as_zip,
				null, view.getActionButton(), null);
	}

	private Callback createSuccessCallback() {
		return new Callback() {
			@Override
			public void onCallback() {
				onClear();
			}
		};
	}

}
