/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common.dialog;

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ResizableDialog extends Dialog implements
		MousePanelListener {
	private Panel content;

	private int mouseDownX = -1;
	private int mouseDownY = -1;

	private int contentWidth = -1;
	private int contentHeight = -1;

	public ResizableDialog(String title, String style) {
		super(title, style);
	}

	@Override
	protected void initialize() {
		content = new VerticalPanel();
		content.add(createContent());

		HorizontalPanel lower = new HorizontalPanel();
		lower.setWidth("100%");
		Widget buttons = createButtons();
		if (buttons != null)
			lower.add(buttons);
		lower.add(createResizeWidget());
		content.add(lower);

		this.add(content);
	}

	protected Widget getSizedWidget() {
		return content;
	}

	private void resetSize() {
		contentWidth = getSizedWidget().getElement().getClientWidth();
		contentHeight = getSizedWidget().getElement().getClientHeight();
	}

	private Widget createResizeWidget() {
		Panel p = new MousePanel(this);
		p.setStylePrimaryName(StyleConstants.DIALOG_RESIZER);
		return p;
	}

	@Override
	public void onMouseDown(int x, int y) {
		if (contentWidth < 0)
			resetSize();
		mouseDownX = x;
		mouseDownY = y;
	}

	@Override
	public void onMouseUp(int x, int y) {
		mouseDownX = -1;
		mouseDownY = -1;
		resetSize();
	}

	@Override
	public void onMouseDrag(int x, int y) {
		int offsetX = mouseDownX - x;
		int offsetY = mouseDownY - y;

		getSizedWidget().setPixelSize(contentWidth - offsetX,
				contentHeight - offsetY);
	}
}
