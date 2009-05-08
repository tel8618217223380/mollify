/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SelectFolderDialog extends CenteredDialog implements
		SelectionHandler<TreeItem>, OpenHandler<TreeItem> {
	private static String pleaseWaitText = null;

	private final DialogManager dialogManager;
	private final DirectoryProvider directoryProvider;
	private final TextProvider textProvider;
	private final String message;
	private final String selectActionTitle;
	private final List<Directory> initialDirectoryPath;

	private Tree folders;
	private TreeItem rootItem;
	private Button selectButton;
	private Map<TreeItem, Directory> items = new HashMap();

	private List<TreeItem> itemsInitialized = new ArrayList();
	private SelectDirectoryListener listener;
	private TreeItem selected = null;

	public SelectFolderDialog(DialogManager dialogManager,
			TextProvider textProvider, String title, String message,
			String selectActionTitle, DirectoryProvider directoryProvider,
			SelectDirectoryListener listener,
			List<Directory> initialDirectoryPath) {
		super(title, StyleConstants.SELECT_FOLDER_DIALOG);
		this.dialogManager = dialogManager;
		this.textProvider = textProvider;
		this.message = message;
		this.selectActionTitle = selectActionTitle;
		this.directoryProvider = directoryProvider;
		this.listener = listener;
		this.initialDirectoryPath = initialDirectoryPath;

		if (pleaseWaitText == null)
			pleaseWaitText = textProvider.getStrings()
					.selectFolderDialogRetrievingFolders();

		initialize();
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.SELECT_FOLDER_DIALOG_CONTENT);

		HTML messageHtml = new HTML(message);
		messageHtml.setStyleName(StyleConstants.SELECT_FOLDER_DIALOG_MESSAGE);
		panel.add(messageHtml);

		folders = new Tree();
		folders.setStyleName(StyleConstants.SELECT_FOLDER_DIALOG_FOLDER_TREE);
		folders.addSelectionHandler(this);
		folders.addOpenHandler(this);
		panel.add(folders);

		rootItem = createItem(
				textProvider.getStrings().selectFolderDialogFoldersRoot(),
				StyleConstants.SELECT_FOLDER_DIALOG_FOLDER_TREE_ROOT_ITEM_LABEL,
				StyleConstants.SELECT_FOLDER_DIALOG_FOLDER_TREE_ROOT_ITEM);

		folders.addItem(rootItem);
		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.SELECT_FOLDER_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		selectButton = createButton(selectActionTitle, new ClickHandler() {
			public void onClick(ClickEvent event) {
				onSelect();
			}
		}, StyleConstants.SELECT_FOLDER_DIALOG_BUTTON_SELECT);
		buttons.add(selectButton);

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						SelectFolderDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		selectButton.setEnabled(false);

		return buttons;
	}

	@Override
	protected void onShow() {
		super.onShow();

		directoryProvider.getDirectories(Directory.Empty,
				new ResultListener<List<Directory>>() {
					public void onFail(ServiceError error) {
						onRequestError(error);
					}

					public void onSuccess(List<Directory> roots) {
						onUpdateRoots(roots);
					}
				});
	}

	private void onUpdateRoots(List<Directory> roots) {
		addSubFolders(rootItem, roots);
		rootItem.setState(true);
		selectInitialDir();
	}

	private void selectInitialDir() {
		if (initialDirectoryPath == null || initialDirectoryPath.size() == 0)
			return;
		// TODO
	}

	private void addSubFolders(TreeItem parent, List<Directory> dirs) {
		for (Directory dir : dirs)
			parent.addItem(createDirItem(dir));
	}

	protected TreeItem createDirItem(Directory dir) {
		TreeItem item = createItem(dir.getName(),
				StyleConstants.SELECT_FOLDER_DIALOG_FOLDER_TREE_ITEM_LABEL,
				StyleConstants.SELECT_FOLDER_DIALOG_FOLDER_TREE_ITEM);
		item.addItem(pleaseWaitText);
		items.put(item, dir);
		return item;
	}

	private TreeItem createItem(String labelTitle, String labelStyle,
			String itemStyle) {
		Label label = new Label(labelTitle);
		label.setStylePrimaryName(labelStyle);

		TreeItem item = new TreeItem(label);
		item.addStyleName(itemStyle);

		item.setUserObject(label);
		return item;
	}

	private Widget getLabel(TreeItem item) {
		return ((Widget) item.getUserObject());
	}

	private void onSelect() {
		if (folders.getSelectedItem() == null
				|| folders.getSelectedItem().equals(rootItem))
			return;

		this.hide();
		listener.onSelect(items.get(folders.getSelectedItem()));
	}

	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		boolean allowed = false;

		if (!item.equals(rootItem))
			allowed = listener.isDirectoryAllowed(items.get(item),
					getDirectoryPath(item));

		this.selectButton.setEnabled(allowed);

		if (selected != null)
			getLabel(selected)
					.removeStyleDependentName(StyleConstants.SELECTED);
		if (!allowed)
			return;

		selected = item;
		getLabel(selected).addStyleDependentName(StyleConstants.SELECTED);

	}

	private List<Directory> getDirectoryPath(TreeItem item) {
		List<Directory> list = new ArrayList();
		TreeItem current = item;

		while (true) {
			list.add(items.get(current));
			if (current.equals(rootItem))
				break;
			current = current.getParentItem();
		}
		return list;
	}

	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem item = event.getTarget();

		if (item.equals(rootItem))
			return;

		if (item.getState() && !itemsInitialized.contains(item))
			addSubDirectories(item);
	}

	private void addSubDirectories(final TreeItem item) {
		final Directory dir = items.get(item);

		directoryProvider.getDirectories(dir,
				new ResultListener<List<Directory>>() {
					public void onFail(ServiceError error) {
						onRequestError(error);
					}

					public void onSuccess(List<Directory> subDirs) {
						itemsInitialized.add(item);
						item.removeItems();
						addSubFolders(item, subDirs);
					}
				});
	}

	protected void onRequestError(ServiceError error) {
		Log.error(error.toString());
		dialogManager.showError(error);
		this.hide();
	}
}
