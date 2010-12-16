package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.common.dialog.ResizableDialog;

import com.google.gwt.user.client.ui.Widget;

public class DefaultCustomContentDialog extends ResizableDialog implements
		CustomContentDialog {
	private final Widget content;

	public DefaultCustomContentDialog(String title, String style,
			Widget content, final CustomDialogListener listener) {
		super(title, style);
		this.content = content;

		this.addViewListener(new ViewListener() {
			@Override
			public void onShow() {
				DefaultCustomContentDialog.this.setMinimumSizeToCurrent();
				listener.onShow(DefaultCustomContentDialog.this);
			}
		});
		this.initialize();
		this.center();
	}

	@Override
	protected Widget createContent() {
		return content;
	}

	@Override
	public void close() {
		hide();
	}

}
