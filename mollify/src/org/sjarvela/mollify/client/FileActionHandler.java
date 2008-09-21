package org.sjarvela.mollify.client;

import org.sjarvela.mollify.client.data.File;

public interface FileActionHandler {
	public void onFileAction(File file, FileAction action);
}
