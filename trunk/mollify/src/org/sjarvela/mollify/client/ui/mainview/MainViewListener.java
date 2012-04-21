package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;

public interface MainViewListener {
	void onViewLoaded();

	void onRootFolderSelected(JsRootFolder root);
}
