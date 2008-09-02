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

import com.google.gwt.core.client.GWT;

public class Localizator {
	private static Localizator instance = null;

	public static Localizator getInstance() {
		if (instance == null) {
			instance = new Localizator();
		}
		return instance;
	}

	private LanguageConstants languageConstants;
	private MessageConstants messageConstants;

	public Localizator() {
		languageConstants = GWT.create(LanguageConstants.class);
		messageConstants = GWT.create(MessageConstants.class);
	}

	public LanguageConstants getStrings() {
		return languageConstants;
	}

	public MessageConstants getMessages() {
		return messageConstants;
	}
}
