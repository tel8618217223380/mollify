package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.service.ResultListener;

public interface DirectoryActionHandler {

	void onDirectoryAction(Directory directory, FileSystemAction action);

	void addRenameListener(ResultListener listener);

	void addDeleteListener(ResultListener listener);

}
