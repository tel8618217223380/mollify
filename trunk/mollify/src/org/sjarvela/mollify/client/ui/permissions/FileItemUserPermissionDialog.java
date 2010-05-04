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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileItemUserPermissionHandler;
import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.session.user.UserBase;
import org.sjarvela.mollify.client.ui.Formatter;
import org.sjarvela.mollify.client.ui.ListBox;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileItemUserPermissionDialog extends CenteredDialog {
	public enum Mode {
		Add, Edit
	};

	private final TextProvider textProvider;
	private final Mode mode;
	private final FileItemUserPermissionHandler handler;

	// in Add mode
	private final List<? extends UserBase> availableUsersOrGroups;
	// in Edit mode
	private final FileItemUserPermission original;

	private ListBox<UserBase> user;
	private TextBox userLabel;
	private ListBox<FilePermission> permission;

	public FileItemUserPermissionDialog(TextProvider textProvider,
			FileItemUserPermissionHandler handler,
			List<? extends UserBase> availableUsersOrGroups, boolean groups) {
		super(groups ? textProvider.getStrings()
				.fileItemUserPermissionDialogAddGroupTitle() : textProvider
				.getStrings().fileItemUserPermissionDialogAddTitle(),
				StyleConstants.FILEITEM_USER_PERMISSION_DIALOG);
		this.mode = Mode.Add;

		this.availableUsersOrGroups = availableUsersOrGroups;
		this.textProvider = textProvider;
		this.handler = handler;
		this.original = null;

		createControls();
		initialize();
	}

	public FileItemUserPermissionDialog(TextProvider textProvider,
			FileItemUserPermissionHandler handler,
			FileItemUserPermission fileItemUserPermission, boolean group) {
		super(group ? textProvider.getStrings()
				.fileItemUserPermissionDialogEditGroupTitle() : textProvider
				.getStrings().fileItemUserPermissionDialogEditTitle(),
				StyleConstants.FILEITEM_USER_PERMISSION_DIALOG);
		this.mode = Mode.Edit;

		this.textProvider = textProvider;
		this.handler = handler;
		this.original = fileItemUserPermission;
		this.availableUsersOrGroups = Collections.EMPTY_LIST;

		createControls();
		initialize();
	}

	private void createControls() {
		permission = new ListBox();
		permission
				.addStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_PERMISSION);
		permission.setFormatter(new Formatter<FilePermission>() {
			public String format(FilePermission mode) {
				return mode.getLocalizedText(textProvider);
			}
		});

		if (Mode.Add.equals(this.mode)) {
			user = new ListBox();
			user
					.addStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_USER);
			user.setFormatter(new Formatter<UserBase>() {
				public String format(UserBase user) {
					return user.getName();
				}
			});
		} else {
			userLabel = new TextBox();
			userLabel.setReadOnly(true);
			userLabel
					.setStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_USER_LABEL);
		}
	}

	@Override
	protected void initialize() {
		super.initialize();

		permission.setContent(Arrays.asList(FilePermission.None,
				FilePermission.ReadOnly, FilePermission.ReadWrite));

		if (Mode.Add.equals(this.mode)) {
			List<UserBase> list = new ArrayList(availableUsersOrGroups);
			user.setContent(list);
		} else {
			userLabel.setText(original.getUserOrGroup().getName());
			permission.setSelectedItem(original.getPermission());
		}
	}

	@Override
	protected Widget createContent() {
		Panel panel = new VerticalPanel();
		panel
				.addStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_CONTENT);

		Label userTitle = new Label(textProvider.getStrings()
				.fileItemUserPermissionDialogName());
		userTitle
				.setStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_USER_TITLE);
		panel.add(userTitle);

		if (Mode.Add.equals(this.mode)) {
			panel.add(user);
		} else {
			panel.add(userLabel);
		}

		Label permissionTitle = new Label(textProvider.getStrings()
				.fileItemUserPermissionDialogPermission());
		permissionTitle
				.setStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_PERMISSION_TITLE);
		panel.add(permissionTitle);
		panel.add(permission);

		return panel;
	}

	@Override
	protected Widget createButtons() {
		Panel buttons = new HorizontalPanel();
		buttons
				.addStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_BUTTONS);

		String title = Mode.Add.equals(mode) ? textProvider.getStrings()
				.fileItemUserPermissionDialogAddButton() : textProvider
				.getStrings().fileItemUserPermissionDialogEditButton();

		buttons.add(createButton(title, new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (Mode.Add.equals(mode))
					onAddPermission();
				else
					onEditPermission();
			}
		}, StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_BUTTON_ADD_EDIT));

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						FileItemUserPermissionDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	protected void onAddPermission() {
		UserBase userOrGroup = this.user.getSelectedItem();
		if (userOrGroup == null)
			return;

		handler.addFileItemUserPermission(userOrGroup, permission
				.getSelectedItem());
		this.hide();
	}

	protected void onEditPermission() {
		FilePermission permission = this.permission.getSelectedItem();

		handler.editFileItemUserPermission(new FileItemUserPermission(original
				.getFileSystemItem(), original.getUserOrGroup(), permission));
		this.hide();
	}
}
