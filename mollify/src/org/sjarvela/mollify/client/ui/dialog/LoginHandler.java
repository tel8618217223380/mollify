package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.ConfirmationListener;

public interface LoginHandler {
	public void onLogin(String userName, String password,
			ConfirmationListener listener);
}
