package org.sjarvela.mollify.client.file.impl;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.file.FileUploadController;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.FileUploadListener;
import org.sjarvela.mollify.client.service.FileUploadResultHandler;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;

public class FileUploadHandlerImpl implements FileUploadHandler {
	private final MollifyService service;

	private List<FileUploadListener> listeners = new ArrayList<FileUploadListener>();

	public FileUploadHandlerImpl(MollifyService service) {
		this.service = service;
	}

	public void addListener(FileUploadListener listener) {
		listeners.add(listener);
	}

	public String getFileUploadId() {
		return service.getNewUploadId();
	}

	public void getUploadProgress(String id, ResultListener listener) {
		service.getUploadProgress(id, listener);
	}

	public FormHandler getUploadFormHandler(
			final FileUploadController controller) {
		return new FormHandler() {

			public void onSubmit(FormSubmitEvent event) {
				if (!controller.onUploadStarted())
					event.setCancelled(true);
				else
					for (FileUploadListener listener : listeners)
						listener.onUploadStarted();
			}

			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				controller.onUploadFinished();
				getFileUploadResultHandler().handleResult(event.getResults());
			}
		};
	}

	private FileUploadResultHandler getFileUploadResultHandler() {
		return new FileUploadResultHandler(new ResultListener() {
			public void onFail(ServiceError error) {
				for (FileUploadListener listener : listeners)
					listener.onUploadFailed(error);
			}

			public void onSuccess(JavaScriptObject... result) {
				for (FileUploadListener listener : listeners)
					listener.onUploadFinished();
			}
		});
	}
}
