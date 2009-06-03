package org.sjarvela.mollify.client.filesystem.handler;

import org.sjarvela.mollify.client.Callback;

public interface FileSystemActionHandlerFactory {

	FileSystemActionHandler create(Callback actionCallback);

}
