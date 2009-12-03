package org.sjarvela.mollify.client.filesystem.handler;

import org.sjarvela.mollify.client.filesystem.Folder;

public interface DirectoryHandler {

	void createDirectory(Folder parentFolder, String folderName);

}
