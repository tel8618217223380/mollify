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
import org.sjarvela.mollify.client.service.UrlResolver;

public class UrlResolverTest {

	@Test
	public void testHttp() {
		UrlResolver urlProvider = new UrlResolver("http://test.domain/folder/",
				"");

		assertEquals("http://test.domain/folder/", urlProvider.getHostPageUrl(
				"", true));
		assertEquals("http://test.domain/folder/", urlProvider.getHostPageUrl(
				"", false));
		assertEquals("http://test.domain/folder/test/", urlProvider
				.getHostPageUrl("test", true));
		assertEquals("http://test.domain/folder/test/", urlProvider
				.getHostPageUrl("test", true));
		assertEquals("http://test.domain/folder/testfile.php", urlProvider
				.getHostPageUrl("testfile.php", false));
		assertEquals("http://test.domain/folder/test/sub/", urlProvider
				.getHostPageUrl("test/sub/", true));
		assertEquals("http://test.domain/root/test/", urlProvider
				.getHostPageUrl("/root/test/", true));
		assertEquals("http://test.domain/root/testfile.php", urlProvider
				.getHostPageUrl("/root/testfile.php", false));
		assertEquals("http://test.domain/testfile.php", urlProvider
				.getHostPageUrl("/testfile.php", false));
		assertEquals("http://test.domain/", urlProvider.getHostPageUrl("/",
				true));
		assertEquals("http://test.domain/folder/", urlProvider.getHostPageUrl(
				null, false));
	}

	@Test
	public void testModuleUrl() {
		UrlResolver urlProvider = new UrlResolver("",
				"http://test.domain/folder/");

		assertEquals("http://test.domain/folder/", urlProvider.getModuleUrl("",
				true));
		assertEquals("http://test.domain/folder/test/", urlProvider
				.getModuleUrl("test", true));
		assertEquals("http://test.domain/folder/testfile.php", urlProvider
				.getModuleUrl("testfile.php", false));
		assertEquals("http://test.domain/folder/test/sub/", urlProvider
				.getModuleUrl("test/sub/", true));
		assertEquals("http://test.domain/root/test/", urlProvider.getModuleUrl(
				"/root/test/", true));
		assertEquals("http://test.domain/root/testfile.php", urlProvider
				.getModuleUrl("/root/testfile.php", false));
		assertEquals("http://test.domain/testfile.php", urlProvider
				.getModuleUrl("/testfile.php", false));
		assertEquals("http://test.domain/", urlProvider.getModuleUrl("/", true));
		assertEquals("http://test.domain/folder/", urlProvider.getModuleUrl(
				null, false));
	}

	@Test
	public void testHttps() {
		UrlResolver urlProvider = new UrlResolver(
				"https://test.domain/folder/", "");

		assertEquals("https://test.domain/folder/", urlProvider.getHostPageUrl(
				"", true));
		assertEquals("https://test.domain/folder/test/", urlProvider
				.getHostPageUrl("test/", true));
		assertEquals("https://test.domain/root/test/", urlProvider
				.getHostPageUrl("/root/test/", true));
	}

	@Test
	public void testRelative() {
		UrlResolver urlProvider = new UrlResolver("http://test.domain/folder/",
				"http://test.domain/folder/module/");

		assertEquals("module/file", urlProvider.getRelativeModuleUrl("file"));
	}

	@Test
	public void testIllegal() {
		UrlResolver urlProvider = new UrlResolver("http://test.domain/folder/",
				"");

		try {
			urlProvider.getHostPageUrl("http://test", true);
			fail("Illegal");
		} catch (RuntimeException e) {
			// ok
		}

		try {
			urlProvider.getHostPageUrl("https://test", true);
			fail("Illegal");
		} catch (RuntimeException e) {
			// ok
		}
	}

}
