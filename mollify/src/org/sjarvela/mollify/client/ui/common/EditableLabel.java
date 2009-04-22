/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common;

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class EditableLabel extends Composite {
	private Label label;
	private TextArea editor;

	public EditableLabel(String style) {
		super();
		initWidget(createContent(style));

		this.setStylePrimaryName(StyleConstants.EDITABLE_LABEL);
		if (style != null)
			this.addStyleDependentName(style);

		setEditable(false);
	}

	private Widget createContent(String style) {
		Panel panel = new FlowPanel();
		panel.setStyleName(StyleConstants.EDITABLE_LABEL_PANEL);

		label = new Label();
		label.setStylePrimaryName(StyleConstants.EDITABLE_LABEL + "-label");
		if (style != null)
			label.addStyleDependentName(style);
		panel.add(label);

		editor = new TextArea();
		editor.setStylePrimaryName(StyleConstants.EDITABLE_LABEL + "-editor");
		if (style != null)
			editor.addStyleDependentName(style);
		panel.add(editor);

		return panel;
	}

	public void setText(String text) {
		label.setText(text);
		editor.setText(text);
	}

	public void setEditable(boolean isEditable) {
		editor.setVisible(isEditable);
		label.setVisible(!isEditable);
		
		if (isEditable)
			editor.setFocus(isEditable);
	}

	public String getText() {
		return editor.getText();
	}
}
