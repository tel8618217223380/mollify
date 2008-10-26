package org.sjarvela.mollify.client.data;

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
		return this.current;
	}-*/;

	public final boolean isDone() {
		return getIsDone() == 1;
	}

	public final double getUploadedPercentage() {
		return (double) getCurrent() / (double) getTotal() * 100d;
	}
}
