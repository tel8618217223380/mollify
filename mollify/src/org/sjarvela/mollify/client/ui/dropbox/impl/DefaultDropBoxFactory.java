/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dropbox.impl;

import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;
import org.sjarvela.mollify.client.ui.dropbox.DropBoxFactory;

import com.google.inject.Singleton;

@Singleton
public class DefaultDropBoxFactory implements DropBoxFactory {

	@Override
	public DropBox createDropBox(FileSystemActionHandler fileSystemActionHandler) {
		DropBoxView view = new DropBoxView();
		DropBoxPresenter presenter = new DropBoxPresenter(view,
				fileSystemActionHandler);
		return new DropBoxGlue(view, presenter);
	}

}
