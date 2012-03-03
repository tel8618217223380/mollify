/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem;

import org.sjarvela.mollify.client.filesystem.js.JsFile;
import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.js.JsObj;

import com.google.gwt.core.client.JsArray;

public class SearchMatch extends JsObj {
	protected SearchMatch() {
	}

	public final FileSystemItem getItem() {
		JsObj item = this.getJsObj("item");

		if (item.getBoolean("is_file"))
			return FileSystemItem.createFrom((JsFile) item.cast());
		else
			return FileSystemItem.createFrom((JsFolder) item.cast());
	}

	public final JsArray getMatches() {
		return this.getArray("matches");
	}

}
