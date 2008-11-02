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

import org.sjarvela.mollify.client.data.ErrorValue;
import org.sjarvela.mollify.client.localization.Localizator;

import com.google.gwt.core.client.GWT;

public enum ServiceError {
	NO_RESPONSE, INVALID_RESPONSE, DATA_TYPE_MISMATCH, OPERATION_FAILED, AUTHENTICATION_FAILED, UNKNOWN_ERROR, INVALID_CONFIGURATION;

	public String getMessage(Localizator localizator) {
		switch (this) {
		case NO_RESPONSE:
			return localizator.getStrings().errorMessageNoResponse();
		case INVALID_RESPONSE:
			return localizator.getStrings().errorMessageInvalidResponse();
		case DATA_TYPE_MISMATCH:
			return localizator.getStrings().errorMessageDataTypeMismatch();
		case OPERATION_FAILED:
			return localizator.getStrings().errorMessageOperationFailed();
		case AUTHENTICATION_FAILED:
			return localizator.getStrings().errorMessageAuthenticationFailed();
		case INVALID_CONFIGURATION:
			return localizator.getStrings().errorMessageInvalidConfiguration();
		default:
			return localizator.getStrings().errorMessageUnknown();
		}
	}

	public static ServiceError getFrom(ErrorValue error) {
		switch (error.getCode()) {
		case 100:
			return AUTHENTICATION_FAILED;
		case 201:
			return INVALID_CONFIGURATION; // actually invalid path
		default:
			GWT.log("ServiceError code " + error.getCode(), null);
			return UNKNOWN_ERROR;
		}
	}
}
