/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.directoryselector;

import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryListItemButton extends FlowPanel {
	private final Label left;
	private final Label center;
	private final Label right;
	private final Widget dropDown;

	private DirectoryListMenu menu = null;

	public DirectoryListItemButton(String itemStyle) {
		this.setStylePrimaryName(StyleConstants.DIRECTORY_LISTITEM_BUTTON);
		if (itemStyle != null)
			this.addStyleDependentName(itemStyle);

		MouseListener mouseListener = new MouseListenerAdapter() {
			@Override
			public void onMouseLeave(Widget sender) {
				DirectoryListItemButton.this.onMouseUp();
			}

			@Override
			public void onMouseDown(Widget sender, int x, int y) {
				DirectoryListItemButton.this.onMouseDown();
			}

			@Override
			public void onMouseUp(Widget sender, int x, int y) {
				DirectoryListItemButton.this.onMouseUp();
			}
		};

		left = createPart(StyleConstants.DIRECTORY_LISTITEM_BUTTON_L,
				itemStyle, mouseListener);
		center = createPart(StyleConstants.DIRECTORY_LISTITEM_BUTTON_C,
				itemStyle, mouseListener);
		dropDown = createDropdownButton(itemStyle);
		right = createPart(StyleConstants.DIRECTORY_LISTITEM_BUTTON_R,
				itemStyle, mouseListener);

		this.add(left);
		this.add(center);
		this.add(dropDown);
		this.add(right);
	}

	private Widget createDropdownButton(String itemStyle) {
		Label dropDown = new Label();
		dropDown.setStyleName(StyleConstants.DIRECTORY_LISTITEM_DROPDOWN);
		if (itemStyle != null)
			dropDown.addStyleDependentName(itemStyle);
		HoverDecorator.decorate(dropDown);

		dropDown.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				DirectoryListItemButton.this.onDropDownClicked();
			}
		});
		return dropDown;
	}

	protected void onDropDownClicked() {
		if (menu != null)
			menu.showMenu();
	}

	private Label createPart(String style, String itemStyle,
			MouseListener mouseListener) {
		Label label = new Label();
		label.setStylePrimaryName(style);
		if (itemStyle != null)
			this.addStyleDependentName(itemStyle);
		label.addMouseListener(mouseListener);
		return label;
	}

	private void onMouseDown() {
		right.addStyleDependentName(StyleConstants.PRESSED);
		center.addStyleDependentName(StyleConstants.PRESSED);
		dropDown.addStyleDependentName(StyleConstants.PRESSED);
		left.addStyleDependentName(StyleConstants.PRESSED);
	}

	protected void onMouseUp() {
		right.removeStyleDependentName(StyleConstants.PRESSED);
		center.removeStyleDependentName(StyleConstants.PRESSED);
		dropDown.removeStyleDependentName(StyleConstants.PRESSED);
		left.removeStyleDependentName(StyleConstants.PRESSED);
	}

	public void setText(String text) {
		center.setText(text);
	}

	public void addClickListener(ClickListener clickListener) {
		center.addClickListener(clickListener);
	}

	public void setDropdownMenu(DirectoryListMenu menu) {
		this.menu = menu;
	}
}
