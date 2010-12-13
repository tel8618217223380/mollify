package org.sjarvela.mollify.client.ui.common.popup;

import com.google.gwt.user.client.ui.Widget;

public interface PopupPositioner {
	void setPositionOnShow(DropdownPopup popup, Widget parent, int offsetWidth,
			int offsetHeight);
}
