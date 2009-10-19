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

import java.util.List;

import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.action.ActionHandler;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.Sort;

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
			public void onColumnClicked(FileItemUserPermission t,
					GridColumn column) {
			}

			public void onColumnSorted(GridColumn column, Sort sort) {
			}

			public void onIconClicked(FileItemUserPermission t) {
			}

			public void onSelectionChanged(List<FileItemUserPermission> selected) {
				updateButtons(selected.size() == 1);
			}
		});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.selectItem, new ActionHandler() {
					public void onAction() {
						presenter.onSelectItem();
					}
				});

		actionDelegator.setActionHandler(PermissionEditorView.Actions.ok,
				new ActionHandler() {
					public void onAction() {
						presenter.onOk();
					}
				});

		actionDelegator.setActionHandler(PermissionEditorView.Actions.cancel,
				new ActionHandler() {
					public void onAction() {
						presenter.onClose();
					}
				});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.addPermission,
				new ActionHandler() {
					public void onAction() {
						presenter.onAddPermission();
					}
				});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.editPermission,
				new ActionHandler() {
					public void onAction() {
						presenter.onEditPermission();
					}
				});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.removePermission,
				new ActionHandler() {
					public void onAction() {
						presenter.onRemovePermission();
					}
				});

		actionDelegator.setActionHandler(
				PermissionEditorView.Actions.defaultPermissionChanged,
				new ActionHandler() {
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

}
