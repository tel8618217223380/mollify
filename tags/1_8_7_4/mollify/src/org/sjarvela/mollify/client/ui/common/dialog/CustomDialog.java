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

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.ActionButton;
import org.sjarvela.mollify.client.ui.dialog.DialogMoveListener;

import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class CustomDialog extends DialogBox {
	private final List<ViewListener> viewListeners = new ArrayList();
	private final List<DialogMoveListener> dialogMoveListeners = new ArrayList();

	private Panel title;

	public CustomDialog(String title, String style) {
		super(false, true);

		this.setStylePrimaryName(StyleConstants.DIALOG);
		this.addStyleDependentName(style);

		createTitle(title);
	}

	private void createTitle(String titleText) {
		title = new HorizontalPanel();
		// title.add(caption);
		// adopt(title);

		Element td = getCellElement(0, 1);
		Node caption = td.getFirstChild();
		td.removeChild(caption);

		title.getElement().appendChild(caption);
		DOM.appendChild(td, title.getElement());
	}

	// @Override
	// public void onBrowserEvent(Event event) {
	// switch (event.getTypeInt()) {
	// case Event.ONMOUSEDOWN:
	// case Event.ONMOUSEUP:
	// case Event.ONMOUSEMOVE:
	// case Event.ONMOUSEOVER:
	// case Event.ONMOUSEOUT:
	// if (isCloseButtonEvent(event)) {
	// DomEvent.fireNativeEvent(event, this, this.getElement());
	// return;
	// }
	// }
	//
	// super.onBrowserEvent(event);
	// }

	// private boolean isCloseButtonEvent(Event event) {
	// return closeActionElement != null
	// && Element.is(event.getEventTarget())
	// && Element.as(event.getEventTarget())
	// .equals(closeActionElement);
	// }

	// private Widget createCloseAction() {
	// Label p = new Label();
	// p.setStylePrimaryName(StyleConstants.DIALOG_TITLE_CLOSE);
	// HoverDecorator.decorate(p);
	// p.addClickHandler(new ClickHandler() {
	// @Override
	// public void onClick(ClickEvent event) {
	// CustomDialog.this.hide();
	// }
	// });
	// return p;
	// }

	protected void initialize() {
		VerticalPanel content = new VerticalPanel();
		content.add(createContent());

		Widget buttons = createButtons();
		if (buttons != null)
			content.add(buttons);
		this.add(content);
	}

	protected Widget createButtons() {
		return null;
	}

	protected abstract Widget createContent();

	public void addViewListener(ViewListener listener) {
		this.viewListeners.add(listener);
	}

	@Override
	public void show() {
		super.show();
		for (ViewListener listener : viewListeners)
			listener.onShow();
	}

	protected Button createButton(String title, ClickHandler handler,
			String style) {
		Button button = new Button(title);
		button.setStylePrimaryName(StyleConstants.DIALOG_BUTTON);
		button.addStyleDependentName(style);
		button.addClickHandler(handler);
		return button;
	}

	public ActionButton createButton(String title, String id, String style,
			ActionListener actionListener, ResourceId actionId) {
		ActionButton actionButton = new ActionButton(title, id, style);
		actionButton.setAction(actionListener, actionId);
		return actionButton;
	}

	public void addMoveListener(DialogMoveListener l) {
		this.dialogMoveListeners.add(l);
	}

	@Override
	protected void endDragging(MouseUpEvent event) {
		super.endDragging(event);
		for (DialogMoveListener l : this.dialogMoveListeners)
			l.onDialogMoved();
	}
}
