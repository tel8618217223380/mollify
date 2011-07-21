/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview.impl;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.common.grid.GridListener;
import org.sjarvela.mollify.client.ui.common.grid.SelectController;
import org.sjarvela.mollify.client.ui.common.grid.SelectionMode;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;

import com.google.gwt.user.client.ui.Widget;

public interface FileListWidget {

	Widget getWidget();

	void refresh();

	void removeAllRows();

	void setSelectionMode(SelectionMode selectionMode);

	void setSelectController(SelectController controller);

	void selectAll();

	void selectNone();

	void addListener(GridListener listener);

	void setContent(List<FileSystemItem> items);

	// void setComparator(GridComparator<FileSystemItem> createComparator);

	void sortColumn(String columnId, SortOrder sort);

}
