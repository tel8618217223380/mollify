/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.pluploader;

import org.sjarvela.mollify.client.filesystem.FileSystemItemProvider;
import org.sjarvela.mollify.client.filesystem.Folder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.UrlResolver;
import org.sjarvela.mollify.client.service.environment.demo.DemoFileUploadHandler;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;

public class PluploaderDialogFactory implements FileUploadDialogFactory {
	private final TextProvider textProvider;
	private final UrlResolver urlResolver;
	private final SessionProvider sessionProvider;
	private final FileUploadService service;
	private final ClientSettings settings;
	private final DialogManager dialogManager;
	private final FileSystemItemProvider fileSystemItemProvider;

	public PluploaderDialogFactory(TextProvider textProvider,
			UrlResolver urlResolver, FileUploadService fileUploadService,
			SessionProvider sessionProvider, DialogManager dialogManager,
			ClientSettings settings,
			FileSystemItemProvider fileSystemItemProvider) {
		this.textProvider = textProvider;
		this.urlResolver = urlResolver;
		this.service = fileUploadService;
		this.sessionProvider = sessionProvider;
		this.dialogManager = dialogManager;
		this.settings = settings;
		this.fileSystemItemProvider = fileSystemItemProvider;
	}

	@Override
	public void openFileUploadDialog(Folder directory, ResultListener listener) {
		if (PluploaderDialog.isOpen())
			return;

		ActionDelegator actionDelegator = new ActionDelegator();
		PluploaderDialog dialog = new PluploaderDialog(textProvider,
				actionDelegator);
		PluploaderPresenter presenter = new PluploaderPresenter(
				sessionProvider.getSession(), service, urlResolver, listener,
				directory, dialog, dialogManager, textProvider, settings,
				fileSystemItemProvider);
		if (service instanceof DemoFileUploadHandler)
			presenter.setDemoMode();
		new PluploaderGlue(dialog, presenter, actionDelegator);
	}

}
