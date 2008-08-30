package org.sjarvela.mollify.client.ui.filelist;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;

public interface SimpleFileListListener {
	void onFileRowClicked(File file, Column column);

	void onDirectoryRowClicked(Directory directory, Column column);

	void onDirectoryUpRowClicked(Column column);
}
