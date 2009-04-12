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

import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.ui.ActionDelegator;
import org.sjarvela.mollify.client.ui.ActionHandler;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.Sort;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationView;
import org.sjarvela.mollify.client.ui.dialog.configuration.Configurator;
import org.sjarvela.mollify.client.ui.dialog.configuration.ConfigurationDialog.ConfigurationType;

public class ConfigurationFoldersGlue implements Configurator {

	private final ConfigurationFoldersView view;

	public ConfigurationFoldersGlue(
			ConfigurationFoldersView view,
			final ConfigurationFoldersPresenter presenter,
			ActionDelegator actionDelegator) {

		this.view = view;

		view.list().addListener(new GridListener<DirectoryInfo>() {
			public void onColumnClicked(DirectoryInfo t, GridColumn column) {
			}

			public void onColumnSorted(GridColumn column, Sort sort) {
			}

			public void onIconClicked(DirectoryInfo t) {
			}

			public void onSelectionChanged(List<DirectoryInfo> selected) {
				updateButtons(selected.size() == 1);
			}
		});

		actionDelegator.setActionHandler(
				ConfigurationFoldersView.Actions.addFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onAddFolder();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationFoldersView.Actions.editFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onEditFolder();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationFoldersView.Actions.removeFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onRemoveFolder();
					}
				});

		updateButtons(false);
	}

	protected void updateButtons(boolean selected) {
		view.editFolderButton().setEnabled(selected);
		view.removeFolderButton().setEnabled(selected);
	}

	public ConfigurationView getView() {
		return view;
	}

	public void onDataChanged(ConfigurationType type) {
	}

}
