package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.data.UploadStatus;

public interface ProgressListener {

	void onUpdateProgress(UploadStatus status);

}
