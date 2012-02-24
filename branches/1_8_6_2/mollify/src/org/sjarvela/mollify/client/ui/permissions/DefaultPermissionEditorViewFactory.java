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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileItemUserPermissionHandler;
import org.sjarvela.mollify.client.session.user.UserBase;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.itemselector.ItemSelectorFactory;

import com.google.inject.Inject;

public class DefaultPermissionEditorViewFactory implements
		PermissionEditorViewFactory {
	private final TextProvider textProvider;
	private final DialogManager dialogManager;
	private final ServiceEnvironment env;
	private final ItemSelectorFactory itemSelectorFactory;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final SessionProvider sessionProvider;

	@Inject
	public DefaultPermissionEditorViewFactory(TextProvider textProvider,
			ServiceEnvironment env,
			FileSystemItemProvider fileSystemItemProvider,
			ItemSelectorFactory itemSelectorFactory,
			DialogManager dialogManager, SessionProvider sessionProvider) {
		this.textProvider = textProvider;
		this.env = env;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.itemSelectorFactory = itemSelectorFactory;
		this.dialogManager = dialogManager;
		this.sessionProvider = sessionProvider;
	}

	public void openPermissionEditor(FileSystemItem item) {
		ActionDelegator actionDelegator = new ActionDelegator();
		PermissionEditorModel model = new PermissionEditorModel(item, env
				.getConfigurationService(), env.getFileSystemService());
		PermissionEditorView view = new PermissionEditorView(textProvider,
				actionDelegator, item != null ? PermissionEditorView.Mode.Fixed
						: PermissionEditorView.Mode.ItemSelectable,
				sessionProvider.getSession().getFeatures().userGroups());
		PermissionEditorPresenter presenter = new PermissionEditorPresenter(
				textProvider, model, view, dialogManager, this,
				itemSelectorFactory, new FilePermissionModeFormatter(
						textProvider), fileSystemItemProvider);
		new PermissionEditorGlue(presenter, view, actionDelegator);
	}

	public void openAddFileItemUserPermissionDialog(
			FileItemUserPermissionHandler fileItemUserPermissionHandler,
			List<? extends UserBase> availableUsersOrGroups, boolean groups) {
		new FileItemUserPermissionDialog(textProvider,
				fileItemUserPermissionHandler, availableUsersOrGroups, groups);
	}

	public void openEditFileItemUserPermissionDialog(
			FileItemUserPermissionHandler fileItemUserPermissionHandler,
			FileItemUserPermission fileItemUserPermission, boolean groups) {
		new FileItemUserPermissionDialog(textProvider,
				fileItemUserPermissionHandler, fileItemUserPermission, groups);
	}

}
