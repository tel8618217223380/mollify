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

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.provider.DirectoryDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.ActionLink;
import org.sjarvela.mollify.client.ui.common.EditableLabel;
import org.sjarvela.mollify.client.ui.common.SwitchPanel;
import org.sjarvela.mollify.client.ui.popup.ContextPopup;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryContextPopup extends ContextPopup implements
		ActionListener {
	private final TextProvider textProvider;
	private final DirectoryDetailsProvider detailsProvider;

	private final boolean folderActionsEnabled;
	private final boolean zipDownloadEnabled;
	private final boolean descriptionEditingEnabled;

	private FileSystemActionHandler actionHandler;

	private Label name;
	private EditableLabel description;
	private Button downloadButton;
	private Button renameButton;
	private Button deleteButton;

	private Directory directory;
	private ActionLink addDescription;
	private ActionLink removeDescription;
	private ActionLink editDescription;
	private ActionLink applyDescription;
	private ActionLink cancelEditDescription;
	private SwitchPanel descriptionActionsSwitch;

	public enum Action implements ResourceId {
		addDescription, editDescription, removeDescription, cancelEditDescription, applyDescription
	}

	public enum DescriptionActionGroup implements ResourceId {
		view, edit
	}

	public DirectoryContextPopup(TextProvider textProvider,
			DirectoryDetailsProvider detailsProvider,
			boolean folderActionsEnabled, boolean zipDownloadEnabled,
			boolean descriptionEditingEnabled) {
		super(StyleConstants.DIR_CONTEXT);

		this.textProvider = textProvider;
		this.detailsProvider = detailsProvider;
		this.folderActionsEnabled = folderActionsEnabled;
		this.zipDownloadEnabled = zipDownloadEnabled;
		this.descriptionEditingEnabled = descriptionEditingEnabled;

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

		description = new EditableLabel(StyleConstants.DIR_CONTEXT_DESCRIPTION);
		content.add(description);

		if (descriptionEditingEnabled)
			createDescriptionActions(content);

		content.add(createButtons());
		return content;
	}

	private void createDescriptionActions(Panel content) {
		addDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsAddDescription(),
				StyleConstants.FILE_CONTEXT_ADD_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		addDescription.setAction(this, Action.addDescription);

		removeDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsRemoveDescription(),
				StyleConstants.FILE_CONTEXT_REMOVE_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		removeDescription.setAction(this, Action.removeDescription);

		editDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsEditDescription(),
				StyleConstants.FILE_CONTEXT_EDIT_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		editDescription.setAction(this, Action.editDescription);

		applyDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsApplyDescription(),
				StyleConstants.FILE_CONTEXT_APPLY_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		applyDescription.setAction(this, Action.applyDescription);

		cancelEditDescription = new ActionLink(textProvider.getStrings()
				.fileDetailsCancelEditDescription(),
				StyleConstants.FILE_CONTEXT_CANCEL_EDIT_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		cancelEditDescription.setAction(this,
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

		content.add(descriptionActionsSwitch);
	}

	private Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setStyleName(StyleConstants.DIR_CONTEXT_BUTTONS);

		if (this.zipDownloadEnabled)
			downloadButton = createActionButton(textProvider.getStrings()
					.dirActionDownloadTitle(), this,
					FileSystemAction.download_as_zip);
		renameButton = createActionButton(textProvider.getStrings()
				.dirActionRenameTitle(), this, FileSystemAction.rename);
		renameButton.setVisible(false);
		deleteButton = createActionButton(textProvider.getStrings()
				.dirActionDeleteTitle(), this, FileSystemAction.delete);
		deleteButton.setVisible(false);

		if (this.zipDownloadEnabled)
			buttons.add(downloadButton);
		buttons.add(renameButton);
		buttons.add(deleteButton);

		return buttons;
	}

	public void update(Directory directory, Element element) {
		setParent(element);
		this.directory = directory;
		name.setText(directory.getName());

		detailsProvider.getDirectoryDetails(directory,
				new ResultListener<DirectoryDetails>() {
					public void onFail(ServiceError error) {
						name.setText(error.getType().getMessage(textProvider));
					}

					public void onSuccess(DirectoryDetails result) {
						updateDetails(result);
					}
				});
	}

	protected void updateDetails(DirectoryDetails details) {
		boolean writable = folderActionsEnabled
				&& details.getFilePermission().canWrite();
		renameButton.setVisible(writable);
		deleteButton.setVisible(writable);
	}

	public void onAction(ResourceId action) {
		actionHandler.onAction(directory, (FileSystemAction) action);
		this.hide();
	}

}
