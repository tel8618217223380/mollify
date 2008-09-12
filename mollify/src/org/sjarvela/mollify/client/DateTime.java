package org.sjarvela.mollify.client;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateTime {
	private static final String INTERNAL_DATETIME_FORMAT = "yMdHms";

	private static DateTime instance = null;

	public static DateTime getInstance() {
		if (instance == null) {
			instance = new DateTime();
		}
		return instance;
	}

	private DateTimeFormat internalDateTimeFormat;

	private DateTime() {
		this.internalDateTimeFormat = com.google.gwt.i18n.client.DateTimeFormat
				.getFormat(INTERNAL_DATETIME_FORMAT);
	}

	public DateTimeFormat getInternalFormat() {
		return internalDateTimeFormat;
	}
}
