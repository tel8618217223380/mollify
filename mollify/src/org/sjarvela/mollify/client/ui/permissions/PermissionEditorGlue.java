/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.permissions;

import org.sjarvela.mollify.client.ui.ActionDelegator;
import org.sjarvela.mollify.client.ui.ActionHandler;
import org.sjarvela.mollify.client.ui.ViewListener;

public class PermissionEditorGlue {

	public PermissionEditorGlue(final PermissionEditorPresenter presenter,
			PermissionEditorView view, ActionDelegator actionDelegator) {
		actionDelegator.setActionHandler(PermissionEditorView.Actions.close,
				new ActionHandler() {
					public void onAction() {
						presenter.onClose();
					}
				});

		view.addViewListener(new ViewListener() {
			public void onShow() {
				presenter.initialize();
			}
		});

		view.show();
	}

}
