package org.sjarvela.mollify.client.ui.dialog;

import com.google.gwt.user.client.ui.FormPanel;

public class CustomFormPanel extends FormPanel {
	@Override
	public void onFrameLoad() {
		super.onFrameLoad();
		//Window.alert(frame.getElement().getInnerHTML());
	}

}
