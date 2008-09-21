/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.file;

import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.service.MollifyService;

public class FileActionProviderImpl implements FileActionProvider {
	private MollifyService service;

	public FileActionProviderImpl(MollifyService service) {
		super();
		this.service = service;
	}

	public String getActionURL(File file, FileAction action) {
		return service.getFileActionUrl(file, action);
	}

	public String getActionURL(Directory dir, FileAction action) {
		return service.getDirectoryActionUrl(dir, action);
	}

	public boolean isActionAllowed(File file, FileAction action) {
		// TODO users rights
		return true;
	}
}
