package org.sjarvela.mollify.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class Directory extends JavaScriptObject {
	static Directory emptyInstance = null;

	public static Directory Empty() {
		if (emptyInstance == null) {
			emptyInstance = Directory.createObject().cast();
			emptyInstance.clear();
		}
		
		return emptyInstance;
	}

	protected Directory() {
	}
	
	private final native void clear() /*-{
		this.id = "";
		this.name = "";
	}-*/;

	public final native String getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final boolean isEmpty() {
		return this.equals(Empty());
	}
}
