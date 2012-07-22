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
import org.sjarvela.mollify.client.localization.Texts;

import com.google.gwt.i18n.client.constants.NumberConstants;

public class MollifyNumberConstants implements NumberConstants {
	private final String decimalSeparator;
	private final String groupingSeparator;
	private final String minusSign;
	private final String zeroDigit;
	private final String plusSign;
	private final String decimalPattern = "#,##0.###";
	private final String notANumber = "NaN";
	private final String currencyPattern = "";
	private final String defCurrencyCode = "EUR";
	private final String exponentialSymbol = "";
	private final String infinity = "";
	private final String monetaryGroupingSeparator = "";
	private final String monetarySeparator = "";
	private final String percent = "%";
	private final String percentPattern = "";
	private final String perMill = "";
	private final String scientificPattern = "";

	public MollifyNumberConstants(TextProvider textProvider) {
		decimalSeparator = textProvider.getText(Texts.decimalSeparator);
		groupingSeparator = textProvider.getText(Texts.groupingSeparator);
		minusSign = textProvider.getText(Texts.minusSign);
		plusSign = textProvider.getText(Texts.plusSign);
		zeroDigit = textProvider.getText(Texts.zeroDigit);
	}

	@Override
	public String notANumber() {
		return notANumber;
	}

	@Override
	public String currencyPattern() {
		return currencyPattern;
	}

	@Override
	public String decimalPattern() {
		return decimalPattern;
	}

	@Override
	public String decimalSeparator() {
		return decimalSeparator;
	}

	@Override
	public String defCurrencyCode() {
		return defCurrencyCode;
	}

	@Override
	public String exponentialSymbol() {
		return exponentialSymbol;
	}

	@Override
	public String groupingSeparator() {
		return groupingSeparator;
	}

	@Override
	public String infinity() {
		return infinity;
	}

	@Override
	public String minusSign() {
		return minusSign;
	}

	@Override
	public String monetaryGroupingSeparator() {
		return monetaryGroupingSeparator;
	}

	@Override
	public String monetarySeparator() {
		return monetarySeparator;
	}

	@Override
	public String percent() {
		return percent;
	}

	@Override
	public String percentPattern() {
		return percentPattern;
	}

	@Override
	public String perMill() {
		return perMill;
	}

	@Override
	public String plusSign() {
		return plusSign;
	}

	@Override
	public String scientificPattern() {
		return scientificPattern;
	}

	@Override
	public String zeroDigit() {
		return zeroDigit;
	}

}
