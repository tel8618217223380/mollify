/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.permissions;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.session.file.FilePermission;
import org.sjarvela.mollify.client.ui.Formatter;

public class FilePermissionModeFormatter implements Formatter<FilePermission> {
	private final TextProvider textProvider;

	public FilePermissionModeFormatter(TextProvider textProvider) {
		this.textProvider = textProvider;
	}

	public String format(FilePermission mode) {
		if (mode == null)
			return textProvider.getStrings().itemPermissionEditorNoPermission();
		return mode.getLocalizedText(textProvider);
	}

}
