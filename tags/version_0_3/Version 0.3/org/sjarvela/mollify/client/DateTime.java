/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateTime {
	private static final String INTERNAL_DATETIME_FORMAT = "yyyyMMddHHmmss";

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
