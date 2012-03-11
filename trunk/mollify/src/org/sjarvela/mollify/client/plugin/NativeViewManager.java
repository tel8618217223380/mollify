package org.sjarvela.mollify.client.plugin;

import org.sjarvela.mollify.client.ui.ViewManager;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeViewManager {
	private final ViewManager viewManager;

	public NativeViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}

	public JavaScriptObject asJs() {
		return createJs(this);
	}

	public void registerView(String name, JavaScriptObject view) {
		viewManager.registerView(name, view);
	}

	private native JavaScriptObject createJs(NativeViewManager vm) /*-{
		var o = {};

		o.registerView = function(n, o) {
			vm.@org.sjarvela.mollify.client.plugin.NativeViewManager::registerView(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(n, o);
		}
		return o;
	}-*/;
}
