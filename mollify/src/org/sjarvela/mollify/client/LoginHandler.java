package org.sjarvela.mollify.client;


public interface LoginHandler {
	public void onLogin(String userName, String password,
			ConfirmationListener listener);
}
