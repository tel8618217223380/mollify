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

import org.sjarvela.mollify.client.filesystem.FolderInfo;
import org.sjarvela.mollify.client.filesystem.UserFolder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.Callback;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserFolderDialog extends CenteredDialog {
	public enum Mode {
		Add, Edit
	};

	private final TextProvider textProvider;
	private final UserFolderHandler handler;
	private final Mode mode;
	private final List<FolderInfo> availableDirectories;

	private UserFolder edited = null;
	private FolderInfo selected = null;

	private ListBox directories;
	private CheckBox useDefaultName;
	private TextBox name;
	private TextBox defaultName;

	public UserFolderDialog(TextProvider textProvider,
			UserFolderHandler handler, List<FolderInfo> availableDirectories) {
		super(textProvider.getStrings().userFolderDialogAddTitle(),
				StyleConstants.USER_FOLDER_DIALOG);
		this.availableDirectories = availableDirectories;
		this.mode = Mode.Add;

		this.textProvider = textProvider;
		this.handler = handler;
		this.edited = null;

		initialize();

		directories.addItem(textProvider.getStrings()
				.userFolderDialogSelectFolder(), null);
		for (FolderInfo dir : availableDirectories)
			directories.addItem(dir.getName(), dir.getId());

		useDefaultName.setValue(true);
		refreshNameField();
	}

	public UserFolderDialog(TextProvider textProvider,
			UserFolderHandler handler, UserFolder folder) {
		super(textProvider.getStrings().userFolderDialogEditTitle(),
				StyleConstants.USER_FOLDER_DIALOG);
		this.mode = Mode.Edit;
		this.availableDirectories = null;
		this.textProvider = textProvider;
		this.handler = handler;
		this.edited = folder;

		initialize();

		name.setText((folder.getName() == null ? "" : folder.getName()));
		useDefaultName.setValue(folder.getName() == null);
		defaultName.setText(folder.getDefaultName());
		refreshNameField();
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.USER_FOLDER_DIALOG_CONTENT);

		if (mode.equals(Mode.Add)) {
			Label directoriesTitle = new Label(textProvider.getStrings()
					.userFolderDialogDirectoriesTitle());
			directoriesTitle
					.setStyleName(StyleConstants.USER_FOLDER_DIALOG_FOLDERS_TITLE);
			panel.add(directoriesTitle);

			directories = new ListBox();
			directories.addStyleName(StyleConstants.USER_FOLDER_DIALOG_FOLDERS);
			directories.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					if (directories.getSelectedIndex() <= 0) {
						selected = null;
					} else {
						selected = availableDirectories.get(directories
								.getSelectedIndex() - 1);
					}
				}
			});
			panel.add(directories);
		} else {
			Label defaultNameTitle = new Label(textProvider.getStrings()
					.userFolderDialogDefaultNameTitle());
			defaultNameTitle
					.setStyleName(StyleConstants.USER_FOLDER_DIALOG_DEFAULT_NAME);
			panel.add(defaultNameTitle);

			defaultName = new TextBox();
			defaultName
					.addStyleName(StyleConstants.USER_FOLDER_DIALOG_DEFAULT_NAME_VALUE);
			defaultName.setReadOnly(true);
			panel.add(defaultName);
		}

		useDefaultName = new CheckBox(textProvider.getStrings()
				.userFolderDialogUseDefaultName());
		useDefaultName
				.addStyleName(StyleConstants.USER_FOLDER_DIALOG_USE_DEFAULT_NAME);
		useDefaultName.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				refreshNameField();
			}
		});
		panel.add(useDefaultName);

		Label nameTitle = new Label(textProvider.getStrings()
				.userFolderDialogName());
		nameTitle.setStyleName(StyleConstants.USER_FOLDER_DIALOG_NAME_TITLE);
		panel.add(nameTitle);

		name = new TextBox();
		name.setStylePrimaryName(StyleConstants.USER_FOLDER_DIALOG_NAME_VALUE);
		panel.add(name);

		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.USER_FOLDER_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		String title = mode.equals(Mode.Add) ? textProvider.getStrings()
				.userFolderDialogAddButton() : textProvider.getStrings()
				.userFolderDialogEditButton();

		buttons.add(createButton(title, new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (mode.equals(Mode.Add))
					onAddFolder();
				else
					onEditFolder();
			}
		}, StyleConstants.USER_FOLDER_DIALOG_BUTTON_ADD_EDIT));

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						UserFolderDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	protected void onAddFolder() {
		if (selected == null)
			return;
		if (!useDefaultName.getValue() && name.getText().length() == 0)
			return;

		handler.addUserFolder(selected, getEffectiveName(),
				createHideCallback());
	}

	protected void onEditFolder() {
		if (!useDefaultName.getValue() && name.getText().length() == 0)
			return;

		handler
				.editUserFolder(edited, getEffectiveName(),
						createHideCallback());
	}

	private String getEffectiveName() {
		return useDefaultName.getValue() ? null : name.getText();
	}

	private void refreshNameField() {
		Boolean readOnly = useDefaultName.getValue();
		name.setReadOnly(readOnly);
	}

	private Callback createHideCallback() {
		return new Callback() {
			public void onCallback() {
				UserFolderDialog.this.hide();
			}
		};
	}
}
