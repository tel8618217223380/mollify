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

import com.google.gwt.dom.client.Element;

public class ContextPopupHandler<T> {
	private final ContextPopup<T> contextPopup;
	private T current = null;

	public ContextPopupHandler(ContextPopup<T> contextPopup) {
		this.contextPopup = contextPopup;
		this.contextPopup.addPopupListener(new ContextPopupListener() {
			public void onPopupClosed() {
				current = null;
			}

			public void onPopupShow() {
			}
		});
	}

	public void onItemSelected(T t, Element parent) {
		if (t.equals(current)) {
			current = null;
			contextPopup.hidePopup();
		} else {
			contextPopup.update(t, parent);
			contextPopup.showPopup();
			current = t;
		}
	}

	public void onOpenItemMenu(T t, Element parent) {
		contextPopup.showMenu(t, parent);
	}
}
