/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.directorycontext;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.file.FileAction;
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

public class DirectoryContextPopup extends DropdownPopup {

	private final Localizator localizator;
	private Label name;
	private Button renameButton;
	private Button deleteButton;

	public DirectoryContextPopup(Localizator localizator) {
		super(null, null);
		this.localizator = localizator;

		this.setStyleName(StyleConstants.DIR_CONTEXT);
		BorderedControl content = new BorderedControl(
				StyleConstants.DIR_CONTEXT_BORDER);

		content.setContent(createContent());
		// extra content, tip pointing the file (just a div with a certain
		// style)
		content.setWidget(0, 1, createPointer());

		addItem(content);
	}

	private Widget createPointer() {
		Label pointer = new Label();
		pointer.setStyleName(StyleConstants.DIR_CONTEXT_POINTER);
		return pointer;
	}

	private VerticalPanel createContent() {
		VerticalPanel content = new VerticalPanel();
		content.setStyleName(StyleConstants.DIR_CONTEXT_CONTENT);

		name = new Label();
		content.add(name);
		content.add(createButtons());
		return content;
	}

	private Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setStyleName(StyleConstants.DIR_CONTEXT_BUTTONS);

		renameButton = createActionButton(localizator.getStrings()
				.dirActionRenameTitle(), FileAction.RENAME);
		renameButton.setVisible(false);
		deleteButton = createActionButton(localizator.getStrings()
				.dirActionDeleteTitle(), FileAction.DELETE);
		deleteButton.setVisible(false);

		buttons.add(renameButton);
		buttons.add(deleteButton);

		return buttons;
	}

	private Button createActionButton(String title, final FileAction action) {
		Button button = new Button(title);
		button.addStyleName(StyleConstants.DIR_CONTEXT_ACTION);
		button.getElement().setId(
				StyleConstants.DIR_CONTEXT_ACTION + "-"
						+ action.name().toLowerCase());
		button.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				onAction(action);
			}
		});
		return button;
	}

	protected void onAction(FileAction action) {
		// TODO Auto-generated method stub
	}

	public void initialize(Directory directory, Element element) {
		name.setText(directory.getName());
		super.setParentElement(element);
		super.setOpenerElement(element);
	}

}
