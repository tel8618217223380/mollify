package org.sjarvela.mollify.client.request.file;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.request.ResultListener;

public interface DirectoryDetailsProvider {

	void getDirectoryDetails(Directory directory, ResultListener resultListener);

}
