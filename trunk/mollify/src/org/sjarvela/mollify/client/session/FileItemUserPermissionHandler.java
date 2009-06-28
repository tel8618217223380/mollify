package org.sjarvela.mollify.client.session;

public interface FileItemUserPermissionHandler {

	void addFileItemUserPermission(User user, FilePermissionMode mode);

	void editFileItemUserPermission(FileItemUserPermission userPermission);

}
