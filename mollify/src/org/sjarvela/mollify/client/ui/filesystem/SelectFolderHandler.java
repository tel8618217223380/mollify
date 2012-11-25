package org.sjarvela.mollify.client.ui.filesystem;

import org.sjarvela.mollify.client.filesystem.js.JsFolder;

public interface SelectFolderHandler {

	void onSelect(JsFolder selected);

	boolean canSelect(JsFolder folder);

}
