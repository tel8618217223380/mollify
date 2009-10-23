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

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class DefaultPasswordGeneratorTest extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "org.sjarvela.mollify.Client";
	}

	@Test
	public void testGenerate() {
		DefaultPasswordGenerator gen = new DefaultPasswordGenerator();

		String pw1 = gen.generate();
		String pw2 = gen.generate();

		assertNotSame(pw1, pw2);
		assertEquals(8, pw1.length());
		assertEquals(8, pw2.length());
	}
}
