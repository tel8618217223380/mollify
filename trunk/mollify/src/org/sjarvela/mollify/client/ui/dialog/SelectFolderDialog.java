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
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SelectFolderDialog extends CenteredDialog implements TreeListener {
	private final DirectoryProvider directoryProvider;
	private final TextProvider textProvider;
	private final String message;
	private final String selectActionTitle;

	private Tree folders;
	private TreeItem rootItem;
	private Button selectButton;
	private Map<TreeItem, Directory> items = new HashMap();
	private List<TreeItem> itemsInitialized = new ArrayList();
	private SelectFolderListener listener;

	public SelectFolderDialog(TextProvider textProvider, String title,
			String message, String selectActionTitle,
			DirectoryProvider directoryProvider, SelectFolderListener listener) {
		super(title, StyleConstants.SELECT_FOLDER_DIALOG);
		this.textProvider = textProvider;
		this.message = message;
		this.selectActionTitle = selectActionTitle;
		this.directoryProvider = directoryProvider;
		this.listener = listener;

		initialize();
	}

	@Override
	Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.SELECT_FOLDER_DIALOG_CONTENT);

		Label messageLabel = new Label(message);
		messageLabel.setStyleName(StyleConstants.SELECT_FOLDER_DIALOG_MESSAGE);
		panel.add(messageLabel);

		folders = new Tree();
		folders.setStyleName(StyleConstants.SELECT_FOLDER_DIALOG_FOLDER_TREE);
		folders.addTreeListener(this);
		panel.add(folders);

		rootItem = new TreeItem();
		rootItem
				.setStyleName(StyleConstants.SELECT_FOLDER_DIALOG_FOLDERS_ROOT_ITEM);
		rootItem.setText(textProvider.getStrings()
				.selectFolderDialogFoldersRoot());
		rootItem.setTitle(rootItem.getText());

		folders.addItem(rootItem);
		return panel;
	}

	@Override
	Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.SELECT_FOLDER_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		selectButton = createButton(selectActionTitle, new ClickListener() {
			public void onClick(Widget sender) {
				onSelect();
			}
		}, StyleConstants.SELECT_FOLDER_DIALOG_BUTTON_SELECT);
		buttons.add(selectButton);

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickListener() {

					public void onClick(Widget sender) {
						SelectFolderDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		return buttons;
	}

	@Override
	void onShow() {
		super.onShow();

		directoryProvider.getDirectories(Directory.Empty,
				new ResultListener<List<Directory>>() {
					public void onFail(ServiceError error) {
						onError(error);
					}

					public void onSuccess(List<Directory> roots) {
						addSubFolders(rootItem, roots);
						rootItem.setState(true);
					}
				});
	}

	private void addSubFolders(TreeItem parent, List<Directory> dirs) {
		for (Directory dir : dirs)
			parent.addItem(createItem(dir));
	}

	protected TreeItem createItem(Directory dir) {
		Label label = new Label(dir.getName());
		label
				.setStyleName(StyleConstants.SELECT_FOLDER_DIALOG_FOLDER_TREE_ITEM_LABEL);

		TreeItem item = new TreeItem(label);
		item.setStyleName(StyleConstants.SELECT_FOLDER_DIALOG_FOLDER_TREE_ITEM);
		item.addItem(textProvider.getStrings()
				.selectFolderDialogRetrievingFolders());

		items.put(item, dir);
		return item;
	}

	protected void onError(ServiceError error) {
		Log.error(error.toString());
	}

	private void onSelect() {
		if (folders.getSelectedItem() == null
				|| folders.getSelectedItem().equals(rootItem))
			return;

		Directory selected = items.get(folders.getSelectedItem());
		listener.onSelect(selected);
	}

	public void onTreeItemSelected(TreeItem item) {
		this.selectButton.setEnabled(!item.equals(rootItem));
	}

	public void onTreeItemStateChanged(final TreeItem item) {
		if (item.equals(rootItem))
			return;

		if (item.getState() && !itemsInitialized.contains(item)) {
			getSubDirectories(item);
		}
	}

	private void getSubDirectories(final TreeItem item) {
		final Directory dir = items.get(item);

		directoryProvider.getDirectories(dir,
				new ResultListener<List<Directory>>() {
					public void onFail(ServiceError error) {
						onError(error);
					}

					public void onSuccess(List<Directory> subDirs) {
						itemsInitialized.add(item);
						item.removeItems();
						addSubFolders(item, subDirs);
					}
				});
	}
}
