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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;
import org.sjarvela.mollify.client.ui.mainview.impl.DefaultMainView.ViewType;

public interface MainView {

	void setUsername(String user);

	// Widget getViewWidget();

	void hideButtons();

	void showAddButton(boolean show);
	
	void showProgress();

	void hideProgress();
	
	void clear();

	void refresh();

	ViewType getViewType();

	void setData(List<FileSystemItem> allItems, JsObj data);

	void sortColumn(String columnId, SortOrder sort);

	void selectAll();

	void selectNone();



}
