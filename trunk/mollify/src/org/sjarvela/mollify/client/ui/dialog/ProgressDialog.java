package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.data.FileUploadStatus;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressDialog extends CenteredDialog implements ProgressListener {

	private ProgressBar progressBar;
	private Label info;
	private Label details;

	public ProgressDialog(String title) {
		super(title, StyleConstants.PROGRESS_DIALOG);
		initialize();
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
		info.setStyleName(StyleConstants.PROGRESS_DIALOG_DETAILS);
		panel.add(details);

		return panel;
	}

	public void onProgressUpdate(FileUploadStatus status) {
		progressBar.setProgress(status.getUploadedPercentage());
		details.setText(String.valueOf(status.getUploadedPercentage()));
	}

	public void onProgressUpdateFail(ServiceError error) {
		details.setText(error.name());
	}

}
