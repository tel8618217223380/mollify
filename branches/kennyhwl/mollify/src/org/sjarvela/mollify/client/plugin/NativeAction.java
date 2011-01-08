package org.sjarvela.mollify.client.plugin;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeAction {
	private final JavaScriptObject cb;

	public NativeAction(JavaScriptObject dp) {
		this.cb = dp;
	}

	public void onAction() {
		triggerCallback();
	}

	private native final void triggerCallback() /*-{
		var c = this.@org.sjarvela.mollify.client.plugin.NativeAction::cb;
		c();
	}-*/;

}
