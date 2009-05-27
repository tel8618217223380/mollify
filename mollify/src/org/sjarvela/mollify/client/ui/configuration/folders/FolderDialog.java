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

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.filesystem.DirectoryInfo;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FolderDialog extends CenteredDialog {
	public enum Mode {
		Add, Edit
	};

	private final TextProvider textProvider;
	private final FolderHandler handler;
	private final Mode mode;
	private final DirectoryInfo folder;

	private TextBox name;
	private TextBox path;

	public FolderDialog(TextProvider textProvider, FolderHandler handler) {
		super(textProvider.getStrings().folderDialogAddTitle(),
				StyleConstants.FOLDER_DIALOG);
		this.mode = Mode.Add;
		this.textProvider = textProvider;
		this.handler = handler;
		this.folder = null;

		initialize();
	}

	public FolderDialog(TextProvider textProvider, FolderHandler handler,
			DirectoryInfo folder) {
		super(textProvider.getStrings().folderDialogEditTitle(),
				StyleConstants.FOLDER_DIALOG);
		this.folder = folder;
		this.mode = Mode.Edit;
		this.textProvider = textProvider;
		this.handler = handler;

		initialize();
		setFolderData();
	}

	private void setFolderData() {
		name.setText(folder.getName());
		path.setText(folder.getPath());
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.FOLDER_DIALOG_CONTENT);

		Label nameTitle = new Label(textProvider.getStrings()
				.folderDialogName());
		nameTitle.setStyleName(StyleConstants.FOLDER_DIALOG_NAME_TITLE);
		panel.add(nameTitle);

		name = new TextBox();
		name.addStyleName(StyleConstants.FOLDER_DIALOG_NAME_VALUE);
		panel.add(name);

		Label pathTitle = new Label(textProvider.getStrings()
				.folderDialogPath());
		pathTitle.setStyleName(StyleConstants.FOLDER_DIALOG_PATH_TITLE);
		panel.add(pathTitle);

		path = new TextBox();
		path.addStyleName(StyleConstants.FOLDER_DIALOG_PATH_VALUE);
		panel.add(path);

		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.FOLDER_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		String title = mode.equals(Mode.Add) ? textProvider.getStrings()
				.folderDialogAddButton() : textProvider.getStrings()
				.folderDialogEditButton();

		buttons.add(createButton(title, new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (mode.equals(Mode.Add))
					onAddFolder();
				else
					onEditFolder();
			}
		}, StyleConstants.FOLDER_DIALOG_BUTTON_ADD_EDIT));

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						FolderDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	protected void onAddFolder() {
		if (name.getText().length() == 0)
			return;
		if (path.getText().length() == 0)
			return;

		handler.addFolder(name.getText(), path.getText(), createHideCallback());
	}

	protected void onEditFolder() {
		if (name.getText().length() == 0)
			return;
		if (path.getText().length() == 0)
			return;

		handler.editFolder(folder, name.getText(), path.getText(),
				createHideCallback());
	}

	private Callback createHideCallback() {
		return new Callback() {
			public void onCallback() {
				FolderDialog.this.hide();
			}
		};
	}
}
