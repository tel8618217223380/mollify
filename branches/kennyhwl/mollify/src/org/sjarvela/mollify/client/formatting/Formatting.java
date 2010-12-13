/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.formatting;

import org.sjarvela.mollify.client.localization.TextProvider;

import com.google.gwt.i18n.client.NumberFormat;

// TODO into container?
public class Formatting {
	private static MollifyNumberConstants numberConstants;

	public static void initialize(TextProvider textProvider) {
		numberConstants = new MollifyNumberConstants(textProvider);
	}

	public static NumberFormat getNumberFormat(String format) {
		return new MollifyNumberFormat(numberConstants, format);
	}

}
