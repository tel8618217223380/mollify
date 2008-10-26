package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.FileUploadStatus;
import org.sjarvela.mollify.client.service.ServiceError;

public interface ProgressListener {

	void onProgressUpdate(FileUploadStatus status);

	void onProgressUpdateFail(ServiceError error);

}
