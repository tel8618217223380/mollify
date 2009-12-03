/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.action.VoidActionHandler;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.Sort;
import org.sjarvela.mollify.client.ui.mainview.impl.DefaultMainView.Action;

public class MainViewGlue implements GridListener<FileSystemItem> {
	private final MainViewPresenter presenter;
	private final ActionDelegator actionDelegator;
	private final DefaultMainView view;

	public MainViewGlue(DefaultMainView view,
			final MainViewPresenter presenter, ActionDelegator actionDelegator) {
		this.view = view;
		this.presenter = presenter;
		this.actionDelegator = actionDelegator;

		view.addFileListListener(this);
		view.addViewListener(new ViewListener() {
			public void onShow() {
				presenter.initialize();
			}
		});

		initializeActions();
	}

	private void initializeActions() {
		actionDelegator.setActionHandler(Action.editItemPermissions,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onEditItemPermissions();
					}
				});

		actionDelegator.setActionHandler(Action.logout,
				new VoidActionHandler() {
					public void onAction() {
						presenter.logout();
					}
				});

		actionDelegator.setActionHandler(Action.refresh,
				new VoidActionHandler() {
					public void onAction() {
						presenter.reload();
					}
				});

		actionDelegator.setActionHandler(Action.addFile,
				new VoidActionHandler() {
					public void onAction() {
						presenter.openUploadDialog();
					}
				});

		actionDelegator.setActionHandler(Action.addDirectory,
				new VoidActionHandler() {
					public void onAction() {
						presenter.openNewDirectoryDialog();
					}
				});

		actionDelegator.setActionHandler(Action.changePassword,
				new VoidActionHandler() {
					public void onAction() {
						presenter.changePassword();
					}
				});

		actionDelegator.setActionHandler(Action.configure,
				new VoidActionHandler() {
					public void onAction() {
						presenter.configure();
					}
				});
	}

	public void onColumnClicked(FileSystemItem item, GridColumn column) {
		presenter.onFileSystemItemSelected(item, column);
	}

	public void onIconClicked(FileSystemItem item) {
		if (item.isFile() || item.equals(Folder.Parent))
			return;
		view.showDirectoryContext((Folder) item);
	}

	public void onColumnSorted(GridColumn column, Sort sort) {
		presenter.setListOrder(column, sort);
	}

	public void onSelectionChanged(List<FileSystemItem> selected) {
	}
}
