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

import com.google.gwt.i18n.client.CurrencyData;

public class MollifyCurrencyData implements CurrencyData {

	@Override
	public String getCurrencyCode() {
		return "";
	}

	@Override
	public String getCurrencySymbol() {
		return "";
	}

	@Override
	public int getDefaultFractionDigits() {
		return 0;
	}

	@Override
	public String getPortableCurrencySymbol() {
		return "";
	}

	@Override
	public boolean isDeprecated() {
		return false;
	}

	@Override
	public boolean isSpaceForced() {
		return false;
	}

	@Override
	public boolean isSpacingFixed() {
		return false;
	}

	@Override
	public boolean isSymbolPositionFixed() {
		return false;
	}

	@Override
	public boolean isSymbolPrefix() {
		return false;
	}
}
