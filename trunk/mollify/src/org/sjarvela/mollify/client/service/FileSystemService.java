package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.provider.DirectoryDetailsProvider;
import org.sjarvela.mollify.client.filesystem.provider.FileDetailsProvider;
import org.sjarvela.mollify.client.service.request.ResultListener;

public interface FileSystemService extends FileDetailsProvider,
		DirectoryDetailsProvider {

	void getDirectories(Directory parent, ResultListener listener);

	void getRootDirectories(ResultListener listener);

	void getDirectoriesAndFiles(String folder, ResultListener listener);

	void rename(FileSystemItem item, String newName, ResultListener listener);

	void delete(FileSystemItem item, ResultListener listener);

	void createDirectory(Directory parentFolder, String folderName,
			ResultListener resultListener);

	String getDownloadUrl(File file);
}