/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext;

import com.google.gwt.user.client.ui.Widget;

public class ContextPopupHandler<T> {
	private final ContextPopup<T> contextPopup;
	private T current = null;

	public ContextPopupHandler(ContextPopup<T> contextPopup) {
		this.contextPopup = contextPopup;
	}

	public void onItemSelected(T t, Widget parent) {
		if (!t.equals(current)) {
			contextPopup.update(t, parent);
			contextPopup.showPopup();
			current = t;
		} else {
			this.current = null;
		}
	}

}
