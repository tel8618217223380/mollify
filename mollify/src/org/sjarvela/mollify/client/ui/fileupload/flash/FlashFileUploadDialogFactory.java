/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.flash;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;

public class FlashFileUploadDialogFactory implements FileUploadDialogFactory {
	private final TextProvider textProvider;
	private final FileUploadService service;
	private final SessionProvider sessionProvider;
	private final String uploaderSrc;

	public FlashFileUploadDialogFactory(TextProvider textProvider,
			FileUploadService fileUploadService,
			SessionProvider sessionProvider, String uploaderSrc) {
		this.textProvider = textProvider;
		this.service = fileUploadService;
		this.sessionProvider = sessionProvider;
		this.uploaderSrc = uploaderSrc;
	}

	public void openFileUploadDialog(Directory directory, ResultListener listener) {
		SessionInfo session = sessionProvider.getSession();
		ActionDelegator actionDelegator = new ActionDelegator();
		FlashFileUploadDialog dialog = new FlashFileUploadDialog(textProvider,
				actionDelegator);
		FlashFileUploadPresenter presenter = new FlashFileUploadPresenter(
				session, service, listener, uploaderSrc, directory, dialog);
		new FlashFileUploadGlue(dialog, presenter, actionDelegator);
	}

}
