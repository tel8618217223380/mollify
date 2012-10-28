package org.sjarvela.mollify.client.ui.login;

import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.NativeView;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeLoginView extends NativeView implements LoginView {
	public NativeLoginView(JsObj viewHandler) {
		super(viewHandler);
	}

	@Override
	public void init(LoginViewListener listener) {
		viewHandler.call("init", createJsListener(listener));
	}

	private native JavaScriptObject createJsListener(LoginViewListener listener) /*-{
		return {
			onLogin : function(u, p, r) {
				listener.@org.sjarvela.mollify.client.ui.login.LoginViewListener::onLogin(Ljava/lang/String;Ljava/lang/String;Z)(u, p, r);
			}
		};
	}-*/;

	public void showLoginError() {
		viewHandler.call("showLoginError");
	}
}
