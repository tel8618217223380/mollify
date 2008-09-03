/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui;

import org.sjarvela.mollify.client.RenameHandler;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.localization.Localizator;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RenameDialog extends DialogBox {
	private File file;
	private Localizator localizator;
	private TextBox name;
	private RenameHandler listener;

	public RenameDialog(File file, Localizator localizator,
			RenameHandler listener) {
		super(false, true);

		this.file = file;
		this.localizator = localizator;
		this.listener = listener;

		this.addStyleName(StyleConstants.RENAME_FILE_DIALOG);
		this.setText(localizator.getMessages().renameFileDialogTitle(
				file.getName()));

		VerticalPanel content = new VerticalPanel();
		content.add(createContent());
		content.add(createButtons());

		this.add(content);

		this.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});
	}

	private Widget createContent() {
		VerticalPanel panel = new VerticalPanel();

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

	private Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();

		buttons.add(createButton(localizator.getStrings()
				.renameFileDialogRenameButton(),
				StyleConstants.RENAME_FILE_DIALOG_BUTTON_RENAME,
				new ClickListener() {

					public void onClick(Widget sender) {
						onRename();
					}
				}));

		buttons.add(createButton(localizator.getStrings()
				.renameFileDialogCancelButton(),
				StyleConstants.RENAME_FILE_DIALOG_BUTTON_CANCEL,
				new ClickListener() {

					public void onClick(Widget sender) {
						RenameDialog.this.hide();
					}
				}));

		return buttons;
	}

	private Widget createButton(String title, String style,
			ClickListener listener) {
		Button button = new Button(title);
		button.addStyleName(StyleConstants.RENAME_FILE_DIALOG_BUTTON);
		button.addStyleName(style);
		button.addClickListener(listener);
		return button;
	}

	@Override
	public void show() {
		super.show();

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

		// TODO create messages
		if (newName.length() < 1) {
			return;
		}

		if (newName.equals(file.getName())) {
			return;
		}

		listener.onRename(file, newName);
	}
}
