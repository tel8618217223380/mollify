package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.service.ResultListener;

public interface DirectoryHandler {

	void onCreate(Directory parentFolder, String folderName,
			ResultListener resultListener);

}
