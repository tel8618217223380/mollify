package org.sjarvela.mollify.client;

import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.ResultListener;

public interface FileDetailsProvider {
	public void getFileDetails(File file, ResultListener listener);
}
