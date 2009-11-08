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
		UrlResolver urlProvider = new UrlResolver("http://test.domain/folder/",
				"");

		assertEquals("http://test.domain/folder/", urlProvider
				.getHostPageUrl(""));
		assertEquals("http://test.domain/folder/test/", urlProvider
				.getHostPageUrl("test/"));
		assertEquals("http://test.domain/folder/testfile.php", urlProvider
				.getHostPageUrl("testfile.php"));
		assertEquals("http://test.domain/folder/test/sub/", urlProvider
				.getHostPageUrl("test/sub/"));
		assertEquals("http://test.domain/root/test/", urlProvider
				.getHostPageUrl("/root/test/"));
		assertEquals("http://test.domain/root/testfile.php", urlProvider
				.getHostPageUrl("/root/testfile.php"));
		assertEquals("http://test.domain/testfile.php", urlProvider
				.getHostPageUrl("/testfile.php"));
		assertEquals("http://test.domain/", urlProvider.getHostPageUrl("/"));
		assertEquals("http://test.domain/folder/", urlProvider
				.getHostPageUrl(null));
	}

	@Test
	public void testModuleUrl() {
		UrlResolver urlProvider = new UrlResolver("",
				"http://test.domain/folder/");

		assertEquals("http://test.domain/folder/", urlProvider.getModuleUrl(""));
		assertEquals("http://test.domain/folder/test/", urlProvider
				.getModuleUrl("test/"));
		assertEquals("http://test.domain/folder/testfile.php", urlProvider
				.getModuleUrl("testfile.php"));
		assertEquals("http://test.domain/folder/test/sub/", urlProvider
				.getModuleUrl("test/sub/"));
		assertEquals("http://test.domain/root/test/", urlProvider
				.getModuleUrl("/root/test/"));
		assertEquals("http://test.domain/root/testfile.php", urlProvider
				.getModuleUrl("/root/testfile.php"));
		assertEquals("http://test.domain/testfile.php", urlProvider
				.getModuleUrl("/testfile.php"));
		assertEquals("http://test.domain/", urlProvider.getModuleUrl("/"));
		assertEquals("http://test.domain/folder/", urlProvider
				.getModuleUrl(null));
	}

	@Test
	public void testHttps() {
		UrlResolver urlProvider = new UrlResolver(
				"https://test.domain/folder/", "");

		assertEquals("https://test.domain/folder/", urlProvider
				.getHostPageUrl(""));
		assertEquals("https://test.domain/folder/test/", urlProvider
				.getHostPageUrl("test/"));
		assertEquals("https://test.domain/root/test/", urlProvider
				.getHostPageUrl("/root/test/"));
	}

	@Test
	public void testIllegal() {
		UrlResolver urlProvider = new UrlResolver("http://test.domain/folder/",
				"");

		try {
			urlProvider.getHostPageUrl("http://test");
			fail("Illegal");
		} catch (RuntimeException e) {
			// ok
		}

		try {
			urlProvider.getHostPageUrl("https://test");
			fail("Illegal");
		} catch (RuntimeException e) {
			// ok
		}
	}

}
