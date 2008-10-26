package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.service.ServiceError;

public interface FileUploadListener {
	
	void onUploadStarted();

	void onUploadFailed(ServiceError error);

	void onUploadFinished();
}
