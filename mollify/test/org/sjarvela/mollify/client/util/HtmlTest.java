/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.util;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class HtmlTest extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "org.sjarvela.mollify.Client";
	}

	@Test
	public void testFoo() {
		assertEquals("%C3%A4%C3%B6%C3%A5", Html.fullUrlEncode("äöå"));
	}
}
