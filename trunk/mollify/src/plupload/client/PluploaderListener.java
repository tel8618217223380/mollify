/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package plupload.client;

import java.util.List;

public interface PluploaderListener {

	void onInit(Pluploader p, String runtime);

	void postInit(Pluploader uploader);

	void onFilesAdded(Pluploader p, List<File> files);

	void onFilesRemoved(Pluploader uploader, List<File> files);

	void onQueueChanged(Pluploader uploader);

	void onRefresh(Pluploader uploader);

	void onStateChanged(Pluploader uploader);

	void onFileUpload(Pluploader uploader, File file);

	void onFileUploadProgress(Pluploader uploader, File file);

}
