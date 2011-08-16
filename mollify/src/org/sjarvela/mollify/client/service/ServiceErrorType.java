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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.request.data.ErrorValue;

public enum ServiceErrorType {

	UNAUTHORIZED, REQUEST_FAILED, AUTHENTICATION_FAILED, NO_RESPONSE, INVALID_RESPONSE, DATA_TYPE_MISMATCH, OPERATION_FAILED, UNKNOWN_ERROR, INVALID_CONFIGURATION, FILE_DOES_NOT_EXIST, DIR_DOES_NOT_EXIST, FILE_ALREADY_EXISTS, DIR_ALREADY_EXISTS, NOT_A_FILE, NOT_A_DIR, DELETE_FAILED, NO_UPLOAD_DATA, UPLOAD_FAILED, SAVING_FAILED, INSUFFICIENT_RIGHTS, ZIP_FAILED, NO_GENERAL_WRITE_PERMISSION, INVALID_REQUEST, FEATURE_DISABLED, FEATURE_NOT_SUPPORTED, RESOURCE_NOT_FOUND;

	public String getMessage(TextProvider textProvider) {
		switch (this) {
		case REQUEST_FAILED:
			return textProvider.getText(Texts.errorMessageRequestFailed);
		case INVALID_REQUEST:
			return textProvider.getText(Texts.errorMessageInvalidRequest);
		case NO_RESPONSE:
			return textProvider.getText(Texts.errorMessageNoResponse);
		case INVALID_RESPONSE:
			return textProvider.getText(Texts.errorMessageInvalidResponse);
		case DATA_TYPE_MISMATCH:
			return textProvider.getText(Texts.errorMessageDataTypeMismatch);
		case OPERATION_FAILED:
			return textProvider.getText(Texts.errorMessageOperationFailed);
		case AUTHENTICATION_FAILED:
			return textProvider.getText(Texts.errorMessageAuthenticationFailed);
		case INVALID_CONFIGURATION:
			return textProvider.getText(Texts.errorMessageInvalidConfiguration);
		case FILE_ALREADY_EXISTS:
			return textProvider.getText(Texts.errorMessageFileAlreadyExists);
		case DIR_ALREADY_EXISTS:
			return textProvider
					.getText(Texts.errorMessageDirectoryAlreadyExists);
		case DIR_DOES_NOT_EXIST:
			return textProvider
					.getText(Texts.errorMessageDirectoryDoesNotExist);
		case INSUFFICIENT_RIGHTS:
			return textProvider.getText(Texts.errorMessageInsufficientRights);
		default:
			if (!this.equals(UNKNOWN_ERROR))
				return this.name();
			return textProvider.getText(Texts.errorMessageUnknown);
		}
	}

	public static ServiceErrorType getFrom(ErrorValue error) {
		return fromCode(error.getCode());
	}

	public static ServiceErrorType getByName(String name) {
		for (ServiceErrorType e : ServiceErrorType.values())
			if (e.name().equals(name))
				return e;
		return UNKNOWN_ERROR;
	}

	public static ServiceErrorType fromCode(int code) {
		switch (code) {
		case 100:
			return UNAUTHORIZED;
		case 101:
			return INVALID_REQUEST;
		case 104:
			return FEATURE_DISABLED;
		case 106:
			return FEATURE_NOT_SUPPORTED;
		case 107:
			return AUTHENTICATION_FAILED;
		case 105:
		case 201:
			// 201 is actually invalid path, but for user it is invalid
			// configuration
			return INVALID_CONFIGURATION;
		case 108:
			return REQUEST_FAILED;
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
			return INSUFFICIENT_RIGHTS;
		case 213:
			return ZIP_FAILED;
		case 214:
			return NO_GENERAL_WRITE_PERMISSION;

		default:
			Logger.getLogger(ServiceErrorType.class.getName()).log(
					Level.SEVERE, "ServiceError code " + code);
			return UNKNOWN_ERROR;
		}
	}
}
