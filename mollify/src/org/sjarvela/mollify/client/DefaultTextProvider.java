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

import org.sjarvela.mollify.client.localization.Localizator;

public class DefaultTextProvider implements TextProvider {
	private final Localizator localizator;

	public DefaultTextProvider(Localizator localizator) {
		this.localizator = localizator;
	}

	public String getSizeText(int bytes) {
		if (bytes < 1024) {
			return (bytes == 1 ? localizator.getMessages().sizeOneByte()
					: localizator.getMessages().sizeInBytes(bytes));
		}

		if (bytes < 1024 * 1024) {
			double kilobytes = (double) bytes / (double) 1024;
			return (kilobytes == 1 ? localizator.getMessages()
					.sizeOneKilobyte() : localizator.getMessages()
					.sizeInKilobytes(kilobytes));
		}

		double megabytes = (double) bytes / (double) (1024 * 1024);
		return localizator.getMessages().sizeInMegabytes(megabytes);
	}

}
