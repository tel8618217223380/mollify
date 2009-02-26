package org.sjarvela.mollify.client.service.environment;

import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.session.ClientSettings;

public interface ServiceEnvironment {

	void initialize(ClientSettings settings);

	SessionService getSessionService();

	FileSystemService getFileSystemService();

	FileUploadService getFileUploadHandler();
}
