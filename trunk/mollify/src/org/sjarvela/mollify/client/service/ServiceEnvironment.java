package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.request.file.FileUploadHandler;
import org.sjarvela.mollify.client.session.ClientSettings;

public interface ServiceEnvironment {

	void initialize(ClientSettings settings);

	SessionService getSessionService();

	FileSystemService getFileSystemService();

	FileUploadHandler getFileUploadHandler();
}
