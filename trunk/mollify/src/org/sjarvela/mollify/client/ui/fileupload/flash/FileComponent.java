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

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.common.ProgressBar;
import org.sjarvela.mollify.client.ui.fileupload.flash.FlashFileUploadDialog.Mode;
import org.swfupload.client.File;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class FileComponent extends FlowPanel {

	private ActionButton button;
	private ProgressBar pb;

	public FileComponent(TextProvider textProvider, File file,
			ActionListener actionListener, ResourceId clickAction) {
		this.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE);

		Label name = new Label(file.getName());
		name.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_NAME);
		add(name);

		button = new ActionButton(textProvider.getStrings()
				.fileUploadDialogRemoveFileButton(),
				StyleConstants.FILE_UPLOAD_DIALOG_FILE_REMOVE_BUTTON,
				StyleConstants.FILE_UPLOAD_DIALOG_FILE_REMOVE_BUTTON);
		button.setAction(actionListener, clickAction, file);
		add(button);

		pb = new ProgressBar(StyleConstants.FILE_UPLOAD_DIALOG_FILE_PROGRESS);
		pb.setProgress(0d);
		pb.setVisible(false);
		add(pb);
	}

	public void setMode(Mode mode) {
		button.setVisible(mode.equals(Mode.Select));
	}

	public void setActive(boolean active) {
		if (active) {
			addStyleDependentName(StyleConstants.ACTIVE);
			pb.setVisible(true);
		} else {
			removeStyleDependentName(StyleConstants.ACTIVE);
		}
	}

	public void setProgress(double progress) {
		pb.setProgress(progress);
	}

	public void setFinished() {
		setActive(false);
		addStyleDependentName(StyleConstants.COMPLETE);
	}

}
