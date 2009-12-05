package org.sjarvela.mollify.client.session.file;

import org.sjarvela.mollify.client.session.user.User;

public interface FileItemUserPermissionHandler {

	void addFileItemUserPermission(User user, FilePermission mode);

	void editFileItemUserPermission(FileItemUserPermission permission);

}
