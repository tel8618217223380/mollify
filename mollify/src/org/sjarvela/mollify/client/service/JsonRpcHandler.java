/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.data.ErrorValue;
import org.sjarvela.mollify.client.data.ReturnValue;
import org.sjarvela.mollify.client.log.MollifyLogger;

import com.google.gwt.core.client.JavaScriptObject;

public class JsonRpcHandler {
	private static int requestId = 0;
	private final ResultListener listener;
	private final String url;
	private final int id;
	private final MollifyLogger logger;

	public JsonRpcHandler(MollifyLogger logger, String url,
			ResultListener listener) {
		this.logger = logger;
		this.listener = listener;
		this.url = url;
		this.id = requestId++;
	}

	public void doRequest() {
		getExternalJson(id, url + "&callback=", this);
	}

	public void handleResponse(JavaScriptObject jso) {
		if (jso == null) {
			onError(new MollifyError(ServiceError.INVALID_RESPONSE));
		} else {
			ReturnValue result = jso.cast();

			if (!result.isSuccess()) {
				ErrorValue error = jso.cast();
				onError(new MollifyError(ServiceError.getFrom(error), error
						.getDetails()));
				return;
			}
			listener.onSuccess(result.getResult());
		}
	}

	private void onError(MollifyError error) {
		logger.logError("Json request failed: id=[" + id + "] url=[" + url
				+ "] msg=" + error.toString());
		listener.onFail(error);
	}

	public void handleError(String error) {
		onError(new MollifyError(ServiceError.getByName(error)));
	}

	private native static void getExternalJson(int requestId, String url,
			JsonRpcHandler handler) /*-{
	    var callback = "callback" + requestId;
	    
	    var script = document.createElement("script");
	    script.setAttribute("src", url+callback);
	    script.setAttribute("type", "text/javascript");
	
	    window[callback] = function(jsonObj) {
	      window[callback + "done"] = true;
	      handler.@org.sjarvela.mollify.client.service.JsonRpcHandler::handleResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
	    }
	    
	    setTimeout(function() {
	      if (!window[callback + "done"]) {
	        handler.@org.sjarvela.mollify.client.service.JsonRpcHandler::handleError(Ljava/lang/String;)("NO_RESPONSE");
	      } 
	
	      // cleanup
	      document.body.removeChild(script);
	      delete window[callback];
	      delete window[callback + "done"];
	    }, 5000);
	    
	    document.body.appendChild(script);
	}-*/;
}
