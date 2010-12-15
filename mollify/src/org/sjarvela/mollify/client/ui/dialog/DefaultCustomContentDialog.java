package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.user.client.ui.Widget;

public class DefaultCustomContentDialog extends CenteredDialog implements
		CustomContentDialog {
	private final Widget content;

	public DefaultCustomContentDialog(String title, String style,
			Widget content, final CustomDialogListener listener) {
		super(title, style);
		this.content = content;

		this.addViewListener(new ViewListener() {
			@Override
			public void onShow() {
				listener.onShow(DefaultCustomContentDialog.this);
			}
		});
		this.initialize();
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
