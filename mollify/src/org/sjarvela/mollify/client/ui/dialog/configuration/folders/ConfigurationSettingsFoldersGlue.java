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

public class ConfigurationSettingsFoldersGlue {

	public ConfigurationSettingsFoldersGlue(
			ConfigurationSettingsFoldersView view,
			final ConfigurationSettingsFoldersPresenter presenter,
			ActionDelegator actionDelegator) {

		view.list().addListener(new GridListener<DirectoryInfo>() {
			public void onColumnClicked(DirectoryInfo t, GridColumn column) {
			}

			public void onColumnSorted(GridColumn column, Sort sort) {
			}

			public void onIconClicked(DirectoryInfo t) {
			}

			public void onSelectionChanged(List<DirectoryInfo> selected) {
			}
		});

		actionDelegator.setActionHandler(
				ConfigurationSettingsFoldersView.Actions.addFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onAddFolder();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationSettingsFoldersView.Actions.removeFolder,
				new ActionHandler() {
					public void onAction() {
						presenter.onRemoveFolder();
					}
				});
	}

}
