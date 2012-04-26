package org.sjarvela.mollify.client.plugin;

import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.SessionService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeSessionActions {

	private final SessionService service;

	public NativeSessionActions(SessionService service) {
		this.service = service;
	}

	public void logout(JavaScriptObject l) {
		final JsObj listener = l.cast();
		service.logout(new ResultListener<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				listener.call("success");
			}

			@Override
			public void onFail(ServiceError error) {
				listener.call("fail", error.asJs());
			}
		});
	}

	public JavaScriptObject asJs() {
		return createJs(this);
	}

	private native JavaScriptObject createJs(NativeSessionActions actions) /*-{
		return {
			logout : function(cb) {
				service.@org.sjarvela.mollify.client.plugin.NativeSessionActions::logout(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
			}
		}
	}-*/;

}
