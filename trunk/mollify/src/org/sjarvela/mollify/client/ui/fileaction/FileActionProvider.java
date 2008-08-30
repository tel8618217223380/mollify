package org.sjarvela.mollify.client.ui.fileaction;

import org.sjarvela.mollify.client.FileAction;
import org.sjarvela.mollify.client.data.File;

public interface FileActionProvider {
	public boolean isActionAllowed(File file, FileAction action);
	public String getActionURL(File file, FileAction action);
}
