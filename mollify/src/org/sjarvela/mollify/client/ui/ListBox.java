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

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.action.ActionListener;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;

public class ListBox<T> extends com.google.gwt.user.client.ui.ListBox {
	private List<T> objects = new ArrayList();
	private Formatter<T> formatter = null;

	public void setFormatter(Formatter<T> formatter) {
		this.formatter = formatter;
	}

	public void setContent(List<T> content) {
		super.clear();
		objects = content;

		for (T t : content) {
			super.addItem(formatter != null ? formatter.format(t) : t
					.toString());
		}
	}

	public void setSelectedItem(T t) {
		super.setSelectedIndex(objects.indexOf(t));
	}

	public T getSelectedItem() {
		if (super.getSelectedIndex() < 0)
			return null;
		return objects.get(super.getSelectedIndex());
	}

	public void setAction(final ActionListener actionListener,
			final ResourceId action) {
		this.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				actionListener.onAction(action, getSelectedItem());
			}
		});
	}
}
