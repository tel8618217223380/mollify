package org.sjarvela.mollify.client.session.user;


public interface UserHandler {

	void addUser(String name, String password, UserPermissionMode mode);

	void editUser(User user, String name, UserPermissionMode mode);

}
