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

import org.sjarvela.mollify.client.ResourceId;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Singleton;

@Singleton
public class DefaultTextProvider implements TextProvider {
	private LanguageConstants languageConstants;
	private MessageConstants messageConstants;
	private JavaScriptObject texts;

	public DefaultTextProvider() {
		languageConstants = GWT.create(LanguageConstants.class);
		messageConstants = GWT.create(MessageConstants.class);
		getTexts();
	}

	private final native void getTexts() /*-{
		if (!$wnd.mollify || !$wnd.mollify.getTexts || typeof($wnd.mollify.getTexts()) != "object")
			@org.sjarvela.mollify.client.localization.DefaultTextProvider::invalidLocalizationError()();

		try {
			this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::texts = $wnd.mollify.getTexts();
		} catch (e) {
			alert(e);
			@org.sjarvela.mollify.client.localization.DefaultTextProvider::invalidLocalizationError()();
		}
	}-*/;

	private native final String getText(String key) /*-{
		// In Firefox, jsObject.hasOwnProperty(key) requires a primitive string
		key = String(key);
		var map = this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::texts;
		var value = map[key];
		if (value == null || !map.hasOwnProperty(key)) {
			return "#"+key;
			//this.@org.sjarvela.mollify.client.localization.DefaultTextProvider::resourceError(Ljava/lang/String;)(key);
		}
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

	public String getText(ResourceId id) {
		return getText(id.name());
	}

//	public LanguageConstants getStrings() {
//		return languageConstants;
//	}

	public MessageConstants getMessages() {
		return messageConstants;
	}

	public String getSizeText(long bytes) {
		if (bytes < 1024l) {
			return (bytes == 1 ? getMessages().sizeOneByte() : getMessages()
					.sizeInBytes(bytes));
		}

		if (bytes < (1024l * 1024l)) {
			double kilobytes = (double) bytes / (double) 1024;
			return (kilobytes == 1 ? getMessages().sizeOneKilobyte()
					: getMessages().sizeInKilobytes(kilobytes));
		}

		if (bytes < (1024l * 1024l * 1024l)) {
			double megabytes = (double) bytes / (double) (1024 * 1024);
			return getMessages().sizeInMegabytes(megabytes);
		}

		double gigabytes = (double) bytes / (double) (1024 * 1024 * 1024);
		return getMessages().sizeInGigabytes(gigabytes);
	}
}
