/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.php;

import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.UrlResolver;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.session.ClientSettings;

public class PhpServiceEnvironment implements ServiceEnvironment {
	private static final String PARAM_SERVICE_PATH = "service-path";
	private static final String PARAM_TIMEOUT = "request-timeout";
	private static final int DEFAULT_REQUEST_TIMEOUT = 5;

	private PhpService service;
	private PhpFileService fileSystemService;
	private PhpFileUploadService uploadHandler;
	private PhpSessionService sessionService;
	private PhpConfigurationService settingsHandler;

	public void initialize(UrlResolver urlResolver, ClientSettings settings) {
		service = new PhpService(urlResolver, settings.getString(PARAM_SERVICE_PATH),
				settings.getInt(PARAM_TIMEOUT, DEFAULT_REQUEST_TIMEOUT));
		sessionService = new PhpSessionService(service);
		fileSystemService = new PhpFileService(service);
		uploadHandler = new PhpFileUploadService(service);
		settingsHandler = new PhpConfigurationService(service);
	}

	public SessionService getSessionService() {
		return sessionService;
	}

	public FileSystemService getFileSystemService() {
		return fileSystemService;
	}

	public FileUploadService getFileUploadService() {
		return uploadHandler;
	}

	public ConfigurationService getConfigurationService() {
		return settingsHandler;
	}

}
