package org.sjarvela.mollify.client.ui;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DropDownButton extends Composite {
	private Label button;
	private DropdownPopup menu;

	public DropDownButton(String title) {
		button = new Label();
		button.setStyleName(StyleConstants.DIRECTORY_LIST_DROPDOWN);

		menu = new DropdownPopup(this.getElement(), button.getElement());

		button.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				menu.show();
			}
		});
	}
}
