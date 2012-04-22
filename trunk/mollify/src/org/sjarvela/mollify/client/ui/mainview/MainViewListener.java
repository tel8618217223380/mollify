package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.filesystem.js.JsFolder;

public interface MainViewListener {
	void onViewLoaded();

	void onSubFolderSelected(JsFolder f);

	void onFolderSelected(int level, JsFolder f);
}
