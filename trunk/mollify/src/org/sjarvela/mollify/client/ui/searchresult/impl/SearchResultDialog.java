/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.searchresult.impl;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.SearchResult;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.js.JsFile;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.ResizableDialog;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.Sort;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.ContextPopupHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.DefaultItemContextPopupFactory;
import org.sjarvela.mollify.client.ui.fileitemcontext.popup.ItemContextPopup;
import org.sjarvela.mollify.client.ui.filelist.FileList;
import org.sjarvela.mollify.client.ui.formatter.PathFormatter;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class SearchResultDialog extends ResizableDialog {
	private final TextProvider textProvider;
	private final String criteria;
	private final SearchResult result;

	private SearchResultFileList list;
	private FlowPanel listPanel;
	private ItemContextPopup itemContextPopup;
	private ContextPopupHandler<FileSystemItem> itemContextHandler;

	public SearchResultDialog(TextProvider textProvider, String criteria,
			SearchResult result, PathFormatter formatter,
			DefaultItemContextPopupFactory itemContextPopupFactory,
			FileSystemActionHandler FileSystemActionHandler, DropBox dropBox) {
		super(textProvider.getStrings().searchResultsDialogTitle(),
				"search-results");
		this.textProvider = textProvider;
		this.criteria = criteria;
		this.result = result;

		this.itemContextPopup = itemContextPopupFactory.createPopup(dropBox);
		this.itemContextHandler = new ContextPopupHandler<FileSystemItem>(
				itemContextPopup);

		this.list = new SearchResultFileList(textProvider, formatter);
		this.list.setContent(getItems());
		this.list.addListener(new GridListener<FileSystemItem>() {
			@Override
			public void onColumnClicked(FileSystemItem item, String columnId) {
				itemContextHandler.onItemSelected(item,
						list.getWidget(item, FileList.COLUMN_ID_NAME));
			}

			@Override
			public void onIconClicked(FileSystemItem item) {
			}

			@Override
			public void onColumnSorted(String columnId, Sort sort) {
			}

			@Override
			public void onSelectionChanged(List<FileSystemItem> selected) {
			}
		});

		this.itemContextPopup.setActionHandler(FileSystemActionHandler);
		// this.itemContextPopup.setPopupPositioner(this);

		this.setMinimumSize(500, 300);
		initialize();
	}

	private List<FileSystemItem> getItems() {
		List<FileSystemItem> list = new ArrayList();
		JsArray matches = result.getMatches();
		for (int i = 0; i < matches.length(); i++) {
			JsObj o = matches.get(i).cast();
			JsObj item = o.getJsObj("item");

			if (item.getBoolean("is_file"))
				list.add(FileSystemItem.createFrom((JsFile) item.cast()));
			else
				list.add(FileSystemItem.createFrom((JsFolder) item.cast()));
		}
		return list;
	}

	@Override
	protected Widget createContent() {
		Panel panel = new FlowPanel();
		panel.setStylePrimaryName(StyleConstants.SEARCH_RESULTS_DIALOG_CONTENT);

		panel.add(createInfoPanel());

		listPanel = new FlowPanel();
		listPanel
				.setStylePrimaryName(StyleConstants.SEARCH_RESULTS_DIALOG_LIST);
		listPanel.add(list);

		panel.add(listPanel);
		return panel;
	}

	@Override
	protected Element getSizedElement() {
		return listPanel.getElement();
	}

	private Widget createInfoPanel() {
		Panel p = new FlowPanel();
		p.setStylePrimaryName(StyleConstants.SEARCH_RESULTS_DIALOG_INFO);
		Label info = new Label(textProvider.getMessages().searchResultsInfo(
				criteria, result.getMatchCount()));
		info.setStylePrimaryName(StyleConstants.SEARCH_RESULTS_DIALOG_INFO_TEXT);
		p.add(info);
		return p;
	}

	@Override
	protected Widget createButtons() {
		FlowPanel buttons = new FlowPanel();
		buttons.addStyleName(StyleConstants.SEARCH_RESULTS_DIALOG_BUTTONS);

		buttons.add(createButton(textProvider.getStrings().dialogCloseButton(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						SearchResultDialog.this.hide();
					}
				}, "search-results"));

		return buttons;
	}
}
