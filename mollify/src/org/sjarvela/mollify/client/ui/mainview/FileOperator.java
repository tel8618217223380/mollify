package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.file.DirectoryProvider;
import org.sjarvela.mollify.client.file.FileDetailsProvider;
import org.sjarvela.mollify.client.file.FileOperationHandler;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class FileOperator implements DirectoryProvider, FileDetailsProvider,
		FileOperationHandler {
	private final MollifyService service;
	private final FileViewModel model;

	public FileOperator(MollifyService service, FileViewModel model) {
		this.service = service;
		this.model = model;
	}

	public void getDirectories(Directory parent, ResultListener listener) {
		// if there is no parent, show root list
		if (parent.isEmpty()) {
			listener.onSuccess(model.getRootDirectories());
			return;
		}

		// no need to retrieve current view directories, they are already
		// retrieved
		if (parent.equals(model.getDirectoryModel().getCurrentFolder())) {
			listener.onSuccess(model.getDirectories());
			return;
		}

		this.service.getDirectories(listener, parent.getId());
	}

	public void getRootDirectories(ResultListener listener) {
		this.service.getRootDirectories(listener);
	}

	public void getDirectoriesAndFiles(final String folder,
			final ResultListener listener) {
		this.service.getDirectories(new ResultListener() {

			public void onFail(ServiceError error) {
				listener.onFail(error);
			}

			public void onSuccess(JavaScriptObject... result) {
				final JsArray<Directory> directories = result[0].cast();
				service.getFiles(new ResultListener() {

					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					public void onSuccess(JavaScriptObject... result) {
						JsArray<File> files = result[0].cast();
						listener.onSuccess(directories, files);
					}
				}, folder);
			}

		}, folder);
	}

	public void getFileDetails(File file, ResultListener resultListener) {
		service.getFileDetails(file, resultListener);
	}

	public void onRename(File file, String newName, ResultListener listener) {
		service.renameFile(file, newName, listener);
	}

	public void onDelete(File file, ResultListener listener) {
		service.deleteFile(file, listener);
	}

}
