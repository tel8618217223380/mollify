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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.FileView;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionListener;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.action.VoidActionHandler;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;
import org.sjarvela.mollify.client.ui.mainview.impl.DefaultMainView.Action;

import com.google.gwt.dom.client.Element;

public class MainViewGlue implements GridListener<FileSystemItem>, FileView {
	private static Logger logger = Logger.getLogger(MainViewGlue.class
			.getName());

	private final MainViewPresenter presenter;
	private final ActionDelegator actionDelegator;
	private final DefaultMainView view;

	public MainViewGlue(DefaultMainView view,
			final MainViewPresenter presenter,
			FileSystemActionHandler fileSystemActionHandler,
			ActionDelegator actionDelegator) {
		this.view = view;
		this.presenter = presenter;
		this.actionDelegator = actionDelegator;

		fileSystemActionHandler.addListener(new FileSystemActionListener() {
			@Override
			public void onFileSystemAction(FileSystemAction action) {
				presenter.reload();
			}
		});
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
						presenter.openNewFolderDialog();
					}
				});

		actionDelegator.setActionHandler(Action.retrieveUrl,
				new VoidActionHandler() {
					public void onAction() {
						presenter.retrieveFromUrl();
					}
				});

		actionDelegator.setActionHandler(Action.changePassword,
				new VoidActionHandler() {
					public void onAction() {
						presenter.changePassword();
					}
				});

		actionDelegator.setActionHandler(Action.selectMode,
				new VoidActionHandler() {
					@Override
					public void onAction() {
						presenter.onToggleSelectMode();
					}
				});

		actionDelegator.setActionHandler(Action.selectAll,
				new VoidActionHandler() {
					@Override
					public void onAction() {
						presenter.onSelectAll();
					}
				});

		actionDelegator.setActionHandler(Action.selectNone,
				new VoidActionHandler() {
					@Override
					public void onAction() {
						presenter.onSelectNone();
					}
				});

		actionDelegator.setActionHandler(Action.admin, new VoidActionHandler() {
			public void onAction() {
				presenter.onOpenAdministration();
			}
		});

		actionDelegator.setActionHandler(Action.copyMultiple,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onCopySelected();
					}
				});

		actionDelegator.setActionHandler(Action.moveMultiple,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onMoveSelected();
					}
				});

		actionDelegator.setActionHandler(Action.deleteMultiple,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onDeleteSelected();
					}
				});

		actionDelegator.setActionHandler(Action.slideBar,
				new VoidActionHandler() {
					@Override
					public void onAction() {
						presenter.onToggleSlidebar();
					}
				});

		actionDelegator.setActionHandler(Action.addToDropbox,
				new VoidActionHandler() {
					@Override
					public void onAction() {
						presenter.onAddSelectedToDropbox();
					}
				});

		actionDelegator.setActionHandler(Action.listView,
				new VoidActionHandler() {
					@Override
					public void onAction() {
						presenter.onShowListView();
					}
				});

		actionDelegator.setActionHandler(Action.gridViewLarge,
				new VoidActionHandler() {
					@Override
					public void onAction() {
						presenter.onShowGridView(false);
					}
				});

		actionDelegator.setActionHandler(Action.gridViewSmall,
				new VoidActionHandler() {
					@Override
					public void onAction() {
						presenter.onShowGridView(true);
					}
				});
	}

	public void onColumnClicked(FileSystemItem item, String columnId, Element e) {
		presenter.onFileSystemItemSelected(item, columnId, e);
	}

	@Override
	public void onIconClicked(FileSystemItem item, Element e) {
		if (item.equals(Folder.Parent))
			return;

		view.showItemContext(item, e);
	}

	public void onColumnSorted(String columnId, SortOrder sort) {
		presenter.setListOrder(columnId, sort);
	}

	public void onSelectionChanged(List<FileSystemItem> selected) {
		presenter.onFileSystemItemSelectionChanged(selected);
	}

	@Override
	public void onRendered() {
		presenter.onListRendered();
	}

	@Override
	public void refreshCurrentFolder() {
		presenter.reload();
	}

	@Override
	public Folder getCurrentFolder() {
		return presenter.getCurrentFolder();
	}

	@Override
	public void setCurrentFolder(String id) {
		logger.log(Level.FINE, id);
		presenter.setCurrentFolder(id);
	}

	@Override
	public List<FileSystemItem> getAllItems() {
		return presenter.getAllItems();
	}

	@Override
	public void openUploader(boolean forceBasic) {
		presenter.openUploadDialog();
	}
}
