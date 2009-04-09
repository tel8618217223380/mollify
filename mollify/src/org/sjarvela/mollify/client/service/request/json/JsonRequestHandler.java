/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.request.json;

import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.ServiceErrorType;
import org.sjarvela.mollify.client.service.request.ErrorValue;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.service.request.ReturnValue;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class JsonRequestHandler {
	private static int requestId = 0;
	private final ResultListener listener;
	private final String url;
	private final int id;
	private final int timeout;

	public JsonRequestHandler(String url, ResultListener listener, int timeout) {
		this.listener = listener;
		this.url = url;
		this.timeout = timeout;
		this.id = requestId++;
	}

	public void doRequest() {
		getExternalJson(id, url + "&callback=", this, timeout);
	}

	public void handleResponse(JavaScriptObject jso) {
		if (jso == null) {
			onError(new ServiceError(ServiceErrorType.INVALID_RESPONSE));
		} else {
			if (Log.isDebugEnabled())
				Log
						.debug("Request response: "
								+ new JSONObject(jso).toString());

			ReturnValue result = jso.cast();

			if (!result.isSuccess()) {
				ErrorValue error = jso.cast();
				onError(new ServiceError(ServiceErrorType.getFrom(error), error
						.getDetails()));
				return;
			}
			listener.onSuccess(result.getResult());
		}
	}

	private void onError(ServiceError error) {
		Log.error("Request failed: id=[" + id + "] url=[" + url + "] msg="
				+ error.toString());
		listener.onFail(error);
	}

	public void handleError(String error) {
		onError(new ServiceError(ServiceErrorType.getByName(error)));
	}

	private native static void getExternalJson(int requestId, String url,
			JsonRequestHandler handler, int timeout) /*-{
	    var callback = "callback" + requestId;
	    
	    var script = document.createElement("script");
	    script.setAttribute("src", url+callback);
	    script.setAttribute("type", "text/javascript");
	
	    window[callback] = function(jsonObj) {
			window[callback + "done"] = true;
			handler.@org.sjarvela.mollify.client.service.request.json.JsonRequestHandler::handleResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
	    }
	    
	    setTimeout(function() {
			if (!window[callback + "done"]) {
				handler.@org.sjarvela.mollify.client.service.request.json.JsonRequestHandler::handleError(Ljava/lang/String;)("NO_RESPONSE");
			} 
	
			// cleanup
			document.body.removeChild(script);
			delete window[callback];
			delete window[callback + "done"];
	    }, timeout * 1000);
	    
	    document.body.appendChild(script);
	}-*/;
}
