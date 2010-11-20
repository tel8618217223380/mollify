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
import org.sjarvela.mollify.client.filesystem.js.JsFile;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.sjarvela.mollify.client.ui.filelist.FileList;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchResultDialog extends CenteredDialog {

	private final SearchResult result;
	private FileList list;

	public SearchResultDialog(TextProvider textProvider, String criteria,
			SearchResult result) {
		super("TODO search results " + criteria, "search-results");
		this.result = result;

		this.list = new FileList(textProvider, null);
		this.list.setContent(getItems());

		initialize();
	}

	private List<FileSystemItem> getItems() {
		List<FileSystemItem> list = new ArrayList();
		JsArray matches = result.getArray("matches");
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
		VerticalPanel panel = new VerticalPanel();
		panel.add(list);
		return list;
	}

}
