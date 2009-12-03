package org.sjarvela.mollify.client.ui.configuration.folders;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.filesystem.FolderInfo;

public interface FolderHandler {

	void addFolder(String name, String path, Callback successCallback);

	void editFolder(FolderInfo folder, String name, String path,
			Callback successCallback);

}
