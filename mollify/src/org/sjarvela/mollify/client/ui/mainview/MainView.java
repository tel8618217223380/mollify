/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview;

import java.util.List;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;

import com.google.gwt.core.client.JavaScriptObject;

public interface MainView {

	public enum Action implements ResourceId {
		addFile, addDirectory, refresh, logout, changePassword, admin, editItemPermissions, selectMode, selectAll, selectNone, copyMultiple, moveMultiple, deleteMultiple, slideBar, addToDropbox, retrieveUrl, listView, gridViewSmall, gridViewLarge;
	};

	public enum ViewType {
		list, gridSmall, gridLarge
	};

	void init(List<JsRootFolder> rootFolders, MainViewListener mainViewListener);

	// Widget getViewWidget();

	void showNoPublishedFolders();

	void showAddButton(boolean show);

	void showProgress();

	void hideProgress();

	void clear();

	// void refresh();

	ViewType getViewType();

	void setData(List<JsFolder> folderHierarchy,
			List<JavaScriptObject> allItems, boolean canWrite, JsObj data);

	void sortColumn(String columnId, SortOrder sort);

	void selectAll();

	void selectNone();

}
