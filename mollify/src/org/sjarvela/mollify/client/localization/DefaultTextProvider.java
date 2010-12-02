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

import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.formatting.Formatting;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.constants.NumberConstants;
import com.google.inject.Singleton;

@Singleton
public class DefaultTextProvider implements TextProvider {
	private static Logger logger = Logger.getLogger(DefaultTextProvider.class
			.getName());

	public static NumberConstants NumberConstants;

	private JavaScriptObject texts;
	private String locale;
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
		if (!$wnd.mollify || !$wnd.mollify.getTexts || typeof($wnd.mollify.getTexts()) != "object")
			@org.sjarvela.mollify.client.localization.DefaultTextProvider::invalidLocalizationError()();

		try {
			this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::locale = $wnd.mollify.getLocale();
			this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::texts = $wnd.mollify.getTexts();
		} catch (e) {
			@org.sjarvela.mollify.client.localization.DefaultTextProvider::invalidLocalizationError()();
		}
	}-*/;

	private native final String getText(String key) /*-{
		// In Firefox, jsObject.hasOwnProperty(key) requires a primitive string
		key = String(key);
		var map = this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::texts;
		var value = map[key];

		if (value == null || !map.hasOwnProperty(key))
			return "["+key+"]";

		return String(value);
	}-*/;

	static void invalidLocalizationError() {
		throw new MissingResourceException(
				"Missing or invalid localization file", null, "");
	}

	void resourceError(String key) {
		throw new MissingResourceException("Cannot find text: " + key,
				this.toString(), key);
	}

	@Override
	public String getText(ResourceId id) {
		return getText(id.name());
	}

	@Override
	public String getText(ResourceId id, String... params) {
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
