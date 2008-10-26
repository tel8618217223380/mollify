package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.service.ServiceError;

public interface FileUploadListener {
	
	void onUploadStarted();

	void onUploadFailed(ServiceError error);

	void onUploadFinished();
}
