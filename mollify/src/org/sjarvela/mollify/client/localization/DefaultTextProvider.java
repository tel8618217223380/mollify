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

import org.sjarvela.mollify.client.service.request.data.ErrorValue;

import com.google.gwt.core.client.GWT;
import com.google.inject.Singleton;

@Singleton
public class DefaultTextProvider implements TextProvider {
	private LanguageConstants languageConstants;
	private MessageConstants messageConstants;

	public DefaultTextProvider() {
		languageConstants = GWT.create(LanguageConstants.class);
		messageConstants = GWT.create(MessageConstants.class);
	}

	public LanguageConstants getStrings() {
		return languageConstants;
	}

	public MessageConstants getMessages() {
		return messageConstants;
	}

	public String getErrorMessage(ErrorValue errorResult) {
		// TODO get localized
		return errorResult.getCode() + ": " + errorResult.getError() + "("
				+ errorResult.getDetails() + ")";
	}

	public String getSizeText(int bytes) {
		if (bytes < 1024) {
			return (bytes == 1 ? getMessages().sizeOneByte() : getMessages()
					.sizeInBytes(bytes));
		}

		if (bytes < 1024 * 1024) {
			double kilobytes = (double) bytes / (double) 1024;
			return (kilobytes == 1 ? getMessages().sizeOneKilobyte()
					: getMessages().sizeInKilobytes(kilobytes));
		}

		double megabytes = (double) bytes / (double) (1024 * 1024);
		return getMessages().sizeInMegabytes(megabytes);
	}
}
