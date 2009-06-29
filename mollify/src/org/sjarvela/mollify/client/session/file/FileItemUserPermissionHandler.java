package org.sjarvela.mollify.client.session.file;

import org.sjarvela.mollify.client.session.user.User;

public interface FileItemUserPermissionHandler {

	void addFileItemUserPermission(User user, FilePermissionMode mode);

	void editFileItemUserPermission(FileItemUserPermission permission);

}
