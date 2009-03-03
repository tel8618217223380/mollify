/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.popup.directorycontext;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.provider.DirectoryDetailsProvider;
import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.SessionSettings;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.popup.ContextPopup;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryContextPopup extends ContextPopup {
	private final DefaultTextProvider localizator;
	private final SessionSettings settings;
	private final DirectoryDetailsProvider detailsProvider;
	private FileSystemActionHandler actionHandler;

	private Label name;
	private Button downloadButton;
	private Button renameButton;
	private Button deleteButton;

	private Directory directory;

	public DirectoryContextPopup(DefaultTextProvider localizator,
			DirectoryDetailsProvider detailsProvider, SessionSettings settings) {
		super(StyleConstants.DIR_CONTEXT);

		this.localizator = localizator;
		this.detailsProvider = detailsProvider;
		this.settings = settings;

		initialize();
	}

	public void setDirectoryActionHandler(FileSystemActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	protected Widget createContent() {
		VerticalPanel content = new VerticalPanel();
		content.setStyleName(StyleConstants.DIR_CONTEXT_CONTENT);

		name = new Label();
		name.setStyleName(StyleConstants.DIR_CONTEXT_NAME);

		content.add(name);
		content.add(createButtons());
		return content;
	}

	private Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setStyleName(StyleConstants.DIR_CONTEXT_BUTTONS);

		if (settings.isZipDownloadEnabled())
			downloadButton = createActionButton(localizator.getStrings()
					.dirActionDownloadTitle(), FileSystemAction.download_as_zip);
		renameButton = createActionButton(localizator.getStrings()
				.dirActionRenameTitle(), FileSystemAction.rename);
		renameButton.setVisible(false);
		deleteButton = createActionButton(localizator.getStrings()
				.dirActionDeleteTitle(), FileSystemAction.delete);
		deleteButton.setVisible(false);

		if (settings.isZipDownloadEnabled())
			buttons.add(downloadButton);
		buttons.add(renameButton);
		buttons.add(deleteButton);

		return buttons;
	}

	protected void onAction(FileSystemAction action) {
		actionHandler.onAction(directory, action);
		this.hide();
	}

	public void update(Directory directory, Element element) {
		setParent(element);
		this.directory = directory;
		name.setText(directory.getName());

		detailsProvider.getDirectoryDetails(directory,
				new ResultListener<DirectoryDetails>() {
					public void onFail(ServiceError error) {
						name.setText(error.getType().getMessage(localizator));
					}

					public void onSuccess(DirectoryDetails result) {
						updateDetails(result);
					}
				});
	}

	protected void updateDetails(DirectoryDetails details) {
		boolean writable = settings.isFolderActionsEnabled()
				&& details.getFilePermission().canWrite();
		renameButton.setVisible(writable);
		deleteButton.setVisible(writable);
	}

}
