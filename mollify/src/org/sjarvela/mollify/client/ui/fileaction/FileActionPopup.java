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
import org.sjarvela.mollify.client.ui.DropdownPopup;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.UrlHandler;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileActionPopup extends DropdownPopup {
	private UrlHandler urlHandler;
	private FileActionProvider actionProvider;

	private Label label;
	private File file = File.Empty();

	public FileActionPopup(Localizator localizator, UrlHandler urlHandler,
			FileActionProvider actionProvider) {
		super(null, null);

		this.urlHandler = urlHandler;
		this.actionProvider = actionProvider;
		this.setStyleName("file-actions");

		VerticalPanel container = new VerticalPanel();
		container.setStyleName("file-actions-title");
		label = new Label();
		container.add(label);

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setStyleName("file-actions-buttons");
		buttons.add(createActionButton(localizator.getStrings()
				.fileActionDownloadTitle(), FileAction.DOWNLOAD));
		container.add(buttons);

		this.add(container);
	}

	public File getFile() {
		return file;
	}

	private Widget createActionButton(String title, final FileAction action) {
		Button button = new Button(title);
		button.setStyleName(StyleConstants.FILE_ACTION);
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
		if (action.equals(FileAction.DOWNLOAD)) {
			urlHandler.openDownloadUrl(actionProvider
					.getActionURL(file, action));
			this.hide();
		}
	}
}
