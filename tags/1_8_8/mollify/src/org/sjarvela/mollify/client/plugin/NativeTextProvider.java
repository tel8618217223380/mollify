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
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.util.DateTime;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.DateTimeFormat;

public class NativeTextProvider {
	private final TextProvider textProvider;
	private final DateTimeFormat dateTimeFormat;

	public NativeTextProvider(TextProvider textProvider) {
		this.textProvider = textProvider;
		this.dateTimeFormat = com.google.gwt.i18n.client.DateTimeFormat
				.getFormat(textProvider.getText(Texts.shortDateTimeFormat));
	}

	public JavaScriptObject asJs() {
		return createJs(this, textProvider.getLocale());
	}

	private native JavaScriptObject createJs(NativeTextProvider tp,
			String locale) /*-{
		var o = {};

		o.get = function(s, p) {
			if (!p || !$wnd.isArray(p))
				return tp.@org.sjarvela.mollify.client.plugin.NativeTextProvider::getText(Ljava/lang/String;)(s);
			return tp.@org.sjarvela.mollify.client.plugin.NativeTextProvider::getText(Ljava/lang/String;Lcom/google/gwt/core/client/JsArrayString;)(s, p);
		}

		o.formatSize = function(s) {
			return tp.@org.sjarvela.mollify.client.plugin.NativeTextProvider::formatSize(I)(s*1);
		}

		o.formatInternalTime = function(s) {
			return tp.@org.sjarvela.mollify.client.plugin.NativeTextProvider::formatInternalTime(Ljava/lang/String;)(''+s);
		}

		o.locale = locale;

		return o;
	}-*/;

	public String getText(String id) {
		return textProvider.getText(id);
	}

	public String getText(String id, JsArrayString params) {
		return textProvider.getText(id,
				JsUtil.asList(params).toArray(new String[0]));
	}

	public String formatInternalTime(String timeString) {
		return dateTimeFormat.format(DateTime.getInstance().getInternalFormat()
				.parse(timeString));
	}

	public String formatSize(int s) {
		return textProvider.getSizeText(s);
	}
}
