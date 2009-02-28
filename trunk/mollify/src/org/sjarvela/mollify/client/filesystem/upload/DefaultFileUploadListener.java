package org.sjarvela.mollify.client.filesystem.upload;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileUploadStatus;
import org.sjarvela.mollify.client.localization.DefaultTextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.ProgressDisplayer;

import com.google.gwt.core.client.JavaScriptObject;

public class DefaultFileUploadListener implements FileUploadListener {
	private final DefaultTextProvider localizator;
	private final boolean isProgressEnabled;
	private final FileUploadService service;
	private final DialogManager dialogManager;

	private FileUploadMonitor uploadMonitor;
	private ProgressDisplayer uploadListener;
	private final ResultListener listener;

	public DefaultFileUploadListener(FileUploadService service,
			boolean isProgressEnabled, DialogManager dialogManager,
			DefaultTextProvider localizator, ResultListener listener) {
		this.service = service;
		this.isProgressEnabled = isProgressEnabled;
		this.dialogManager = dialogManager;
		this.localizator = localizator;
		this.listener = listener;
	}

	public void onUploadStarted(String uploadId, List<String> filenames) {
		String info = filenames.size() == 1 ? filenames.get(0) : localizator
				.getMessages().uploadingNFilesInfo(filenames.size());

		uploadListener = dialogManager.openProgressDialog(localizator
				.getStrings().fileUploadProgressTitle(), false);
		uploadListener.setInfo(info);
		uploadListener.setDetails(localizator.getStrings()
				.fileUploadProgressPleaseWait());

		if (!isProgressEnabled)
			return;

		uploadMonitor = new FileUploadMonitor(uploadId,
				new FileUploadProgressListener() {
					public void onProgressUpdate(FileUploadStatus status) {
						int percentage = (int) status.getUploadedPercentage();
						uploadListener.setProgressBarVisible(true);
						uploadListener.setProgress(percentage);
						uploadListener.setDetails(String.valueOf(percentage)
								+ "%");
					}

					public void onProgressUpdateFail(ServiceError error) {
						uploadListener.setProgress(0);
						uploadMonitor.stop();
					}
				}, service);

		uploadMonitor.start();
	}

	public void onUploadFinished(JavaScriptObject result) {
		stopUploaders();
		listener.onSuccess(result);
	}

	public void onUploadFailed(ServiceError error) {
		stopUploaders();
		listener.onFail(error);
	}

	private void stopUploaders() {
		uploadListener.setProgress(100);
		uploadListener.setDetails("");
		uploadListener.onFinished();

		if (uploadMonitor != null)
			uploadMonitor.stop();
	}

}
