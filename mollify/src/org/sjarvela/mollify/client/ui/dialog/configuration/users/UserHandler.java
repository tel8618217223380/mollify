package org.sjarvela.mollify.client.ui.dialog.configuration.users;

import org.sjarvela.mollify.client.session.PermissionMode;
import org.sjarvela.mollify.client.session.User;

public interface UserHandler {

	void addUser(String name, String password, PermissionMode mode);

	void editUser(User user, String name, PermissionMode mode);

}
