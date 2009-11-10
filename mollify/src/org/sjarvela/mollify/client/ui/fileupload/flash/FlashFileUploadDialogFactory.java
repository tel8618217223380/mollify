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

import org.sjarvela.mollify.client.UrlResolver;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.environment.demo.DemoFileUploadHandler;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.ClientSettings;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;

public class FlashFileUploadDialogFactory implements FileUploadDialogFactory {
	static final String PARAM_FLASH_UPLOADER_SRC = "flash-uploader-src";
	static final String PARAM_FLASH_UPLOADER_STYLE = "flash-uploader-style";
	static final String FLASH_FILE_NAME = "swfupload.swf";

	private final TextProvider textProvider;
	private final FileUploadService service;
	private final SessionProvider sessionProvider;
	private final String uploaderSrc;
	private final String uploaderStyle;
	private final UrlResolver urlProvider;

	public FlashFileUploadDialogFactory(TextProvider textProvider,
			UrlResolver urlProvider, FileUploadService fileUploadService,
			SessionProvider sessionProvider, ClientSettings settings) {
		this.textProvider = textProvider;
		this.urlProvider = urlProvider;
		this.service = fileUploadService;
		this.sessionProvider = sessionProvider;

		this.uploaderSrc = getSourceUrl(settings
				.getString(PARAM_FLASH_UPLOADER_SRC));
		this.uploaderStyle = settings.getString(PARAM_FLASH_UPLOADER_STYLE);
	}

	private String getSourceUrl(String url) {
		if (url != null && url.length() > 0)
			return urlProvider.getHostPageUrl(url, false);
		return urlProvider.getModuleUrl(FLASH_FILE_NAME, false);
	}

	public void openFileUploadDialog(Directory directory,
			ResultListener listener) {

		SessionInfo session = sessionProvider.getSession();
		ActionDelegator actionDelegator = new ActionDelegator();
		FlashFileUploadDialog dialog = new FlashFileUploadDialog(textProvider,
				actionDelegator, uploaderStyle);
		FlashFileUploadPresenter presenter = new FlashFileUploadPresenter(
				session, service, listener, uploaderSrc, directory, dialog,
				textProvider);
		if (service instanceof DemoFileUploadHandler)
			presenter.setDemoMode();
		new FlashFileUploadGlue(dialog, presenter, actionDelegator);
	}

}
