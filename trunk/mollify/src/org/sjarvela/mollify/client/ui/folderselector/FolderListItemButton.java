/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.folderselector;

import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;
import org.sjarvela.mollify.client.ui.common.Tooltip;
import org.sjarvela.mollify.client.ui.common.TooltipTarget;
import org.sjarvela.mollify.client.ui.common.popup.PopupClickTrigger;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;

public class FolderListItemButton extends FlowPanel {
	private final Label left;
	private final Label center;
	private final Label right;
	private final Button dropDown;
	private FolderListMenu menu = null;

	public FolderListItemButton(String itemStyle) {
		this.setStylePrimaryName(StyleConstants.DIRECTORY_LISTITEM_BUTTON);
		if (itemStyle != null)
			this.addStyleDependentName(itemStyle);

		MouseOutHandler mouseOutHandler = new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				FolderListItemButton.this.onMouseUp();
			}
		};

		MouseDownHandler mouseDownHandler = new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				FolderListItemButton.this.onMouseDown();
			}
		};

		MouseUpHandler mouseUpHandler = new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				FolderListItemButton.this.onMouseUp();
			}
		};

		left = createPart(StyleConstants.DIRECTORY_LISTITEM_BUTTON_L,
				itemStyle, mouseOutHandler, mouseDownHandler, mouseUpHandler);
		center = createPart(StyleConstants.DIRECTORY_LISTITEM_BUTTON_C,
				itemStyle, mouseOutHandler, mouseDownHandler, mouseUpHandler);
		dropDown = createDropdownButton(itemStyle);
		right = createPart(StyleConstants.DIRECTORY_LISTITEM_BUTTON_R,
				itemStyle, mouseOutHandler, mouseDownHandler, mouseUpHandler);

		this.add(left);
		this.add(center);
		this.add(dropDown);
		this.add(right);
	}

	private Button createDropdownButton(String itemStyle) {
		Button dropDown = new Button();
		dropDown.setStyleName(StyleConstants.DIRECTORY_LISTITEM_DROPDOWN);

		if (itemStyle != null)
			dropDown.addStyleDependentName(itemStyle);
		HoverDecorator.decorate(dropDown);

		return dropDown;
	}

	private Label createPart(String style, String itemStyle,
			MouseOutHandler mouseOutHandler, MouseDownHandler mouseDownHandler,
			MouseUpHandler mouseUpHandler) {
		Label label = new Label();
		label.setStylePrimaryName(style);
		if (itemStyle != null)
			this.addStyleDependentName(itemStyle);
		label.addMouseOutHandler(mouseOutHandler);
		label.addMouseDownHandler(mouseDownHandler);
		label.addMouseUpHandler(mouseUpHandler);
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

	public void addClickHandler(ClickHandler clickHandler) {
		center.addClickHandler(clickHandler);
	}

	public void setDropdownMenu(FolderListMenu menu) {
		this.menu = menu;
		new PopupClickTrigger(dropDown, menu);
	}

	public void addDropdownTooltip(Tooltip tooltip) {
		tooltip.attach(new TooltipTarget() {
			public FocusWidget getWidget() {
				return dropDown;
			}

			public boolean showTooltip() {
				return menu != null && !menu.isShowing();
			}
		});
	}
}
