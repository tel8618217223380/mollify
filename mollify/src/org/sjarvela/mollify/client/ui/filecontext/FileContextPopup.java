/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.filecontext;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.data.FileDetails;
import org.sjarvela.mollify.client.file.FileAction;
import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileDetailsProvider;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyError;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.ui.BorderedControl;
import org.sjarvela.mollify.client.ui.DropdownPopup;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileContextPopup extends DropdownPopup {
	private DateTimeFormat dateTimeFormat;

	private final Localizator localizator;
	private final FileActionHandler fileActionHandler;
	private final FileDetailsProvider detailsProvider;

	private Label filename;
	private File file = File.Empty;
	private Label description;
	private List<Label> detailRowValues = new ArrayList<Label>();
	private DisclosurePanel details;

	private Button downloadButton;
	private Button renameButton;
	private Button deleteButton;

	private enum Details {
		Accessed, Modified, Changed
	}

	public FileContextPopup(Localizator localizator,
			FileDetailsProvider detailsProvider,
			FileActionHandler fileActionHandler) {
		super(null, null);

		this.localizator = localizator;
		this.detailsProvider = detailsProvider;
		this.fileActionHandler = fileActionHandler;
		this.setStyleName(StyleConstants.FILE_CONTEXT);

		this.dateTimeFormat = com.google.gwt.i18n.client.DateTimeFormat
				.getFormat(localizator.getStrings().shortDateTimeFormat());

		BorderedControl content = new BorderedControl(
				StyleConstants.FILE_CONTEXT_BORDER);

		content.setContent(createContent());
		// extra content, tip pointing the file (just a div with a certain
		// style)
		content.setWidget(0, 1, createPointer());

		addItem(content);
	}

	private Widget createPointer() {
		Label pointer = new Label();
		pointer.setStyleName(StyleConstants.FILE_CONTEXT_POINTER);
		return pointer;
	}

	private VerticalPanel createContent() {
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

		downloadButton = createActionButton(localizator.getStrings()
				.fileActionDownloadTitle(), FileAction.DOWNLOAD);
		renameButton = createActionButton(localizator.getStrings()
				.fileActionRenameTitle(), FileAction.RENAME);
		renameButton.setVisible(false);
		deleteButton = createActionButton(localizator.getStrings()
				.fileActionDeleteTitle(), FileAction.DELETE);
		deleteButton.setVisible(false);

		buttons.add(downloadButton);
		buttons.add(renameButton);
		buttons.add(deleteButton);

		return buttons;
	}

	private Widget createDetails() {
		details = new DisclosurePanel(localizator.getStrings()
				.fileActionDetailsTitle(), false);
		details.addStyleName(StyleConstants.FILE_CONTEXT_DETAILS);
		details.getHeader().getElement().getParentElement().setClassName(
				StyleConstants.FILE_CONTEXT_DETAILS_HEADER);

		VerticalPanel content = new VerticalPanel();
		content.setStyleName(StyleConstants.FILE_CONTEXT_DETAILS_CONTENT);

		for (Details detail : Details.values()) {
			String title = "?";
			if (detail.equals(Details.Accessed))
				title = localizator.getStrings().fileDetailsLabelLastAccessed();
			else if (detail.equals(Details.Modified))
				title = localizator.getStrings().fileDetailsLabelLastModified();
			else if (detail.equals(Details.Changed))
				title = localizator.getStrings().fileDetailsLabelLastChanged();

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

	private Button createActionButton(String title, final FileAction action) {
		Button button = new Button(title);
		button.addStyleName(StyleConstants.FILE_CONTEXT_ACTION);
		button.getElement().setId(
				StyleConstants.FILE_CONTEXT_ACTION + "-"
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
		details.setOpen(false);

		filename.setText(file.getName());
		emptyDetails();

		detailsProvider.getFileDetails(file, new ResultListener() {
			public void onFail(MollifyError error) {
				description.setText(error.getError().getMessage(localizator));
			}

			public void onSuccess(Object... result) {
				updateDetails((FileDetails) result[0]);
			}
		});
	}

	private void emptyDetails() {
		description.setText("");

		for (Details detail : Details.values()) {
			detailRowValues.get(detail.ordinal()).setText("");
		}

		renameButton.setVisible(false);
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
	}

	private void onAction(FileAction action) {
		fileActionHandler.onFileAction(file, action);
		this.hide();
	}
}
