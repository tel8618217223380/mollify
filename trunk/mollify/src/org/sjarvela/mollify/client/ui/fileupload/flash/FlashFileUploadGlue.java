/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.flash;

import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.action.ActionHandler;
import org.sjarvela.mollify.client.ui.action.VoidActionHandler;
import org.swfupload.client.File;

public class FlashFileUploadGlue {

	public FlashFileUploadGlue(final FlashFileUploadDialog dialog,
			final FlashFileUploadPresenter presenter,
			ActionDelegator actionDelegator) {
		actionDelegator.setActionHandler(FlashFileUploadDialog.Actions.upload,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onStartUpload();
					}
				});
		actionDelegator.setActionHandler(FlashFileUploadDialog.Actions.cancel,
				new VoidActionHandler() {
					public void onAction() {
						dialog.hide();
					}
				});
		actionDelegator.setActionHandler(
				FlashFileUploadDialog.Actions.removeFile,
				new ActionHandler<File>() {
					public void onAction(File f) {
						presenter.onRemoveFile(f);
					}
				});
	}
}
