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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.file.FileItemUserPermission;
import org.sjarvela.mollify.client.session.file.FileItemUserPermissionHandler;
import org.sjarvela.mollify.client.session.file.FilePermissionMode;
import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.ui.Formatter;
import org.sjarvela.mollify.client.ui.ListBox;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileItemUserPermissionDialog extends CenteredDialog {
	public enum Mode {
		Add, Edit
	};

	private final TextProvider textProvider;
	private final Mode mode;
	private final FileItemUserPermissionHandler handler;

	private final List<User> availableUsers; // in Add mode
	private final FileItemUserPermission original; // in Edit mode

	private ListBox<User> user;
	private Label userLabel;
	private ListBox<FilePermissionMode> permission;

	public FileItemUserPermissionDialog(TextProvider textProvider,
			FileItemUserPermissionHandler handler, List<User> availableUsers) {
		super(textProvider.getStrings().fileItemUserPermissionDialogAddTitle(),
				StyleConstants.FILEITEM_USER_PERMISSION_DIALOG);
		this.mode = Mode.Add;

		this.availableUsers = availableUsers;
		this.textProvider = textProvider;
		this.handler = handler;
		this.original = null;

		createControls();
		initialize();
	}

	public FileItemUserPermissionDialog(TextProvider textProvider,
			FileItemUserPermissionHandler handler,
			FileItemUserPermission fileItemUserPermission) {
		super(
				textProvider.getStrings()
						.fileItemUserPermissionDialogEditTitle(),
				StyleConstants.FILEITEM_USER_PERMISSION_DIALOG);
		this.mode = Mode.Edit;

		this.textProvider = textProvider;
		this.handler = handler;
		this.original = fileItemUserPermission;
		this.availableUsers = Collections.EMPTY_LIST;

		createControls();
		initialize();
	}

	private void createControls() {
		permission = new ListBox();
		permission
				.addStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_PERMISSION);
		permission.setFormatter(new Formatter<FilePermissionMode>() {
			public String format(FilePermissionMode mode) {
				return mode.getLocalizedText(textProvider);
			}
		});

		if (Mode.Add.equals(this.mode)) {
			user = new ListBox();
			user
					.addStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_USER);
			user.setFormatter(new Formatter<User>() {
				public String format(User user) {
					return user.getName();
				}
			});
		} else {
			userLabel = new Label();
			userLabel
					.setStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_USER_LABEL);
		}
	}

	@Override
	protected void initialize() {
		super.initialize();

		permission.setContent(Arrays.asList(FilePermissionMode.values()));

		if (Mode.Add.equals(this.mode)) {
			user.setContent(availableUsers);
		} else {
			userLabel.setText(original.getUser().getName());
			permission.setSelectedItem(original.getPermission());
		}
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel
				.addStyleName(StyleConstants.FILEITEM_USER_PERMISSION_DIALOG_CONTENT);

		Label userTitle = new Label(textProvider.getStrings()
				.fileItemUserPermissionDialogUser());
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
		User user = this.user.getSelectedItem();
		if (user == null)
			return;

		handler.addFileItemUserPermission(user, permission.getSelectedItem());
		this.hide();
	}

	protected void onEditPermission() {
		FilePermissionMode permission = this.permission.getSelectedItem();
		if (FilePermissionMode.None.equals(permission))
			return;

		handler.editFileItemUserPermission(new FileItemUserPermission(original
				.getFileSystemItem(), original.getUser(), permission));
		this.hide();
	}
}
