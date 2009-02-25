/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.ui.ProgressBar;
import org.sjarvela.mollify.client.ui.ProgressDisplayer;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressDialog extends CenteredDialog implements ProgressDisplayer {

	private ProgressBar progressBar;
	private Label info;
	private Label details;

	public ProgressDialog(String title, boolean progressInitiallyBarVisible) {
		super(title, StyleConstants.PROGRESS_DIALOG);
		initialize();
		if (!progressInitiallyBarVisible)
			progressBar.setVisible(false);
	}

	@Override
	Widget createContent() {
		VerticalPanel panel = new VerticalPanel();
		info = new Label();
		info.setStyleName(StyleConstants.PROGRESS_DIALOG_INFO);
		panel.add(info);

		progressBar = new ProgressBar(
				StyleConstants.PROGRESS_DIALOG_PROGRESS_BAR);
		panel.add(progressBar);

		details = new Label();
		details.setStyleName(StyleConstants.PROGRESS_DIALOG_DETAILS);
		panel.add(details);

		return panel;
	}

	public void setProgress(int percentage) {
		progressBar.setProgress(percentage);
	}

	public void setInfo(String infoText) {
		info.setText(infoText);
	}

	public void setDetails(String detailsText) {
		details.setText(detailsText);
	}

	public void onFinished() {
		this.hide();
	}

	public void setProgressBarVisible(boolean visible) {
		progressBar.setVisible(visible);
	}

}
