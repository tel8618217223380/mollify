/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.upload;

import com.google.gwt.core.client.JavaScriptObject;

public class FileUploadStatus extends JavaScriptObject {
	protected FileUploadStatus() {
	}

	public final native int getTotal() /*-{
		return this.total;
	}-*/;

	public final native int getCurrent() /*-{
		return this.current;
	}-*/;

	public final native int getUploadRate() /*-{
		return this.rate;
	}-*/;

	public final native String getFilename() /*-{
		return this.filename;
	}-*/;

	private final native int getIsDone() /*-{
		return this.done;
	}-*/;

	public final boolean isDone() {
		return getIsDone() == 1;
	}

	public final double getUploadedPercentage() {
		return (double) getCurrent() / (double) getTotal() * 100d;
	}

	public static FileUploadStatus create(int total, int current, int rate,
			String fileName, int done) {
		FileUploadStatus result = FileUploadStatus.createObject().cast();
		result.putValues(total, current, rate, fileName, done);
		return result;
	}

	private final native void putValues(int total, int current, int rate,
			String fileName, int done) /*-{
		this.total = total;
		this.current = current;
		this.rate = rate;
		this.filename = fileName;
		this.done = done;
	}-*/;
}
