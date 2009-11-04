/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.flash;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.swfupload.client.File;

import com.google.gwt.junit.client.GWTTestCase;

public class UploadModelTest extends GWTTestCase {
	File file1;
	File file2;
	File file3;

	@Override
	public String getModuleName() {
		return "org.sjarvela.mollify.Client";
	}

	public void gwtSetUp() {
		file1 = File.create(0, "0", "File1", 100);
		file2 = File.create(1, "1", "File2", 200);
		file3 = File.create(2, "2", "File3", 300);
	}

	@Test
	public void testEmpty() {
		UploadModel model = new UploadModel(Collections.EMPTY_LIST);

		assertFalse(model.hasNext());
		assertNull(model.nextFile());
	}

	@Test
	public void testNonEmpty() {
		UploadModel model = new UploadModel(Arrays.asList(file1, file2, file3));

		assertTrue(model.hasNext());
		assertEquals(600l, model.getTotalBytes());

		assertEquals(0l, model.getTotalProgress(100l));
		assertEquals(file1, model.nextFile());
		assertEquals(55l, model.getTotalProgress(55l));
		assertEquals(file2, model.nextFile());
		assertEquals(155l, model.getTotalProgress(55l));
		assertEquals(file3, model.nextFile());
		assertEquals(355l, model.getTotalProgress(55l));
		assertFalse(model.hasNext());
		assertNull(model.nextFile());
	}
}
