package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.ResultListener;

public interface DeleteHandler {
	public void onDelete(File file, ResultListener listener);
}
