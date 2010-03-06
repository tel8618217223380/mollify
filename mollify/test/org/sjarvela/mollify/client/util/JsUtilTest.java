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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.sjarvela.mollify.client.filesystem.js.JsFile;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.junit.client.GWTTestCase;

public class JsUtilTest extends GWTTestCase {
	@Override
	public String getModuleName() {
		return "org.sjarvela.mollify.Client";
	}

	@Test
	public void testAsJsonString() {
		Map<String, JavaScriptObject> o = new HashMap();
		o.put("a", JsFile.create("a", "", "file A", "p", "ext", 123));
		o.put("b", JsUtil.asJsArray(JsFile.class, JsFile.create("b", "",
				"file B", "p", "ext", 123), JsFile.create("c", "", "file C",
				"p", "ext", 123)));
		assertEquals(
				"{\"a\":{\"id\":\"a\", \"name\":\"file A\", \"parent_id\":\"p\", \"extension\":\"ext\", \"size\":123}, \"b\":[{\"id\":\"b\", \"name\":\"file B\", \"parent_id\":\"p\", \"extension\":\"ext\", \"size\":123},{\"id\":\"c\", \"name\":\"file C\", \"parent_id\":\"p\", \"extension\":\"ext\", \"size\":123}]}",
				JsUtil.asJsonString(o, Arrays.asList("a", "b")));
	}
}
