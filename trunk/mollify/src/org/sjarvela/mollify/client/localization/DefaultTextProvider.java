/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.localization;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.formatting.Formatting;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.inject.Singleton;

@Singleton
public class DefaultTextProvider implements TextProvider {
	private static Logger logger = Logger.getLogger(DefaultTextProvider.class
			.getName());

	private JavaScriptObject texts;
	private String locale = "";
	private NumberFormat sizeFormatter;

	public DefaultTextProvider() {
		logger.log(Level.INFO, "Initializing texts");
		initTexts();
		Formatting.initialize(this);
		sizeFormatter = Formatting
				.getNumberFormat(getText(Texts.fileSizeFormat));
		logger.log(Level.INFO, "Locale: " + locale);
	}

	private final native void initTexts() /*-{
		this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::texts = {}

		if (!$wnd.mollify || !$wnd.mollify.texts || !$wnd.mollify.texts.values || typeof($wnd.mollify.texts.values) != "object") {
			@org.sjarvela.mollify.client.localization.DefaultTextProvider::invalidLocalizationError()();
			return;
		}

		try {
			this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::locale = $wnd.mollify.texts.locale;
			this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::texts = $wnd.mollify.texts.values;
		} catch (e) {
			@org.sjarvela.mollify.client.localization.DefaultTextProvider::invalidLocalizationError()();
		}
	}-*/;

	private native final String get(String key) /*-{
		// In Firefox, jsObject.hasOwnProperty(key) requires a primitive string
		key = String(key);
		var map = this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::texts;
		var value = map != null ? map[key] : null;

		if (value == null || !map.hasOwnProperty(key))
			return "[" + this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::locale + ":" + key + "]";

		return String(value);
	}-*/;

	static void invalidLocalizationError() {
		logger.log(Level.SEVERE, "Invalid or missing localization file");
	}

	@Override
	public String getLocale() {
		return locale;
	}

	@Override
	public String getText(ResourceId id) {
		return get(id.name());
	}

	@Override
	public String getText(String id) {
		return get(id);
	}

	@Override
	public String getText(ResourceId id, String... params) {
		return replaceParams(getText(id), params);
	}

	@Override
	public String getText(String id, String... params) {
		return replaceParams(getText(id), params);
	}

	private String replaceParams(String v, String... params) {
		for (int i = 0; i < params.length; i++)
			v = v.replaceAll("\\{" + i + "\\}", params[i]);
		return v;
	}

	// TODO into separate formatter
	@Override
	public String getSizeText(long bytes) {
		if (bytes < 1024l) {
			return (bytes == 1 ? getText(Texts.sizeOneByte) : getText(
					Texts.sizeInBytes, String.valueOf(bytes)));
		}

		if (bytes < (1024l * 1024l)) {
			double kilobytes = (double) bytes / (double) 1024;
			return (kilobytes == 1 ? getText(Texts.sizeOneKilobyte) : getText(
					Texts.sizeInKilobytes, sizeFormatter.format(kilobytes)));
		}

		if (bytes < (1024l * 1024l * 1024l)) {
			double megabytes = (double) bytes / (double) (1024 * 1024);
			return getText(Texts.sizeInMegabytes,
					sizeFormatter.format(megabytes));
		}

		double gigabytes = (double) bytes / (double) (1024 * 1024 * 1024);
		return getText(Texts.sizeInGigabytes, sizeFormatter.format(gigabytes));
	}
}
