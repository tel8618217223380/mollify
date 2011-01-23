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
