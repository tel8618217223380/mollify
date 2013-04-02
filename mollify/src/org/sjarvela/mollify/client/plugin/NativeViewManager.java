package org.sjarvela.mollify.client.plugin;

import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeViewManager {
	// private final ViewManager viewManager;
	private final DialogManager dialogManager;

	public NativeViewManager(DialogManager dialogManager) {
		// this.viewManager = viewManager;
		this.dialogManager = dialogManager;
	}

	public JavaScriptObject asJs() {
		return createJs(this);
	}

	public void registerHandlers(JavaScriptObject handlers) {
		// viewManager.setViewHandlers(handlers);
		JsObj h = handlers.cast();
		dialogManager.setHandler(h.getObject("dialogs"));
	}

	private native JavaScriptObject createJs(NativeViewManager vm) /*-{
		return {
			registerHandlers : function(h) {
				vm.@org.sjarvela.mollify.client.plugin.NativeViewManager::registerHandlers(Lcom/google/gwt/core/client/JavaScriptObject;)(h);
			}
		};
	}-*/;
}
