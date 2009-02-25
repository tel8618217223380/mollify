package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.request.ResultListener;
import org.sjarvela.mollify.client.request.file.DirectoryDetailsProvider;
import org.sjarvela.mollify.client.request.file.FileDetailsProvider;

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