package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.ResultListener;

public interface FileActionHandler {

	void addRenameListener(ResultListener listener);

	void addDeleteListener(ResultListener listener);

	void onFileAction(File file, FileAction action);

}
