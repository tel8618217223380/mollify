package org.sjarvela.mollify.client.filesystem.handler;

import org.sjarvela.mollify.client.service.Callback;

public interface FileSystemActionHandlerFactory {

	FileSystemActionHandler create(Callback actionCallback);

}
