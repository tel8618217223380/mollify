/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem;

import java.util.Arrays;
import java.util.List;

import org.sjarvela.mollify.client.event.Event;

import com.google.gwt.core.client.JavaScriptObject;

public class FileSystemEvent {

	public static Event createEvent(FileSystemItem item, FileSystemAction action) {
		return Event.create(getEventString(action), createItemList(Arrays
				.asList(item)));
	}

	public static Event createEvent(List<FileSystemItem> items,
			FileSystemAction action) {
		return Event.create(getEventString(action), createItemList(items));
	}

	private static JavaScriptObject createItemList(List<FileSystemItem> items) {
		JavaScriptObject a = JavaScriptObject.createArray();
		for (FileSystemItem item : items)
			add(a, item.asJs());
		return a;
	}

	private static native void add(JavaScriptObject a, JavaScriptObject item) /*-{
		a.push(item);
	}-*/;

	private static String getEventString(FileSystemAction action) {
		return "FILESYSTEM_" + action.name().toUpperCase();
	}

}
