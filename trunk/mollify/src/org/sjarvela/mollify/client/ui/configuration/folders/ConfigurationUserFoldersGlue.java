/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration.folders;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.UserDirectory;
import org.sjarvela.mollify.client.ui.ActionDelegator;
import org.sjarvela.mollify.client.ui.ActionHandler;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.Sort;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationView;
import org.sjarvela.mollify.client.ui.configuration.Configurator;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationDialog.ConfigurationType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class ConfigurationUserFoldersGlue implements Configurator {

	private final ConfigurationUserFoldersView view;
	private final ConfigurationUserFoldersPresenter presenter;

	public ConfigurationUserFoldersGlue(
			ConfigurationUserFoldersView view,
			final ConfigurationUserFoldersPresenter presenter,
			ActionDelegator actionDelegator) {
		this.view = view;
		this.presenter = presenter;

		view.directories().addListener(new GridListener<UserDirectory>() {
			public void onColumnClicked(UserDirectory t, GridColumn column) {
			}

			public void onColumnSorted(GridColumn column, Sort sort) {
			}

			public void onIconClicked(UserDirectory t) {
			}

			public void onSelectionChanged(List<UserDirectory> selected) {
				updateButtons();
			}
		});

		view.user().addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateButtons();
				presenter.onUserChanged();
			}
		});

		actionDelegator.setActionHandler(
				ConfigurationUserFoldersView.Actions.addUserFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onAddUserFolder();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationUserFoldersView.Actions.editUserFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onEditUserFolder();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationUserFoldersView.Actions.removeUserFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onRemoveUserFolder();
					}
				});

		updateButtons();
	}

	protected void updateButtons() {
		boolean userSelected = view.user().getSelectedIndex() >= 1;
		boolean directorySelected = (view.directories().getSelected().size() == 1);

		view.addButton().setEnabled(userSelected);
		view.editButton().setEnabled(userSelected && directorySelected);
		view.removeButton().setEnabled(userSelected && directorySelected);
	}

	public ConfigurationView getView() {
		return view;
	}

	public void onDataChanged(ConfigurationType type) {
		if (type.equals(ConfigurationType.Users)) {
			presenter.reloadUsers();
		} else if (type.equals(ConfigurationType.Folders)) {
			presenter.reloadFolders();
		}
	}
}
