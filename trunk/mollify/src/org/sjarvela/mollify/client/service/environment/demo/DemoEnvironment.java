/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.demo;

import org.sjarvela.mollify.client.UrlResolver;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.FileUploadService;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.session.ClientSettings;

import com.allen_sauer.gwt.log.client.Log;

public class DemoEnvironment implements ServiceEnvironment {
	static final String MOLLIFY_PACKAGE_URL = "http://www.jaervelae.com/mollify/download/latest.php";
	private static final String PARAM_MULTI_USER = "multi-user";

	private DemoSessionService sessionService;
	private DemoData data;
	private FileSystemService fileSystemService;
	private FileUploadService demoFileUploadHandler;
	private ConfigurationService settingsHandler;

	public void initialize(UrlResolver urlProvider, ClientSettings settings) {
		Log.info("Mollify Demo");

		this.data = new DemoData(settings.getBool(PARAM_MULTI_USER, true));
		this.sessionService = new DemoSessionService(data);
		this.fileSystemService = new DemoFileService(data);
		this.demoFileUploadHandler = new DemoFileUploadHandler();
		this.settingsHandler = new DemoConfigurationService(data);
	}

	public SessionService getSessionService() {
		return sessionService;
	}

	public FileSystemService getFileSystemService() {
		return fileSystemService;
	}

	public FileUploadService getFileUploadService() {
		return demoFileUploadHandler;
	}

	public ConfigurationService getConfigurationService() {
		return settingsHandler;
	}

}
