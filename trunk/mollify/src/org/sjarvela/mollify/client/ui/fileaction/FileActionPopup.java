/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileaction;

import org.sjarvela.mollify.client.FileAction;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.ui.BorderedControl;
import org.sjarvela.mollify.client.ui.DropdownPopup;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileActionPopup extends DropdownPopup {
	private FileActionProvider actionProvider;

	private Label label;
	private File file = File.Empty();

	public FileActionPopup(Localizator localizator,
			FileActionProvider actionProvider) {
		super(null, null);

		this.actionProvider = actionProvider;
		this.setStyleName(StyleConstants.FILE_ACTIONS);

		BorderedControl content = new BorderedControl(
				StyleConstants.FILE_ACTIONS_BORDER);
		content.setContent(createContent(localizator));

		Label pointer = new Label();
		pointer.setStyleName(StyleConstants.FILE_ACTIONS_POINTER);
		content.setWidget(0, 1, pointer);

		this.add(content);
	}

	private VerticalPanel createContent(Localizator localizator) {
		VerticalPanel content = new VerticalPanel();
		content.setStyleName(StyleConstants.FILE_ACTIONS_CONTENT);

		label = new Label();
		content.add(label);

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setStyleName(StyleConstants.FILE_ACTIONS_BUTTONS);

		buttons.add(createActionButton(localizator.getStrings()
				.fileActionDownloadTitle(), FileAction.DOWNLOAD));
		buttons.add(createActionButton(localizator.getStrings()
				.fileActionRenameTitle(), FileAction.RENAME));
		buttons.add(createActionButton(localizator.getStrings()
				.fileActionDeleteTitle(), FileAction.DELETE));

		content.add(buttons);
		return content;
	}

	public File getFile() {
		return file;
	}

	private Widget createActionButton(String title, final FileAction action) {
		Button button = new Button(title);
		button.addStyleName(StyleConstants.FILE_ACTION);
		button.addStyleName(StyleConstants.FILE_ACTION_PREFIX
				+ action.name().toLowerCase());
		button.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				onAction(action);
			}
		});
		return button;
	}

	public void initialize(File file, Element parent) {
		this.file = file;
		super.setParentElement(parent);
		super.setOpenerElement(parent);

		label.setText(file.getName());
	}

	private void onAction(FileAction action) {
		actionProvider.onFileAction(file, action);
		this.hide();
	}
}
