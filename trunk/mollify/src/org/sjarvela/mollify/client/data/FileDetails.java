package org.sjarvela.mollify.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class FileDetails extends JavaScriptObject {
	protected FileDetails() {}
	
	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native int getLastAccessed() /*-{
		return this.last_accessed;
	}-*/;
	
	public final native int getLastChanged() /*-{
		return this.last_changed;
	}-*/;

	public final native int getLastModified() /*-{
		return this.last_modified;
	}-*/;
	
	public final native String getDescription() /*-{
		return this.description;
	}-*/;
}
