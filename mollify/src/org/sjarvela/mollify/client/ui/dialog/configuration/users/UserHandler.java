package org.sjarvela.mollify.client.ui.dialog.configuration.users;

import org.sjarvela.mollify.client.session.PermissionMode;

public interface UserHandler {

	void addUser(String name, String password, PermissionMode mode);

	void editUser(String name, PermissionMode mode);

}
