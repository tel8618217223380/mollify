/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request.data;

import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;

public class JSONStringBuilderTest extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "org.sjarvela.mollify.Client";
	}

	@Test
	public void testTypes() {
		JSONStringBuilder b = new JSONStringBuilder();
		b.add("string", "stringVal");
		b.add("int", 13);
		b.add("double", 15.5d);

		assertEquals("{\"string\":\"stringVal\", \"int\":13, \"double\":15.5}",
				b.toString());
	}
}
