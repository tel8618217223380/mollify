/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog.configuration.folders;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.UserDirectory;
import org.sjarvela.mollify.client.ui.ActionDelegator;
import org.sjarvela.mollify.client.ui.ActionHandler;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.Sort;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class ConfigurationSettingsUserFoldersGlue {

	private final ConfigurationSettingsUserFoldersView view;

	public ConfigurationSettingsUserFoldersGlue(
			ConfigurationSettingsUserFoldersView view,
			final ConfigurationSettingsUserFoldersPresenter presenter,
			ActionDelegator actionDelegator) {
		this.view = view;

		view.directories().addListener(new GridListener<UserDirectory>() {
			public void onColumnClicked(UserDirectory t, GridColumn column) {
			}

			public void onColumnSorted(GridColumn column, Sort sort) {
			}

			public void onIconClicked(UserDirectory t) {
			}

			public void onSelectionChanged(List<UserDirectory> selected) {
				updateButtons(selected.size() == 1);
			}
		});

		view.user().addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				presenter.onUserChanged();
			}
		});

		actionDelegator.setActionHandler(
				ConfigurationSettingsUserFoldersView.Actions.addUserFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onAddUserFolder();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationSettingsUserFoldersView.Actions.editUserFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onEditUserFolder();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationSettingsUserFoldersView.Actions.removeUserFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onRemoveUserFolder();
					}
				});

		updateButtons(false);
	}

	protected void updateButtons(boolean selected) {
		view.editButton().setEnabled(selected);
		view.removeButton().setEnabled(selected);
	}
}
