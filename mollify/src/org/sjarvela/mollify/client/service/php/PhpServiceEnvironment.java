/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.php;

import org.sjarvela.mollify.client.request.file.FileUploadHandler;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceEnvironment;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.session.ClientSettings;

public class PhpServiceEnvironment implements ServiceEnvironment {
	private static final String PARAM_SERVICE_PATH = "service-path";
	private PhpService service;
	private PhpFileService fileSystemService;
	private PhpFileUploadHandler uploadHandler;
	private PhpSessionService sessionService;

	public void initialize(ClientSettings settings) {
		service = new PhpService(settings.getString(PARAM_SERVICE_PATH));
		sessionService = new PhpSessionService(service);
		fileSystemService = new PhpFileService(service);
		uploadHandler = new PhpFileUploadHandler(service);
	}

	public SessionService getSessionService() {
		return sessionService;
	}

	public FileSystemService getFileSystemService() {
		return fileSystemService;
	}

	public FileUploadHandler getFileUploadHandler() {
		return uploadHandler;
	}

}
