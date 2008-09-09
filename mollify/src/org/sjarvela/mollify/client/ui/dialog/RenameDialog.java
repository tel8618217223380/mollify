/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.RenameHandler;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RenameDialog extends CenteredDialog {
	private File file;
	private Localizator localizator;
	private TextBox name;
	private RenameHandler listener;

	public RenameDialog(File file, Localizator localizator,
			RenameHandler listener) {
		super(localizator.getStrings().renameFileDialogTitle(),
				StyleConstants.RENAME_FILE_DIALOG);

		this.file = file;
		this.localizator = localizator;
		this.listener = listener;

		initialize();
	}

	@Override
	Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.RENAME_FILE_DIALOG_CONTENT);

		Label originalNameTitle = new Label(localizator.getStrings()
				.renameFileDialogOriginalName());
		originalNameTitle
				.setStyleName(StyleConstants.RENAME_FILE_ORIGINAL_NAME_TITLE);
		panel.add(originalNameTitle);

		Label originalName = new Label(file.getName());
		originalName
				.setStyleName(StyleConstants.RENAME_FILE_ORIGINAL_NAME_VALUE);
		panel.add(originalName);

		Label newNameTitle = new Label(localizator.getStrings()
				.renameFileDialogNewName());
		newNameTitle.setStyleName(StyleConstants.RENAME_FILE_NEW_NAME_TITLE);
		panel.add(newNameTitle);

		name = new TextBox();
		name.addStyleName(StyleConstants.RENAME_FILE_NEW_NAME_VALUE);
		name.setText(file.getName());

		panel.add(name);

		return panel;
	}

	@Override
	Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.RENAME_FILE_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		buttons.add(createButton(localizator.getStrings()
				.renameFileDialogRenameButton(), new ClickListener() {

			public void onClick(Widget sender) {
				onRename();
			}
		}, StyleConstants.RENAME_FILE_DIALOG_BUTTON_RENAME));

		buttons.add(createButton(localizator.getStrings()
				.renameFileDialogCancelButton(), new ClickListener() {

			public void onClick(Widget sender) {
				RenameDialog.this.hide();
			}
		}, StyleConstants.RENAME_FILE_DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	@Override
	void onShow() {
		hilightFilename();
	}

	private void hilightFilename() {
		if (file.getExtension().length() > 0)
			name.setSelectionRange(0, file.getName().length()
					- (file.getExtension().length() + 1));
		name.setFocus(true);
	}

	private void onRename() {
		String newName = name.getText();

		if (newName.length() < 1) {
			name.setFocus(true);
			return;
		}

		if (newName.equals(file.getName())) {
			hilightFilename();
			return;
		}

		this.hide();
		listener.onRename(file, newName);
	}
}
