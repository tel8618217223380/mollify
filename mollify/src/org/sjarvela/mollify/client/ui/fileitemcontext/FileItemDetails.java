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

import com.google.gwt.core.client.JavaScriptObject;

public class FileItemDetails extends JavaScriptObject {
	public static FileItemDetails create() {
		FileItemDetails c = FileItemDetails.createObject()
				.cast();
		c.init();
		return c;
	}

	private final native void init() /*-{
		this.sections = [];
	}-*/;

	protected FileItemDetails() {
	}
	
	public final native void addSection(String pluginId, JavaScriptObject section) /*-{
		this.sections.push({"section": section, "plugin":pluginId});
	}-*/;
}
