/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.filesystem.handler.FolderHandler;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CreateFolderDialog extends CenteredDialog {
	private final Folder parentFolder;
	private final TextProvider textProvider;
	private final FolderHandler handler;
	private TextBox name;

	public CreateFolderDialog(Folder parentFolder, TextProvider textProvider,
			FolderHandler handler) {
		super(textProvider.getStrings().createFolderDialogTitle(),
				StyleConstants.CREATE_FOLDER_DIALOG);

		this.parentFolder = parentFolder;
		this.textProvider = textProvider;
		this.handler = handler;

		this.addViewListener(new ViewListener() {
			public void onShow() {
				focusName();
			}

		});

		initialize();
	}

	private void focusName() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				name.setFocus(true);
			}
		});
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.CREATE_FOLDER_DIALOG_CONTENT);

		Label nameTitle = new Label(textProvider.getStrings()
				.createFolderDialogName());
		nameTitle.setStyleName(StyleConstants.CREATE_FOLDER_DIALOG_NAME_TITLE);
		panel.add(nameTitle);

		name = new TextBox();
		name.addStyleName(StyleConstants.CREATE_FOLDER_DIALOG_NAME_VALUE);

		panel.add(name);

		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.CREATE_FOLDER_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		buttons.add(createButton(textProvider.getStrings()
				.createFolderDialogCreateButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				onCreate();
			}
		}, StyleConstants.CREATE_FOLDER_DIALOG_BUTTON_CREATE));

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						CreateFolderDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	private void onCreate() {
		String folderName = name.getText();

		if (folderName.length() < 1) {
			focusName();
			return;
		}

		this.hide();
		handler.createFolder(parentFolder, folderName);
	}
}
