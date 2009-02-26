/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.filesystem.upload;

import java.util.List;

import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.core.client.JavaScriptObject;

public interface FileUploadListener {

	void onUploadStarted(String uploadId, List<String> filenames);

	void onUploadFailed(ServiceError error);

	void onUploadFinished(JavaScriptObject result);
}
