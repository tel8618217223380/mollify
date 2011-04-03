/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common.popup;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class DropdownPopup extends PopupPanel {
	private PopupPositioner positioner;

	protected final Panel container;
	protected Element parent;

	public DropdownPopup(Element parent) {
		this(parent, null);
	}

	public DropdownPopup(Element parent, PopupPositioner positioner) {
		super(true);

		this.parent = parent;
		this.positioner = positioner;

		this.container = createContainer();
		setWidget(container);
	}

	protected Panel createContainer() {
		return new FlowPanel();
	}

	protected void addItem(Widget item) {
		container.add(item);
	}

	public void removeAllMenuItems() {
		container.clear();
	}

	public void setPositioner(PopupPositioner positioner) {
		this.positioner = positioner;
	}

	public void showPopup() {
		this.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				if (positioner != null)
					positioner.setPositionOnShow(DropdownPopup.this, parent,
							offsetWidth, offsetHeight);
				else if (parent != null) {
					position(parent, offsetWidth, offsetHeight);
				}
			}
		});
	}

	// direct copy from PopupPanel class in GWT, it just has weird class
	// requirement on UIObject instead of Element
	private void position(final Element relativeObject, int offsetWidth,
			int offsetHeight) {
		// Calculate left position for the popup. The computation for
		// the left position is bidi-sensitive.

		int textBoxOffsetWidth = relativeObject.getOffsetWidth();

		// Compute the difference between the popup's width and the
		// textbox's width
		int offsetWidthDiff = offsetWidth - textBoxOffsetWidth;

		int left;

		if (LocaleInfo.getCurrentLocale().isRTL()) { // RTL case

			int textBoxAbsoluteLeft = relativeObject.getAbsoluteLeft();

			// Right-align the popup. Note that this computation is
			// valid in the case where offsetWidthDiff is negative.
			left = textBoxAbsoluteLeft - offsetWidthDiff;

			// If the suggestion popup is not as wide as the text box, always
			// align to the right edge of the text box. Otherwise, figure out
			// whether
			// to right-align or left-align the popup.
			if (offsetWidthDiff > 0) {

				// Make sure scrolling is taken into account, since
				// box.getAbsoluteLeft() takes scrolling into account.
				int windowRight = Window.getClientWidth()
						+ Window.getScrollLeft();
				int windowLeft = Window.getScrollLeft();

				// Compute the left value for the right edge of the textbox
				int textBoxLeftValForRightEdge = textBoxAbsoluteLeft
						+ textBoxOffsetWidth;

				// Distance from the right edge of the text box to the right
				// edge
				// of the window
				int distanceToWindowRight = windowRight
						- textBoxLeftValForRightEdge;

				// Distance from the right edge of the text box to the left edge
				// of the
				// window
				int distanceFromWindowLeft = textBoxLeftValForRightEdge
						- windowLeft;

				// If there is not enough space for the overflow of the popup's
				// width to the right of the text box and there IS enough space
				// for the
				// overflow to the right of the text box, then left-align the
				// popup.
				// However, if there is not enough space on either side, stick
				// with
				// right-alignment.
				if (distanceFromWindowLeft < offsetWidth
						&& distanceToWindowRight >= offsetWidthDiff) {
					// Align with the left edge of the text box.
					left = textBoxAbsoluteLeft;
				}
			}
		} else { // LTR case

			// Left-align the popup.
			left = relativeObject.getAbsoluteLeft();

			// If the suggestion popup is not as wide as the text box, always
			// align to
			// the left edge of the text box. Otherwise, figure out whether to
			// left-align or right-align the popup.
			if (offsetWidthDiff > 0) {
				// Make sure scrolling is taken into account, since
				// box.getAbsoluteLeft() takes scrolling into account.
				int windowRight = Window.getClientWidth()
						+ Window.getScrollLeft();
				int windowLeft = Window.getScrollLeft();

				// Distance from the left edge of the text box to the right edge
				// of the window
				int distanceToWindowRight = windowRight - left;

				// Distance from the left edge of the text box to the left edge
				// of the
				// window
				int distanceFromWindowLeft = left - windowLeft;

				// If there is not enough space for the overflow of the popup's
				// width to the right of hte text box, and there IS enough space
				// for the
				// overflow to the left of the text box, then right-align the
				// popup.
				// However, if there is not enough space on either side, then
				// stick with
				// left-alignment.
				if (distanceToWindowRight < offsetWidth
						&& distanceFromWindowLeft >= offsetWidthDiff) {
					// Align with the right edge of the text box.
					left -= offsetWidthDiff;
				}
			}
		}

		// Calculate top position for the popup

		int top = relativeObject.getAbsoluteTop();

		// Make sure scrolling is taken into account, since
		// box.getAbsoluteTop() takes scrolling into account.
		int windowTop = Window.getScrollTop();
		int windowBottom = Window.getScrollTop() + Window.getClientHeight();

		// Distance from the top edge of the window to the top edge of the
		// text box
		int distanceFromWindowTop = top - windowTop;

		// Distance from the bottom edge of the window to the bottom edge of
		// the text box
		int distanceToWindowBottom = windowBottom
				- (top + relativeObject.getOffsetHeight());

		// If there is not enough space for the popup's height below the text
		// box and there IS enough space for the popup's height above the text
		// box, then then position the popup above the text box. However, if
		// there
		// is not enough space on either side, then stick with displaying the
		// popup below the text box.
		if (distanceToWindowBottom < offsetHeight
				&& distanceFromWindowTop >= offsetHeight) {
			top -= offsetHeight;
		} else {
			// Position above the text box
			top += relativeObject.getOffsetHeight();
		}
		setPopupPosition(left, top);
	}

	@Override
	public void show() {
		onShow();
		super.show();
	}

	protected void onShow() {
	}

	public Element getParentWidget() {
		return parent;
	}

	public void setParentElement(Element parent) {
		if (parent != null)
			this.removeAutoHidePartner(parent);
		this.parent = parent;
		this.addAutoHidePartner(parent);
	}
}
