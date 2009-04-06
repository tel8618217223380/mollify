/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog.configuration.users;

import java.util.List;

import org.sjarvela.mollify.client.session.User;
import org.sjarvela.mollify.client.ui.ActionDelegator;
import org.sjarvela.mollify.client.ui.ActionHandler;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.Sort;

public class ConfigurationSettingsUsersGlue {

	public ConfigurationSettingsUsersGlue(ConfigurationSettingsUsersView view,
			final ConfigurationSettingsUsersPresenter presenter,
			ActionDelegator actionDelegator) {
		view.list().addListener(new GridListener<User>() {
			public void onColumnClicked(User t, GridColumn column) {
			}

			public void onColumnSorted(GridColumn column, Sort sort) {
			}

			public void onIconClicked(User t) {
			}

			public void onSelectionChanged(List<User> selected) {
			}
		});

		actionDelegator.setActionHandler(
				ConfigurationSettingsUsersView.Actions.addUser,
				new ActionHandler() {
					public void onAction() {
						presenter.onAddUser();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationSettingsUsersView.Actions.removeUser,
				new ActionHandler() {
					public void onAction() {
						presenter.onRemoveUser();
					}
				});
	}

}
