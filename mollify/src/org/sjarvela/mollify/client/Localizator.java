package org.sjarvela.mollify.client;

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
