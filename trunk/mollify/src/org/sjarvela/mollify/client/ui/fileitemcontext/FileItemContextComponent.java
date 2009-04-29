/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.ActionLink;
import org.sjarvela.mollify.client.ui.common.EditableLabel;
import org.sjarvela.mollify.client.ui.common.MultiActionButton;
import org.sjarvela.mollify.client.ui.common.SwitchPanel;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileItemContextComponent extends ContextPopup {
	private final TextProvider textProvider;
	private final ActionListener actionListener;

	private final boolean hasGeneralWritePermissions;
	private final boolean descriptionEditingEnabled;
	private final boolean zipDownloadEnabled;

	private Label name;

	private EditableLabel description;
	private SwitchPanel<DescriptionActionGroup> descriptionActionsSwitch;

	private ActionLink editDescription;
	private ActionLink addDescription;
	private ActionLink applyDescription;
	private ActionLink cancelEditDescription;
	private ActionLink removeDescription;

	private DisclosurePanel details;
	private Map<ResourceId, Label> detailRowValues = new HashMap();

	private Button renameButton;
	private Button copyButton;
	private Button moveButton;
	private Button deleteButton;
	private final Mode mode;

	public enum Mode {
		File, Directory
	}

	public enum Action implements ResourceId {
		addDescription, editDescription, removeDescription, cancelEditDescription, applyDescription
	}

	public enum DescriptionActionGroup implements ResourceId {
		view, edit
	}

	public FileItemContextComponent(Mode mode, TextProvider textProvider,
			boolean generalWritePermissions, boolean descriptionEditingEnabled,
			boolean zipDownloadEnabled, ActionListener actionListener) {
		super(Mode.File.equals(mode) ? StyleConstants.FILE_CONTEXT
				: StyleConstants.DIR_CONTEXT);
		this.mode = mode;

		this.hasGeneralWritePermissions = generalWritePermissions;
		this.descriptionEditingEnabled = descriptionEditingEnabled;
		this.zipDownloadEnabled = zipDownloadEnabled;

		this.textProvider = textProvider;
		this.actionListener = actionListener;

		initialize();
	}

	private boolean isFileMode() {
		return Mode.File.equals(mode);
	}

	protected Widget createContent() {
		Panel content = new VerticalPanel();
		content.setStyleName(StyleConstants.FILE_CONTEXT_CONTENT);

		name = new Label();
		name.setStyleName(StyleConstants.FILE_CONTEXT_FILENAME);
		content.add(name);

		description = new EditableLabel(
				StyleConstants.FILE_CONTEXT_DESCRIPTION, true);
		content.add(description);

		if (descriptionEditingEnabled)
			content.add(createDescriptionActions());

		content.add(createDetails());
		content.add(createButtons());
		return content;
	}

	private Widget createDescriptionActions() {
		addDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsAddDescription(),
				StyleConstants.FILE_CONTEXT_ADD_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		addDescription.setAction(actionListener, Action.addDescription);

		removeDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsRemoveDescription(),
				StyleConstants.FILE_CONTEXT_REMOVE_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		removeDescription.setAction(actionListener, Action.removeDescription);

		editDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsEditDescription(),
				StyleConstants.FILE_CONTEXT_EDIT_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		editDescription.setAction(actionListener, Action.editDescription);

		applyDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsApplyDescription(),
				StyleConstants.FILE_CONTEXT_APPLY_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		applyDescription.setAction(actionListener, Action.applyDescription);

		cancelEditDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsCancelEditDescription(),
				StyleConstants.FILE_CONTEXT_CANCEL_EDIT_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		cancelEditDescription.setAction(actionListener,
				Action.cancelEditDescription);

		Map<DescriptionActionGroup, Widget> groups = new HashMap();
		Panel descriptionActionsView = new FlowPanel();
		descriptionActionsView
				.setStyleName(StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTIONS);
		descriptionActionsView.add(addDescription);
		descriptionActionsView.add(editDescription);
		descriptionActionsView.add(removeDescription);
		groups.put(DescriptionActionGroup.view, descriptionActionsView);

		Panel descriptionActionsEdit = new FlowPanel();
		descriptionActionsEdit
				.setStyleName(StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTIONS);

		descriptionActionsEdit.add(applyDescription);
		descriptionActionsEdit.add(cancelEditDescription);
		groups.put(DescriptionActionGroup.edit, descriptionActionsEdit);

		descriptionActionsSwitch = new SwitchPanel(
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTIONS_SWITCH, groups);

		return descriptionActionsSwitch;
	}

	private Widget createButtons() {
		Panel buttons = new HorizontalPanel();
		buttons.setStyleName(StyleConstants.FILE_CONTEXT_BUTTONS);

		renameButton = createActionButton(textProvider.getStrings()
				.fileActionRenameTitle(), actionListener,
				FileSystemAction.rename);
		renameButton.setVisible(false);

		copyButton = createActionButton(textProvider.getStrings()
				.fileActionCopyTitle(), actionListener, FileSystemAction.copy);
		copyButton.setVisible(isFileMode() && hasGeneralWritePermissions);

		moveButton = createActionButton(textProvider.getStrings()
				.fileActionMoveTitle(), actionListener, FileSystemAction.move);
		moveButton.setVisible(false);

		deleteButton = createActionButton(textProvider.getStrings()
				.fileActionDeleteTitle(), actionListener,
				FileSystemAction.delete);
		deleteButton.setVisible(false);

		if (this.zipDownloadEnabled) {
			MultiActionButton downloadButton = createMultiActionButton(
					actionListener, textProvider.getStrings()
							.fileActionDownloadTitle(),
					FileSystemAction.download.name());
			downloadButton.addAction(FileSystemAction.download, textProvider
					.getStrings().fileActionDownloadTitle());
			downloadButton.addAction(FileSystemAction.download_as_zip,
					textProvider.getStrings().fileActionDownloadZippedTitle());
			downloadButton.setDefaultAction(FileSystemAction.download);
			buttons.add(downloadButton);
		} else {
			buttons.add(createActionButton(textProvider.getStrings()
					.fileActionDownloadTitle(), actionListener,
					FileSystemAction.download));
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
		details.setVisible(false);
		return details;
	}

	public void initializeDetailsSection(List<ResourceId> order,
			Map<ResourceId, String> headers) {
		this.detailRowValues.clear();

		Panel content = new VerticalPanel();
		content.setStyleName(StyleConstants.FILE_CONTEXT_DETAILS_CONTENT);

		for (ResourceId id : order) {
			this.detailRowValues.put(id, createDetailsRow(content, headers
					.get(id), id.name().toLowerCase()));
		}
		details.add(content);
		details.setVisible(true);
	}

	private Label createDetailsRow(Panel parent, String title, String style) {
		Panel detailsRow = new HorizontalPanel();
		detailsRow.setStylePrimaryName(StyleConstants.FILE_CONTEXT_DETAILS_ROW);
		detailsRow.addStyleDependentName(style);

		Label label = new Label(title);
		label
				.setStylePrimaryName(StyleConstants.FILE_CONTEXT_DETAILS_ROW_LABEL);
		label.addStyleDependentName(style);
		detailsRow.add(label);

		Label value = new Label();
		value
				.setStylePrimaryName(StyleConstants.FILE_CONTEXT_DETAILS_ROW_VALUE);
		value.addStyleDependentName(style);
		detailsRow.add(value);

		parent.add(detailsRow);
		return value;
	}

	public void reset() {
		description.setText("");

		for (Label detailsValue : detailRowValues.values())
			detailsValue.setText("");

		renameButton.setVisible(false);
		deleteButton.setVisible(false);
		moveButton.setVisible(false);
	}

	public void setDetailValue(ResourceId id, String value) {
		detailRowValues.get(id).setText(value);
	}

	public void updateButtons(boolean isWritable) {
		renameButton.setVisible(isWritable);
		deleteButton.setVisible(isWritable);
		moveButton.setVisible(isWritable && hasGeneralWritePermissions);
	}

	public void setDescription(String description) {
		this.description.setText(description);
	}

	public void setDescriptionEditable(boolean isEditable,
			boolean descriptionDefined) {
		description.setEditable(isEditable);
		description.setVisible(isEditable || descriptionDefined);

		if (!descriptionEditingEnabled)
			return;

		addDescription.setVisible(!isEditable && !descriptionDefined);
		editDescription.setVisible(!isEditable && descriptionDefined);
		removeDescription.setVisible(!isEditable && descriptionDefined);
		applyDescription.setVisible(isEditable);
		cancelEditDescription.setVisible(isEditable);

		if (isEditable)
			descriptionActionsSwitch.switchTo(DescriptionActionGroup.edit);
		else
			descriptionActionsSwitch.switchTo(DescriptionActionGroup.view);
	}

	public DisclosurePanel getDetails() {
		return details;
	}

	public Label getName() {
		return name;
	}

	public EditableLabel getDescription() {
		return description;
	}

	public ActionLink getEditDescription() {
		return editDescription;
	}

	public ActionLink getAddDescription() {
		return addDescription;
	}

	public ActionLink getApplyDescription() {
		return applyDescription;
	}

	public ActionLink getCancelEditDescription() {
		return cancelEditDescription;
	}

	public ActionLink getRemoveDescription() {
		return removeDescription;
	}
}
