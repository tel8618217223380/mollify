/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service.environment.demo;

import org.sjarvela.mollify.client.service.ExternalService;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;

public class DemoExternalService implements ExternalService {

	@Override
	public void get(String path, ResultListener listener) {
		if (path.equals("preview")) {
			listener
					.onSuccess(new JsObjBuilder()
							.string(
									"html",
									"<div id='demo-preview-container' style='overflow:auto; max-height:300px'><img src='file-preview.png' style='max-width:400px'></div>")
							.create());
		} else if (path.equals("embedded-view")) {
			listener
					.onSuccess(new JsObjBuilder()
							.string(
									"html",
									"<iframe id='demo-viewer' src='file-view.html' style='border: none;'></iframe>")
							.string("size", "600;400").string(
									"resized_element_id", "demo-viewer")
							.create());
		}
	}

}
