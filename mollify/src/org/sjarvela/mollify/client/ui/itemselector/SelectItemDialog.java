/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.itemselector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.DirectoryContent;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

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

public class SelectItemDialog extends CenteredDialog implements
		SelectionHandler<TreeItem>, OpenHandler<TreeItem> {
	public enum Mode {
		Folders, FoldersAndFiles
	};

	private static String pleaseWaitText = null;

	private final Mode mode;
	private final DialogManager dialogManager;
	private final FileSystemItemProvider fileSystemItemProvider;
	private final TextProvider textProvider;
	private final String message;
	private final String selectActionTitle;

	private Tree itemTree;
	private TreeItem rootItem;
	private Button selectButton;
	private Map<TreeItem, FileSystemItem> items = new HashMap();

	private List<TreeItem> itemsInitialized = new ArrayList();
	private SelectItemHandler listener;
	private TreeItem selected = null;

	public SelectItemDialog(Mode mode, DialogManager dialogManager,
			TextProvider textProvider, String title, String message,
			String selectActionTitle,
			FileSystemItemProvider fileSystemItemProvider,
			SelectItemHandler handler) {
		super(
				title,
				Mode.Folders.equals(mode) ? StyleConstants.SELECT_ITEM_DIALOG_FOLDER
						: StyleConstants.SELECT_ITEM_DIALOG);

		this.mode = mode;
		this.dialogManager = dialogManager;
		this.textProvider = textProvider;
		this.message = message;
		this.selectActionTitle = selectActionTitle;
		this.fileSystemItemProvider = fileSystemItemProvider;
		this.listener = handler;

		if (pleaseWaitText == null)
			pleaseWaitText = textProvider.getStrings()
					.selectFolderDialogRetrievingFolders();

		this.addViewListener(new ViewListener() {
			public void onShow() {
				SelectItemDialog.this.onShow();
			}
		});
		initialize();
	}

	@Override
	protected Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		panel.addStyleName(StyleConstants.SELECT_ITEM_DIALOG_CONTENT);

		HTML messageHtml = new HTML(message);
		messageHtml.setStyleName(StyleConstants.SELECT_ITEM_DIALOG_MESSAGE);
		panel.add(messageHtml);

		itemTree = new Tree();
		itemTree.setStyleName(StyleConstants.SELECT_ITEM_DIALOG_TREE);
		itemTree.addSelectionHandler(this);
		itemTree.addOpenHandler(this);
		panel.add(itemTree);

		rootItem = createItem(textProvider.getStrings()
				.selectFolderDialogFoldersRoot(),
				StyleConstants.SELECT_ITEM_DIALOG_TREE_ROOT_ITEM_LABEL,
				StyleConstants.SELECT_ITEM_DIALOG_TREE_ROOT_ITEM);

		itemTree.addItem(rootItem);
		return panel;
	}

	@Override
	protected Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.addStyleName(StyleConstants.SELECT_ITEM_DIALOG_BUTTONS);
		buttons.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

		selectButton = createButton(selectActionTitle, new ClickHandler() {
			public void onClick(ClickEvent event) {
				onSelect();
			}
		}, StyleConstants.SELECT_ITEM_DIALOG_BUTTON_SELECT);
		buttons.add(selectButton);

		buttons.add(createButton(
				textProvider.getStrings().dialogCancelButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						SelectItemDialog.this.hide();
					}
				}, StyleConstants.DIALOG_BUTTON_CANCEL));

		selectButton.setEnabled(false);

		return buttons;
	}

	private void onShow() {
		onUpdateRoots(fileSystemItemProvider.getRootDirectories());
	}

	private void onUpdateRoots(List<Directory> roots) {
		addSubItems(rootItem, roots);
		rootItem.setState(true);
		selectInitialDir();
	}

	private void selectInitialDir() {
		// TODO
	}

	private void addSubItems(TreeItem parent,
			List<? extends FileSystemItem> items) {
		List<? extends FileSystemItem> list = new ArrayList(items);
		Collections.sort(list, new Comparator<FileSystemItem>() {
			public int compare(FileSystemItem item1, FileSystemItem item2) {
				if (item1.isFile() && !item2.isFile())
					return 1;
				if (item2.isFile() && !item1.isFile())
					return -1;
				return item1.getName().compareToIgnoreCase(item2.getName());
			}
		});

		for (FileSystemItem item : list)
			parent.addItem(item.isFile() ? createFileItem((File) item)
					: createDirItem((Directory) item));
	}

	protected TreeItem createDirItem(Directory dir) {
		TreeItem item = createItem(dir.getName(),
				StyleConstants.SELECT_ITEM_DIALOG_TREE_ITEM_LABEL_DIR,
				StyleConstants.SELECT_ITEM_DIALOG_TREE_ITEM);
		item.addItem(pleaseWaitText);
		items.put(item, dir);
		return item;
	}

	protected TreeItem createFileItem(File file) {
		TreeItem item = createItem(file.getName(),
				StyleConstants.SELECT_ITEM_DIALOG_TREE_ITEM_LABEL_FILE,
				StyleConstants.SELECT_ITEM_DIALOG_TREE_ITEM);
		items.put(item, file);
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
		if (itemTree.getSelectedItem() == null
				|| itemTree.getSelectedItem().equals(rootItem))
			return;

		this.hide();
		listener.onSelect(items.get(itemTree.getSelectedItem()));
	}

	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		boolean allowed = false;

		if (!item.equals(rootItem))
			allowed = listener.isItemAllowed(items.get(item),
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

	private List<Directory> getDirectoryPath(TreeItem treeItem) {
		List<Directory> list = new ArrayList();
		TreeItem current = treeItem;

		while (true) {
			FileSystemItem item = items.get(current);
			if (!item.isFile())
				list.add((Directory) item);
			current = current.getParentItem();
			if (current.equals(rootItem))
				break;
		}
		return list;
	}

	public void onOpen(OpenEvent<TreeItem> event) {
		TreeItem item = event.getTarget();

		if (item.equals(rootItem))
			return;

		if (item.getState() && !itemsInitialized.contains(item))
			addSubItems(item);
	}

	private void addSubItems(final TreeItem treeItem) {
		final FileSystemItem item = items.get(treeItem);
		if (item.isFile())
			return;

		if (Mode.Folders.equals(this.mode)) {
			fileSystemItemProvider.getDirectories((Directory) item,
					new ResultListener<List<Directory>>() {
						public void onFail(ServiceError error) {
							onRequestError(error);
						}

						public void onSuccess(List<Directory> subDirs) {
							itemsInitialized.add(treeItem);
							treeItem.removeItems();
							SelectItemDialog.this
									.addSubItems(treeItem, subDirs);
						}
					});
		} else {
			fileSystemItemProvider.getFilesAndFolders((Directory) item,
					new ResultListener<DirectoryContent>() {
						public void onFail(ServiceError error) {
							onRequestError(error);
						}

						public void onSuccess(DirectoryContent result) {
							itemsInitialized.add(treeItem);
							treeItem.removeItems();

							List<FileSystemItem> list = new ArrayList(result
									.getDirectories());
							list.addAll(result.getFiles());
							addSubItems(treeItem, list);
						}
					});
		}
	}

	protected void onRequestError(ServiceError error) {
		Log.error(error.toString());
		dialogManager.showError(error);
		this.hide();
	}
}
