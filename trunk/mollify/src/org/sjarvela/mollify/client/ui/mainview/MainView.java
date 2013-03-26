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

import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;
import org.sjarvela.mollify.client.js.JsObj;

import com.google.gwt.core.client.JavaScriptObject;

public interface MainView {

	void init(List<JsRootFolder> rootFolders, MainViewListener mainViewListener);

	JavaScriptObject getDataRequest(JsFolder folder);

	void showAllRoots();

	void showNoRoots();

	void showProgress();

	void hideProgress();

	void clear();

	void onChangePassword();

	void setFolder(List<JsFolder> folderHierarchy, boolean canWrite);

	void setData(List<JavaScriptObject> allItems, JsObj data);

	void onOpenAdminUtil(String url);

}
