package org.sjarvela.mollify.client;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.service.ResultListener;

public interface DirectoryProvider {
	public void getDirectories(Directory parent, ResultListener listener);
}
