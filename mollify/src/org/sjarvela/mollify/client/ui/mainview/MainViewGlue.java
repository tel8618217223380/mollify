/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.ui.ActionId;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.filelist.Column;
import org.sjarvela.mollify.client.ui.filelist.SimpleFileListListener;
import org.sjarvela.mollify.client.ui.mainview.MainView.Action;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

public class MainViewGlue implements SimpleFileListListener, ViewListener {
	private final MainView view;
	private final MainViewPresenter presenter;
	private final ActionDelegator actionDelegator;

	public MainViewGlue(MainView view, final MainViewPresenter presenter,
			ActionDelegator actionDelegator) {
		this.view = view;
		this.presenter = presenter;
		this.actionDelegator = actionDelegator;

		view.addFileListListener(this);
		view.addViewListener(this);

		initializeActions();
	}

	public void onViewLoad() {
		presenter.initialize();
	}

	private void initializeActions() {
		view.getRefreshButton().addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				presenter.reload();
			}
		});

		view.getParentDirButton().addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				presenter.moveToParentDirectory();
			}
		});

		view.getLogoutButton().addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				presenter.logout();
			}
		});

		actionDelegator.setActionListener(Action.addFile, new ActionListener() {

			public void onActionTriggered(ActionId action) {
				presenter.openUploadDialog();
			}
		});

		actionDelegator.setActionListener(Action.addDirectory,
				new ActionListener() {
					public void onActionTriggered(ActionId action) {
						presenter.openNewDirectoryDialog();
					}
				});
	}

	public void onRowClicked(FileSystemItem item, Column column) {
		presenter.onFileSystemItemSelected(item, column);
	}
}
