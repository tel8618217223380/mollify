package org.sjarvela.mollify.client.session;


public interface UserHandler {

	void addUser(String name, String password, UserPermissionMode mode);

	void editUser(User user, String name, UserPermissionMode mode);

}
