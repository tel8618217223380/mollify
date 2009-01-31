package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.service.ResultListener;

public interface DirectoryDetailsProvider {

	void getDirectoryDetails(Directory directory, ResultListener resultListener);

}
