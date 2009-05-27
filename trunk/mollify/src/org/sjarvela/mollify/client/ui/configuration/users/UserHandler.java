package org.sjarvela.mollify.client.ui.configuration.users;

import org.sjarvela.mollify.client.session.UserPermissionMode;
import org.sjarvela.mollify.client.session.User;

public interface UserHandler {

	void addUser(String name, String password, UserPermissionMode mode);

	void editUser(User user, String name, UserPermissionMode mode);

}
