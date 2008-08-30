package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.Localizator;

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
