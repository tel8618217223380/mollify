/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.popup.filecontext;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.provider.FileDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.ActionId;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.MultiActionButton;
import org.sjarvela.mollify.client.ui.popup.ContextPopup;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileContextPopup extends ContextPopup implements ActionListener {
	private DateTimeFormat dateTimeFormat;

	private final TextProvider textProvider;
	private final FileDetailsProvider detailsProvider;
	private final SessionInfo sessionInfo;
	private FileSystemActionHandler fileActionHandler;

	private Label filename;
	private File file = File.Empty;
	private Label description;
	private List<Label> detailRowValues = new ArrayList<Label>();
	private DisclosurePanel details;

	private Button renameButton;
	private Button copyButton;
	private Button moveButton;
	private Button deleteButton;

	private enum Details {
		Accessed, Modified, Changed
	}

	public FileContextPopup(TextProvider textProvider,
			FileDetailsProvider detailsProvider, SessionInfo sessionInfo) {
		super(StyleConstants.FILE_CONTEXT);

		this.textProvider = textProvider;
		this.detailsProvider = detailsProvider;
		this.sessionInfo = sessionInfo;

		this.dateTimeFormat = com.google.gwt.i18n.client.DateTimeFormat
				.getFormat(textProvider.getStrings().shortDateTimeFormat());

		initialize();
	}

	public void setFileActionHandler(FileSystemActionHandler actionHandler) {
		this.fileActionHandler = actionHandler;
	}

	protected Widget createContent() {
		VerticalPanel content = new VerticalPanel();
		content.setStyleName(StyleConstants.FILE_CONTEXT_CONTENT);

		filename = new Label();
		filename.setStyleName(StyleConstants.FILE_CONTEXT_FILENAME);
		content.add(filename);

		description = new Label();
		description.setStyleName(StyleConstants.FILE_CONTEXT_DESCRIPTION);
		content.add(description);

		content.add(createDetails());
		content.add(createButtons());
		return content;
	}

	private Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setStyleName(StyleConstants.FILE_CONTEXT_BUTTONS);

		renameButton = createActionButton(textProvider.getStrings()
				.fileActionRenameTitle(), FileSystemAction.rename);
		renameButton.setVisible(false);

		copyButton = createActionButton(textProvider.getStrings()
				.fileActionCopyTitle(), FileSystemAction.copy);
		copyButton.setVisible(false);

		moveButton = createActionButton(textProvider.getStrings()
				.fileActionMoveTitle(), FileSystemAction.move);
		moveButton.setVisible(false);

		deleteButton = createActionButton(textProvider.getStrings()
				.fileActionDeleteTitle(), FileSystemAction.delete);
		deleteButton.setVisible(false);

		if (sessionInfo.getSettings().isZipDownloadEnabled()) {
			MultiActionButton downloadButton = createMultiActionButton(this,
					textProvider.getStrings().fileActionDownloadTitle(),
					FileSystemAction.download.name());
			downloadButton.addAction(FileSystemAction.download, textProvider
					.getStrings().fileActionDownloadTitle());
			downloadButton.addAction(FileSystemAction.download_as_zip,
					textProvider.getStrings().fileActionDownloadZippedTitle());
			downloadButton.setDefaultAction(FileSystemAction.download);
			buttons.add(downloadButton);
		} else {
			buttons.add(createActionButton(textProvider.getStrings()
					.fileActionDownloadTitle(), FileSystemAction.download));
		}

		buttons.add(renameButton);
		buttons.add(copyButton);
		buttons.add(moveButton);
		buttons.add(deleteButton);

		return buttons;
	}

	private Widget createDetails() {
		details = new DisclosurePanel(textProvider.getStrings()
				.fileActionDetailsTitle(), false);
		details.addStyleName(StyleConstants.FILE_CONTEXT_DETAILS);
		details.getHeader().getElement().getParentElement().setClassName(
				StyleConstants.FILE_CONTEXT_DETAILS_HEADER);

		VerticalPanel content = new VerticalPanel();
		content.setStyleName(StyleConstants.FILE_CONTEXT_DETAILS_CONTENT);

		for (Details detail : Details.values()) {
			String title = "";
			if (detail.equals(Details.Accessed))
				title = textProvider.getStrings()
						.fileDetailsLabelLastAccessed();
			else if (detail.equals(Details.Modified))
				title = textProvider.getStrings()
						.fileDetailsLabelLastModified();
			else if (detail.equals(Details.Changed))
				title = textProvider.getStrings().fileDetailsLabelLastChanged();

			content.add(createDetailsRow(title, detail.name().toLowerCase()));
		}

		details.setContent(content);
		return details;
	}

	private Widget createDetailsRow(String labelText, String style) {
		HorizontalPanel detailsRow = new HorizontalPanel();
		detailsRow.setStyleName(StyleConstants.FILE_CONTEXT_DETAILS_ROW);

		Label label = new Label(labelText);
		label
				.setStylePrimaryName(StyleConstants.FILE_CONTEXT_DETAILS_ROW_LABEL);
		label.addStyleDependentName(style);
		detailsRow.add(label);

		Label value = new Label();
		label
				.setStylePrimaryName(StyleConstants.FILE_CONTEXT_DETAILS_ROW_VALUE);
		label.addStyleDependentName(style);
		detailsRow.add(value);

		detailRowValues.add(value);

		return detailsRow;
	}

	public File getFile() {
		return file;
	}

	public void update(File file, Element parent) {
		setParent(parent);

		this.file = file;
		details.setOpen(false);

		filename.setText(file.getName());
		emptyDetails();

		detailsProvider.getFileDetails(file, new ResultListener<FileDetails>() {
			public void onFail(ServiceError error) {
				description.setText(error.getType().getMessage(textProvider));
			}

			public void onSuccess(FileDetails result) {
				updateDetails(result);
			}
		});
	}

	private void emptyDetails() {
		description.setText("");

		for (Details detail : Details.values()) {
			detailRowValues.get(detail.ordinal()).setText("");
		}

		renameButton.setVisible(false);
		copyButton.setVisible(false);
		moveButton.setVisible(false);
		deleteButton.setVisible(false);
	}

	private void updateDetails(FileDetails details) {
		this.description.setText(details.getDescription());
		this.description
				.setVisible(details.getDescription().trim().length() > 0);

		for (Details detail : Details.values()) {
			Label value = detailRowValues.get(detail.ordinal());

			if (detail.equals(Details.Accessed)) {
				value.setText(dateTimeFormat.format(details.getLastAccessed()));
			} else if (detail.equals(Details.Modified))
				value.setText(dateTimeFormat.format(details.getLastModified()));
			else if (detail.equals(Details.Changed))
				value.setText(dateTimeFormat.format(details.getLastChanged()));
		}

		boolean writable = details.getFilePermission().canWrite();
		renameButton.setVisible(writable);
		deleteButton.setVisible(writable);

		boolean hasGeneralWritePermissions = sessionInfo
				.getDefaultPermissionMode().hasWritePermission();
		copyButton.setVisible(hasGeneralWritePermissions);
		moveButton.setVisible(hasGeneralWritePermissions);
	}

	protected void onAction(FileSystemAction action) {
		fileActionHandler.onAction(file, action);
		this.hide();
	}

	public void onActionTriggered(ActionId action) {
		onAction((FileSystemAction) action);
	}
}
