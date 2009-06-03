package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;

public interface FileSystemActionHandlerFactory {

	FileSystemActionHandler create(Callback actionCallback);

}
