package org.sjarvela.mollify.client.ui;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class BorderedControl extends FlexTable {
	private static String[][] STYLES = new String[][] {
			{ StyleConstants.BORDERED_CONTROL_PADDING_NW,
					StyleConstants.BORDERED_CONTROL_PADDING_N,
					StyleConstants.BORDERED_CONTROL_PADDING_NE },
			{ StyleConstants.BORDERED_CONTROL_PADDING_W, "",
					StyleConstants.BORDERED_CONTROL_PADDING_E },
			{ StyleConstants.BORDERED_CONTROL_PADDING_SW,
					StyleConstants.BORDERED_CONTROL_PADDING_S,
					StyleConstants.BORDERED_CONTROL_PADDING_SE } };

	public BorderedControl(String style) {
		this.setStyleName(style);
		createCells();
	}

	private void createCells() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				this.setText(x, y, "");
				String style = STYLES[x][y];
				if (style.length() > 0)
					this.getCellFormatter().setStyleName(x, y, style);
			}
		}
	}

	public void setContent(Widget widget) {
		this.setWidget(1, 1, widget);
	}
}
