package org.sjarvela.mollify.client.session.file;

import org.sjarvela.mollify.client.session.user.UserBase;

public interface FileItemUserPermissionHandler {

	void addFileItemUserPermission(UserBase userOrGroup, FilePermission mode);

	void editFileItemUserPermission(FileItemUserPermission permission);

}
