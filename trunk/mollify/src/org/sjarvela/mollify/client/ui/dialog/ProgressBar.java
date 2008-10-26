package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ProgressBar extends Composite {
	private VerticalPanel panel;
	private Element current;
	private Element left;

	public ProgressBar(String... styles) {
		super();

		panel = new VerticalPanel();
		this.initWidget(panel);

		this.setStyleName(StyleConstants.PROGRESS_BAR);
		for (String style : styles)
			this.addStyleName(style);

		createElement();
		setProgress(0);
	}

	private void createElement() {
		Element row = DOM.createTR();
		row.setClassName(StyleConstants.PROGRESS_BAR_TOTAL);
		
		current = DOM.createTD();
		current.setClassName(StyleConstants.PROGRESS_BAR_CURRENT);
		current.setInnerHTML("&nbsp;");
		
		left = DOM.createTD();
		left.setClassName(StyleConstants.PROGRESS_BAR_LEFT);
		left.setInnerHTML("&nbsp;");

		DOM.appendChild(this.getElement(), row);
		DOM.appendChild(row, current);
		DOM.appendChild(row, left);
	}

	public void setProgress(double progress) {
		String currentWidth = String.valueOf((int) progress) + "%";
		String leftWidth = String.valueOf(100 - (int) progress) + "%";

		current.setAttribute("width", currentWidth);
		left.setAttribute("width", leftWidth);
	}

}
