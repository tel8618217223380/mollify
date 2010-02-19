/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dropbox.impl;

import org.sjarvela.mollify.client.ui.dropbox.DropBox;

public class DropBoxGlue implements DropBox {
	private final DropBoxView view;
	private final DropBoxPresenter presenter;

	public DropBoxGlue(DropBoxView view, DropBoxPresenter presenter) {
		this.view = view;
		this.presenter = presenter;
	}

	@Override
	public boolean isVisible() {
		return view.isVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		view.setVisible(visible);
	}

}
