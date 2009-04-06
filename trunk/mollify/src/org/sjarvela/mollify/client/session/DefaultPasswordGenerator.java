package org.sjarvela.mollify.client.session;

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
