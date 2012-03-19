package org.sjarvela.mollify.client.ui.login;

public interface LoginViewListener {
	void onLogin(String username, String password, boolean remember);
	void onResetPassword(String email);
}
