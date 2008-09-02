/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.localization.Localizator;

public enum ServiceError {
	NO_RESPONSE, INVALID_RESPONSE, DATA_TYPE_MISMATCH;

	public String getMessage(Localizator localizator) {
		switch (this) {
		case NO_RESPONSE:
			return localizator.getStrings().errorMessageNoResponse();
		case INVALID_RESPONSE:
			return localizator.getStrings().errorMessageInvalidResponse();
		case DATA_TYPE_MISMATCH:
			return localizator.getStrings().errorMessageDataTypeMismatch();
		default:
			return localizator.getStrings().errorMessageUnknown();
		}
	}
}
