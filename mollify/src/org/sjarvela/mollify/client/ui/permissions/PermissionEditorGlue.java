/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.permissions;

/*import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.action.VoidActionHandler;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;

import com.google.gwt.dom.client.Element;

public class PermissionEditorGlue {
	private final PermissionEditorView view;

	public PermissionEditorGlue(final PermissionEditorPresenter presenter,
			final PermissionEditorView view, ActionDelegator actionDelegator) {
		this.view = view;

		view.addViewListener(new ViewListener() {
			public void onShow() {
				presenter.initialize();
			}
		});

		view.getList().addListener(new GridListener<FileItemUserPermission>() {
			@Override
			public void onColumnClicked(FileItemUserPermission t,
					String columnId, Element e) {
			}

			@Override
			public void onColumnSorted(String columnId, SortOrder sort) {
			}

			@Override
			public void onIconClicked(FileItemUserPermission t, Element e) {
			}

			@Override
			public void onMenuClicked(FileSystemItem item, Element e) {
			}

			@Override
			public void onSelectionChanged(List<FileItemUserPermission> selected) {
				updateButtons(selected.size() == 1);
			}

			@Override
			public void onRendered() {
			}
		});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.selectItem,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onSelectItem();
					}
				});

		actionDelegator.setActionHandler(PermissionEditorView.Actions.ok,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onOk();
					}
				});

		actionDelegator.setActionHandler(PermissionEditorView.Actions.cancel,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onClose();
					}
				});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.addUserPermission,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onAddPermission();
					}
				});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.addUserGroupPermission,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onAddGroupPermission();
					}
				});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.editPermission,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onEditPermission();
					}
				});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.removePermission,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onRemovePermission();
					}
				});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.defaultPermissionChanged,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onDefaultPermissionChanged(view
								.getDefaultPermission().getSelectedItem());
					}
				});

		view.show();
	}

	protected void updateButtons(boolean selected) {
		view.getEditPermissionButton().setEnabled(selected);
		view.getRemovePermissionButton().setEnabled(selected);
	}

}*/
