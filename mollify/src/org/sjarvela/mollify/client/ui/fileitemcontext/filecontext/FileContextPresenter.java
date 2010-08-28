/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.filecontext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemPermissionHandler;
import org.sjarvela.mollify.client.filesystem.provider.FileDetailsProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.fileitemcontext.FileItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.PopupPanel;

public class FileContextPresenter implements ActionListener {
	private final FileItemContextComponent popup;
	private final FileDetailsProvider fileDetailsProvider;
	private final TextProvider textProvider;
	private final DateTimeFormat dateTimeFormat;
	private final SessionInfo session;
	private final DropBox dropBox;
	private final ItemContextProvider itemContextProvider;
	private final DialogManager dialogManager;

	private FileSystemActionHandler fileSystemActionHandler;
	private FileSystemPermissionHandler permissionHandler;

	private File file = File.Empty;
	private FileDetails details;
	private List<ItemContextComponent> components;

	private enum Details implements ResourceId {
		Accessed, Modified, Changed
	}

	public FileContextPresenter(FileItemContextComponent popup,
			SessionInfo session, FileDetailsProvider fileDetailsProvider,
			TextProvider textProvider, DropBox dropBox,
			ItemContextProvider itemContextProvider, DialogManager dialogManager) {
		this.popup = popup;
		this.session = session;
		this.fileDetailsProvider = fileDetailsProvider;
		this.textProvider = textProvider;
		this.dropBox = dropBox;
		this.itemContextProvider = itemContextProvider;
		this.dialogManager = dialogManager;
		this.dateTimeFormat = com.google.gwt.i18n.client.DateTimeFormat
				.getFormat(textProvider.getStrings().shortDateTimeFormat());

		popup.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				for (ItemContextComponent c : components)
					c.onContextClose();
			}
		});
		initializeDetails();
	}

	private void initializeDetails() {
		List<ResourceId> order = (List<ResourceId>) Arrays.asList(
				(ResourceId) Details.Modified, (ResourceId) Details.Changed,
				(ResourceId) Details.Accessed);
		Map<ResourceId, String> headers = new HashMap();
		headers.put(Details.Accessed, textProvider.getStrings()
				.fileDetailsLabelLastAccessed());
		headers.put(Details.Changed, textProvider.getStrings()
				.fileDetailsLabelLastChanged());
		headers.put(Details.Modified, textProvider.getStrings()
				.fileDetailsLabelLastModified());

		this.popup.initializeDetailsSection(order, headers);
	}

	public void setFileActionHandler(FileSystemActionHandler actionHandler) {
		this.fileSystemActionHandler = actionHandler;
	}

	public void setPermissionHandler(
			FileSystemPermissionHandler permissionHandler) {
		this.permissionHandler = permissionHandler;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;

		popup.getDetails().setOpen(false);
		popup.getName().setText(file.getName());
		updateDetails(null);

		fileDetailsProvider.getFileDetails(file,
				new ResultListener<FileDetails>() {
					public void onFail(ServiceError error) {
						dialogManager.showError(error);
					}

					public void onSuccess(FileDetails details) {
						updateDetails(details);
					}
				});
	}

	private void updateDetails(FileDetails details) {
		this.popup.reset();

		this.components = Collections.EMPTY_LIST;
		if (details != null) {
			components = popup.createComponents(itemContextProvider
					.getItemContext(file));
		}

		this.details = details;

		if (details != null) {
			this.popup.setDetailValue(Details.Accessed, dateTimeFormat
					.format(details.getLastAccessed()));
			this.popup.setDetailValue(Details.Modified, dateTimeFormat
					.format(details.getLastModified()));
			this.popup.setDetailValue(Details.Changed, dateTimeFormat
					.format(details.getLastChanged()));
		}

		boolean writable = (details == null ? false : details
				.getFilePermission().canWrite());
		boolean isView = session.getFeatures().fileView() && details != null
				&& details.getFileView() != null;

		popup.update(writable, isView);

		List<ItemContextComponent> rejected = new ArrayList();
		for (ItemContextComponent c : components)
			if (!c.onInit(file, details))
				rejected.add(c);
		components.removeAll(rejected);
		popup.removeComponents(rejected);
	}

	public void onAction(ResourceId action, Object o) {
		if (FileSystemAction.class.equals(action.getClass())) {
			Object param = null;
			if (action.equals(FileSystemAction.view))
				param = details.getFileView();
			fileSystemActionHandler.onAction(file, (FileSystemAction) action,
					popup, param);
			popup.hide();
			return;
		}

		if (FileItemContextComponent.Action.addToDropbox.equals(action))
			onAddToDropbox();
		else if (FileItemContextComponent.Action.editPermissions.equals(action)) {
			popup.hide();
			permissionHandler.onEditPermissions(file);
		}
	}

	private void onAddToDropbox() {
		dropBox.addItems(Arrays.asList((FileSystemItem) file));
	}
}
