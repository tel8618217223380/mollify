/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin;

import org.sjarvela.mollify.client.localization.TextProvider;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeTextProvider {

	private final TextProvider textProvider;

	public NativeTextProvider(TextProvider textProvider) {
		this.textProvider = textProvider;
	}

	public JavaScriptObject asJs() {
		return createJs(this, textProvider.getLocale());
	}

	private native JavaScriptObject createJs(NativeTextProvider tp, String locale) /*-{
		var o = {};

		o.get = function(s) {
			return tp.@org.sjarvela.mollify.client.plugin.NativeTextProvider::getText(Ljava/lang/String;)(s);
		}
		
		o.locale = locale;

		return o;
	}-*/;

	public String getText(String id) {
		return textProvider.getText(id);
	}
}
