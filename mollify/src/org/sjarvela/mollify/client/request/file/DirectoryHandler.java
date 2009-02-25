package org.sjarvela.mollify.client.request.file;

import org.sjarvela.mollify.client.filesystem.Directory;

public interface DirectoryHandler {

	void createDirectory(Directory parentFolder, String folderName);

}
