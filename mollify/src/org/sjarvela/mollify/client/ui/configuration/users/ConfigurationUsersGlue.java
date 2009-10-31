/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration.users;

import java.util.List;

import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.action.VoidActionHandler;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.Sort;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationView;
import org.sjarvela.mollify.client.ui.configuration.Configurator;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationDialog.ConfigurationType;

public class ConfigurationUsersGlue implements Configurator {

	private final ConfigurationUsersView view;

	public ConfigurationUsersGlue(ConfigurationUsersView view,
			final ConfigurationUsersPresenter presenter,
			ActionDelegator actionDelegator) {
		this.view = view;

		view.list().addListener(new GridListener<User>() {
			public void onColumnClicked(User t, GridColumn column) {
			}

			public void onColumnSorted(GridColumn column, Sort sort) {
			}

			public void onIconClicked(User t) {
			}

			public void onSelectionChanged(List<User> selected) {
				updateButtons(selected.size() == 1);
			}
		});

		actionDelegator.setActionHandler(
				ConfigurationUsersView.Actions.addUser,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onAddUser();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationUsersView.Actions.editUser,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onEditUser();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationUsersView.Actions.removeUser,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onRemoveUser();
					}
				});

		actionDelegator.setActionHandler(
				ConfigurationUsersView.Actions.resetPassword,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onResetPassword();
					}
				});

		updateButtons(false);
	}

	protected void updateButtons(boolean selected) {
		view.editUserButton().setEnabled(selected);
		view.removeUserButton().setEnabled(selected);
		view.resetPasswordButton().setEnabled(selected);
	}

	public ConfigurationView getView() {
		return view;
	}

	public void onDataChanged(ConfigurationType type) {
	}

}
