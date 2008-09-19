package org.sjarvela.mollify.client.data;

import com.google.gwt.core.client.JavaScriptObject;

public class ReturnValue extends JavaScriptObject {
	protected ReturnValue() {
	}

	public final native boolean isSuccess() /*-{
		return this.success;
	}-*/;
	
	public final native JavaScriptObject getResult() /*-{
		return this.result;
	}-*/;
}
