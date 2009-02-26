package org.sjarvela.mollify.client.filesystem.provider;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.service.request.ResultListener;

public interface DirectoryDetailsProvider {

	void getDirectoryDetails(Directory directory, ResultListener resultListener);

}
