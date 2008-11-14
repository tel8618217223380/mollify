/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.file.impl;

import org.sjarvela.mollify.client.data.FileSystemItem;
import org.sjarvela.mollify.client.file.FileAction;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.service.MollifyService;

public class FileActionProviderImpl implements FileActionProvider {
	private MollifyService service;

	public FileActionProviderImpl(MollifyService service) {
		super();
		this.service = service;
	}

	public String getActionURL(FileSystemItem item, FileAction action) {
		return service.getFileActionUrl(item, action);
	}

	public boolean isActionAllowed(FileSystemItem item, FileAction action) {
		// TODO users rights
		return true;
	}
}
