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

import java.util.List;

import org.sjarvela.mollify.client.js.JsObj;

public class SearchResult extends JsObj {
	protected SearchResult() {
	}

	public final int getMatchCount() {
		return this.getInt("count");
	}

	public final List<String> getMatches() {
		return this.getJsObj("matches").getKeys();
	}

	public final SearchMatch getMatch(String id) {
		JsObj matches = this.getJsObj("matches");
		if (matches.hasValue("id")) return null;
		return matches.getJsObj(id).cast();
	}
}
