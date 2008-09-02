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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;

public class DataGrid extends FlexTable {
	private Element head;
	private Element headerRow;

	public DataGrid() {
		super();

		// create elements
		head = DOM.createTHead();
		headerRow = DOM.createTR();

		// insert into DOM
		DOM.insertChild(getElement(), head, 0);
		DOM.insertChild(head, headerRow, 0);
		DOM.setElementAttribute(getBodyElement(), "style",
				"overflow:auto;text-align: left;");
		DOM.setElementAttribute(head, "style", "text-align: left;");

	}

	public void setHeaderText(int column, String text, String id) {
		assureColumnCount(column);
		Element col = DOM.getChild(headerRow, column);
		col.setId("column-" + id);
		DOM.setElementAttribute(col, "class", "column-header");
		DOM.setInnerText(col, text);
	}

	private void assureColumnCount(int column) {
		int cellCount = DOM.getChildCount(headerRow);
		int required = column + 1 - cellCount;
		if (required > 0)
			addHeaderCells(head, required);
	}

	private native void addHeaderCells(Element table, int count) /*-{ 
		var row = table.rows[0];
		 
		for(var i = 0; i < count; i++){ 
			var cell = $doc.createElement("th"); 
			row.appendChild(cell);   
		} 
	}-*/;
}