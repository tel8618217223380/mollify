package org.sjarvela.mollify.client;

import org.sjarvela.mollify.client.data.Directory;

public interface DirectoryController {
	public void initialize();
	public void changeDirectory(int level, Directory directory);
	public void refresh();
	public void moveToParentDirectory();
}
