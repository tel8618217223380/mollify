package org.sjarvela.mollify.client.session;


public interface PasswordHandler {

	void changePassword(String oldPassword, String newPassword);

	void resetPassword(User user, String password);

}
