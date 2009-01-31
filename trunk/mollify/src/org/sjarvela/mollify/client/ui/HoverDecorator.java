/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.Widget;

public class HoverDecorator {

	private static MouseListenerAdapter listener;

	static MouseListenerAdapter getListener() {
		if (listener == null) {
			listener = new MouseListenerAdapter() {

				@Override
				public void onMouseEnter(Widget sender) {
					sender.addStyleDependentName(StyleConstants.HOVER);
				}

				@Override
				public void onMouseLeave(Widget sender) {
					clear(sender);
				}
			};
		}
		return listener;
	}

	public static void decorate(Label decorated) {
		decorated.addMouseListener(getListener());
	}

	public static void clear(Widget decorated) {
		decorated.removeStyleDependentName(StyleConstants.HOVER);
	}

}
