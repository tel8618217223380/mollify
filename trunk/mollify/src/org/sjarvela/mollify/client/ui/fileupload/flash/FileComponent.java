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
import org.swfupload.client.File;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class FileComponent extends FlowPanel {
	private final TextProvider textProvider;

	private ActionButton button;
	private ProgressBar pb;
	private Panel progressPanel;
	private Label info;
	private String totalSize;

	public FileComponent(TextProvider textProvider, File file,
			ActionListener actionListener, ResourceId clickAction) {
		this.textProvider = textProvider;
		this.totalSize = textProvider.getSizeText(file.getSize());

		this.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE);

		// remove button
		button = new ActionButton(textProvider.getStrings()
				.fileUploadDialogRemoveFileButton(),
				StyleConstants.FILE_UPLOAD_DIALOG_FILE_REMOVE_BUTTON,
				StyleConstants.FILE_UPLOAD_DIALOG_FILE_REMOVE_BUTTON);
		button.setAction(actionListener, clickAction, file);
		add(button);

		// first row
		Panel upper = new FlowPanel();
		upper.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_ROW1);
		Label name = new Label(file.getName());
		name.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_NAME);
		upper.add(name);
		add(upper);

		// second row
		Panel lower = new HorizontalPanel();
		lower.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_ROW2);

		progressPanel = new FlowPanel();
		progressPanel
				.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_PROGRESS_PANEL);
		pb = new ProgressBar(StyleConstants.FILE_UPLOAD_DIALOG_FILE_PROGRESS);
		pb.setProgress(0d);
		progressPanel.add(pb);
		progressPanel.setVisible(false);
		lower.add(progressPanel);

		info = new Label(totalSize);
		info.setStylePrimaryName(StyleConstants.FILE_UPLOAD_DIALOG_FILE_INFO);
		lower.add(info);

		add(lower);
	}

	public void setActive(boolean active) {
		if (active) {
			addStyleDependentName(StyleConstants.ACTIVE);
			progressPanel.setVisible(true);
		} else {
			removeStyleDependentName(StyleConstants.ACTIVE);
			progressPanel.setVisible(false);
		}
	}

	public void setProgress(double progress, long complete) {
		pb.setProgress(progress);
		info.setText(textProvider.getSizeText(complete) + " / " + totalSize);
	}

	public void setFinished() {
		button.setVisible(false);
		setActive(false);
		info.setText(textProvider.getStrings()
				.fileUploadDialogMessageFileCompleted());
		addStyleDependentName(StyleConstants.COMPLETE);
	}

	public void setCancelled() {
		button.setVisible(false);
		setActive(false);
		info.setText(textProvider.getStrings()
				.fileUploadDialogMessageFileCancelled());
		addStyleDependentName(StyleConstants.CANCEL);
	}

}
