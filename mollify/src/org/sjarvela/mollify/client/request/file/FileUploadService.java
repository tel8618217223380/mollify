/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.request.file;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.request.ResultListener;
import org.sjarvela.mollify.client.request.ReturnValue;
import org.sjarvela.mollify.client.service.MollifyServiceException;

public interface FileUploadService {

	void getUploadProgress(String id, ResultListener listener);

	String getFileUploadId();

	String getUploadUrl(Directory directory);

	ReturnValue handleResult(String results) throws MollifyServiceException;

}
