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

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.request.ErrorValue;

import com.google.gwt.core.client.GWT;

public enum ServiceErrorType {
	AUTHENTICATION_FAILED, NO_RESPONSE, INVALID_RESPONSE, DATA_TYPE_MISMATCH, OPERATION_FAILED, UNKNOWN_ERROR, INVALID_CONFIGURATION, FILE_DOES_NOT_EXIST, DIR_DOES_NOT_EXIST, FILE_ALREADY_EXISTS, DIR_ALREADY_EXISTS, NOT_A_FILE, NOT_A_DIR, DELETE_FAILED, NO_UPLOAD_DATA, UPLOAD_FAILED, SAVING_FAILED, NO_MODIFY_RIGHTS;

	public String getMessage(TextProvider textProvider) {
		switch (this) {
		case NO_RESPONSE:
			return textProvider.getStrings().errorMessageNoResponse();
		case INVALID_RESPONSE:
			return textProvider.getStrings().errorMessageInvalidResponse();
		case DATA_TYPE_MISMATCH:
			return textProvider.getStrings().errorMessageDataTypeMismatch();
		case OPERATION_FAILED:
			return textProvider.getStrings().errorMessageOperationFailed();
		case AUTHENTICATION_FAILED:
			return textProvider.getStrings().errorMessageAuthenticationFailed();
		case INVALID_CONFIGURATION:
			return textProvider.getStrings().errorMessageInvalidConfiguration();
		case DIR_ALREADY_EXISTS:
			return textProvider.getStrings()
					.errorMessageDirectoryAlreadyExists();
		default:
			if (!this.equals(UNKNOWN_ERROR))
				return this.name();
			return textProvider.getStrings().errorMessageUnknown();
		}
	}

	public static ServiceErrorType getFrom(ErrorValue error) {
		switch (error.getCode()) {
		case 100:
			return AUTHENTICATION_FAILED;
		case 105:
		case 201:
			// 201 is actually invalid path, but for user it is invalid
			// configuration
			return INVALID_CONFIGURATION;
		case 202:
			return FILE_DOES_NOT_EXIST;
		case 203:
			return DIR_DOES_NOT_EXIST;
		case 204:
			return FILE_ALREADY_EXISTS;
		case 205:
			return DIR_ALREADY_EXISTS;
		case 206:
			return NOT_A_FILE;
		case 207:
			return NOT_A_DIR;
		case 208:
			return DELETE_FAILED;
		case 209:
			return NO_UPLOAD_DATA;
		case 210:
			return UPLOAD_FAILED;
		case 211:
			return SAVING_FAILED;
		case 212:
			return NO_MODIFY_RIGHTS;
		default:
			GWT.log("ServiceError code " + error.getCode(), null);
			return UNKNOWN_ERROR;
		}
	}

	public static ServiceErrorType getByName(String name) {
		for (ServiceErrorType e : ServiceErrorType.values())
			if (e.name().equals(name))
				return e;
		return UNKNOWN_ERROR;
	}
}
