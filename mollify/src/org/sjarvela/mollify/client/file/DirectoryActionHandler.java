package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.Directory;

public interface DirectoryActionHandler {

	void onDirectoryAction(Directory directory, FileSystemAction action);

}
