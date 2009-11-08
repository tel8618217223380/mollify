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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class UrlResolverTest {

	@Test
	public void testHttp() {
		UrlResolver urlProvider = new UrlResolver("http://test.domain/folder/");

		assertEquals("http://test.domain/folder/", urlProvider.getUrl(""));
		assertEquals("http://test.domain/folder/test/", urlProvider
				.getUrl("test/"));
		assertEquals("http://test.domain/folder/testfile.php", urlProvider
				.getUrl("testfile.php"));
		assertEquals("http://test.domain/folder/test/sub/", urlProvider
				.getUrl("test/sub/"));
		assertEquals("http://test.domain/root/test/", urlProvider
				.getUrl("/root/test/"));
		assertEquals("http://test.domain/root/testfile.php", urlProvider
				.getUrl("/root/testfile.php"));
		assertEquals("http://test.domain/testfile.php", urlProvider
				.getUrl("/testfile.php"));
		assertEquals("http://test.domain/", urlProvider.getUrl("/"));
		assertEquals("http://test.domain/folder/", urlProvider.getUrl(null));
	}

	@Test
	public void testHttps() {
		UrlResolver urlProvider = new UrlResolver("https://test.domain/folder/");

		assertEquals("https://test.domain/folder/", urlProvider.getUrl(""));
		assertEquals("https://test.domain/folder/test/", urlProvider
				.getUrl("test/"));
		assertEquals("https://test.domain/root/test/", urlProvider
				.getUrl("/root/test/"));
	}

	@Test
	public void testIllegal() {
		UrlResolver urlProvider = new UrlResolver("http://test.domain/folder/");

		try {
			urlProvider.getUrl("http://test");
			fail("Illegal");
		} catch (RuntimeException e) {
			// ok
		}

		try {
			urlProvider.getUrl("https://test");
			fail("Illegal");
		} catch (RuntimeException e) {
			// ok
		}
	}

}
