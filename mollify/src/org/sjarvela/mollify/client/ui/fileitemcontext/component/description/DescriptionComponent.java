/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.component.description;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionLink;
import org.sjarvela.mollify.client.ui.common.EditableLabel;
import org.sjarvela.mollify.client.ui.common.SwitchPanel;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextContainer;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent.Action;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl.ItemContextPopupComponent.DescriptionActionGroup;
import org.sjarvela.mollify.client.util.Html;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DescriptionComponent implements ItemContextComponent,
		ActionListener {
	private final TextProvider textProvider;
	private final FileSystemService fileSystemService;
	private final DialogManager dialogManager;
	private final boolean descriptionUpdate;

	private EditableLabel description;
	private Widget component;
	private ActionLink addDescription;
	private ActionLink removeDescription;
	private ActionLink editDescription;
	private ActionLink applyDescription;
	private ActionLink cancelEditDescription;
	private SwitchPanel descriptionActionsSwitch;

	private ItemDetails details;
	private FileSystemItem item;

	public DescriptionComponent(TextProvider textProvider,
			FileSystemService fileSystemService, SessionInfo session,
			DialogManager dialogManager) {
		this.textProvider = textProvider;
		this.fileSystemService = fileSystemService;
		this.descriptionUpdate = session.getDefaultPermissionMode().isAdmin()
				&& session.getFeatures().descriptions();
		this.dialogManager = dialogManager;
	}

	@Override
	public Widget getComponent() {
		if (component == null)
			component = createContent();
		return component;
	}

	private Widget createContent() {
		description = new EditableLabel(
				StyleConstants.FILE_CONTEXT_DESCRIPTION, true);
		descriptionActionsSwitch = descriptionUpdate ? createDescriptionActions()
				: null;

		FlowPanel content = new FlowPanel();
		content.add(description);
		if (descriptionUpdate)
			content.add(descriptionActionsSwitch);
		return content;
	}

	private SwitchPanel createDescriptionActions() {
		addDescription = new ActionLink(
				textProvider.getText(Texts.fileDetailsAddDescription),
				StyleConstants.FILE_CONTEXT_ADD_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		addDescription.setAction(this, Action.addDescription);

		removeDescription = new ActionLink(
				textProvider.getText(Texts.fileDetailsRemoveDescription),
				StyleConstants.FILE_CONTEXT_REMOVE_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		removeDescription.setAction(this, Action.removeDescription);

		editDescription = new ActionLink(
				textProvider.getText(Texts.fileDetailsEditDescription),
				StyleConstants.FILE_CONTEXT_EDIT_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		editDescription.setAction(this, Action.editDescription);

		applyDescription = new ActionLink(
				textProvider.getText(Texts.fileDetailsApplyDescription),
				StyleConstants.FILE_CONTEXT_APPLY_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		applyDescription.setAction(this, Action.applyDescription);

		cancelEditDescription = new ActionLink(
				textProvider.getText(Texts.fileDetailsCancelEditDescription),
				StyleConstants.FILE_CONTEXT_CANCEL_EDIT_DESCRIPTION,
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTION);
		cancelEditDescription.setAction(this, Action.cancelEditDescription);

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

		return new SwitchPanel(
				StyleConstants.FILE_CONTEXT_DESCRIPTION_ACTIONS_SWITCH, groups);
	}

	@Override
	public boolean onInit(ItemContextContainer container, FileSystemItem item,
			ItemDetails details) {
		this.item = item;
		this.details = details;
		updateDescription();
		return true;
	}

	private void updateDescription() {
		boolean descriptionDefined = isDescriptionDefined();
		String visibleDescription = descriptionDefined ? details
				.getDescription() : "";

		this.description.setText(visibleDescription);
		setDescriptionEditable(false);
	}

	@Override
	public void onContextClose() {
		item = null;
		details = null;
	}

	@Override
	public void onAction(ResourceId action, Object o) {
		if (ItemContextPopupComponent.Action.addDescription.equals(action))
			onStartEditDescription();
		else if (ItemContextPopupComponent.Action.editDescription
				.equals(action))
			onStartEditDescription();
		else if (ItemContextPopupComponent.Action.cancelEditDescription
				.equals(action))
			onCancelEditDescription();
		else if (ItemContextPopupComponent.Action.applyDescription
				.equals(action))
			onApplyDescription();
		else if (ItemContextPopupComponent.Action.removeDescription
				.equals(action))
			onRemoveDescription();
	}

	private void onStartEditDescription() {
		setDescriptionEditable(true);
	}

	private void onApplyDescription() {
		final String desc = description.getText();
		if (!validateDescription(desc))
			return;

		setDescriptionEditable(false);

		fileSystemService.setItemDescription(item, desc, new ResultListener() {
			@Override
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

			@Override
			public void onSuccess(Object result) {
				details.setDescription(desc);
				updateDescription();
			}
		});
	}

	private void onCancelEditDescription() {
		updateDescription();
	}

	private void onRemoveDescription() {
		fileSystemService.removeItemDescription(item, new ResultListener() {
			@Override
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

			@Override
			public void onSuccess(Object result) {
				details.removeDescription();
				updateDescription();
			}
		});
	}

	private boolean isDescriptionDefined() {
		return (details != null && details.getDescription() != null);
	}

	private void setDescriptionEditable(boolean isEditable) {
		boolean descriptionDefined = isDescriptionDefined();
		description.setEditable(isEditable);
		description.setVisible(isEditable || descriptionDefined);

		if (!descriptionUpdate)
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

	private boolean validateDescription(String description) {
		List<String> unsafeTags = Html.findUnsafeHtmlTags(description);
		if (unsafeTags.size() > 0) {
			dialogManager.showInfo(
					textProvider.getText(Texts.infoDialogErrorTitle),
					textProvider.getText(Texts.invalidDescriptionUnsafeTags));
			return false;
		}
		return true;
	}
}
