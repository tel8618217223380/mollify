/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.popup;

import org.sjarvela.mollify.client.ui.common.popup.PopupPositioner;

import com.google.gwt.user.client.ui.Widget;

public interface ContextPopup<T> {
	void showPopup();

	void hidePopup();

	void addPopupListener(ContextPopupListener contextPopupListener);

	void setPopupPositioner(PopupPositioner positioner);

	void update(T t, Widget parent);
}
