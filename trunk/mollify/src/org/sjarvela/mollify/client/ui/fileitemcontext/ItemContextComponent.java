/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext;

import org.sjarvela.mollify.client.filesystem.FileDetails;

import com.google.gwt.user.client.ui.Widget;

public interface ItemContextComponent {
	String getHtml();

	void onInit(Widget content, FileDetails details);

	void onDispose();
}
