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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.configuration.ConfigurationView;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationUserFoldersView extends ConfigurationView {
	private final UserDirectoryList directoryList;
	private final ListBox user;
	private final ActionButton addButton;
	private final ActionButton editButton;
	private final ActionButton removeButton;

	public enum Actions implements ResourceId {
		addUserFolder, editUserFolder, removeUserFolder
	}

	public ConfigurationUserFoldersView(TextProvider textProvider,
			ActionListener actionListener) {
		super(textProvider,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS);

		user = new ListBox();
		user
				.addStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_USERS);

		directoryList = new UserDirectoryList(textProvider,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_LIST);

		addButton = new ActionButton(
				textProvider.getStrings()
						.configurationDialogSettingUserFoldersAdd(),
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_ACTION_ADD,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_ACTION);
		addButton.setAction(actionListener, Actions.addUserFolder);

		editButton = new ActionButton(
				textProvider.getStrings()
						.configurationDialogSettingUserFoldersEdit(),
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_ACTION_EDIT,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_ACTION);
		editButton.setAction(actionListener, Actions.editUserFolder);

		removeButton = new ActionButton(
				textProvider.getStrings()
						.configurationDialogSettingUserFoldersRemove(),
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_ACTION_REMOVE,
				StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_ACTION);
		removeButton.setAction(actionListener, Actions.removeUserFolder);

		add(createUserSelection());
		add(createList());
		add(createButtons());
	}

	private Widget createUserSelection() {
		Panel panel = new FlowPanel();
		panel
				.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_USER_PANEL);
		panel.add(user);
		return panel;
	}

	private Widget createList() {
		Panel panel = new FlowPanel();
		panel
				.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_LIST_PANEL);
		panel.add(directoryList);
		return panel;
	}

	private Widget createButtons() {
		Panel actions = new FlowPanel();
		actions
				.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_USER_FOLDERS_ACTIONS);
		actions.add(addButton);
		actions.add(editButton);
		actions.add(removeButton);
		return actions;
	}

	@Override
	public String getTitle() {
		return textProvider.getStrings()
				.configurationDialogSettingUserFoldersViewTitle();
	}

	public ListBox user() {
		return user;
	}

	public UserDirectoryList directories() {
		return directoryList;
	}

	public ActionButton addButton() {
		return addButton;
	}

	public ActionButton editButton() {
		return editButton;
	}

	public ActionButton removeButton() {
		return removeButton;
	}
}
