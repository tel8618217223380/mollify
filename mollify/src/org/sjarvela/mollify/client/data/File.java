package org.sjarvela.mollify.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class File extends JavaScriptObject {
	static File emptyInstance = null;

	public static File Empty() {
		if (emptyInstance == null) {
			emptyInstance = File.createObject().cast();
			emptyInstance.clear();
		}
		
		return emptyInstance;
	}
	
	protected File() {}

	private final native void clear() /*-{
		this.id = "";
		this.name = "";
		this.extension = "";
		this.size = 0;
	}-*/;
	
	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getExtension() /*-{
		return this.extension;
	}-*/;
	
	public final native int getSize() /*-{
		return this.size;
	}-*/;
	
	public final int getSizeInKB() {
		return getSize() / 1024;
	}
}
