/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.filemanager;

import org.sjarvela.mollify.client.FileHandler;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.FileUploadResultHandler;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ResultListener;
import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.JavaScriptObject;

public class FileHandlerImpl implements FileHandler {
	private MollifyService service;
	private MainView view;
	private MainViewPresenter presenter;

	public FileHandlerImpl(MollifyService service, MainView view,
			MainViewPresenter presenter) {
		super();
		this.presenter = presenter;
		this.service = service;
		this.view = view;
	}

	public void onRename(File file, String newName) {
		service.renameFile(file, newName, new ResultListener() {
			public void onFail(ServiceError error) {
				view.showError(error);
				presenter.refresh();
			}

			public void onSuccess(JavaScriptObject jso) {
				presenter.refresh();
			}

		});
	}

	public void onDelete(File file) {
		service.deleteFile(file, new ResultListener() {
			public void onFail(ServiceError error) {
				view.showError(error);
				presenter.refresh();
			}

			public void onSuccess(JavaScriptObject jso) {
				presenter.refresh();
			}
		});
	}

	public FileUploadResultHandler getFileUploadResultHandler() {
		return new FileUploadResultHandler(new ResultListener() {
			public void onFail(ServiceError error) {
				view.showError(error);
				presenter.refresh();
			}

			public void onSuccess(JavaScriptObject jso) {
				presenter.refresh();
			}
		});
	}

}
