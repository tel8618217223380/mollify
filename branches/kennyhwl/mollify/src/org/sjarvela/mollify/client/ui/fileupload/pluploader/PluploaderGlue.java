/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileupload.pluploader;

import org.sjarvela.mollify.client.ui.action.ActionDelegator;
import org.sjarvela.mollify.client.ui.action.ActionHandler;
import org.sjarvela.mollify.client.ui.action.VoidActionHandler;

import plupload.client.File;

public class PluploaderGlue {

	public PluploaderGlue(final PluploaderDialog dialog,
			final PluploaderPresenter presenter, ActionDelegator actionDelegator) {
		actionDelegator.setActionHandler(PluploaderDialog.Actions.upload,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onStartUpload();
					}
				});
		actionDelegator.setActionHandler(PluploaderDialog.Actions.cancel,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onCancel();
					}
				});
		actionDelegator.setActionHandler(PluploaderDialog.Actions.cancelUpload,
				new VoidActionHandler() {
					public void onAction() {
						presenter.onCancelUpload();
					}
				});
		actionDelegator.setActionHandler(PluploaderDialog.Actions.removeFile,
				new ActionHandler<File>() {
					public void onAction(File f) {
						presenter.onRemoveFile(f);
					}
				});
		presenter.init();
	}
}
