/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session.user;

import com.google.gwt.user.client.Random;

public class DefaultPasswordGenerator implements PasswordGenerator {
	private static int LENGTH = 8;
	private static String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public String generate() {
		String result = "";
		for (int i = 0; i < LENGTH; i++)
			result += getNextChar();
		return result;
	}

	private char getNextChar() {
		return CHARS.charAt(Random.nextInt(CHARS.length()));
	}

}
