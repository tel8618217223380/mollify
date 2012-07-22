package org.sjarvela.mollify.client.ui.mainview.impl;

import org.sjarvela.mollify.client.filesystem.Folder;

import com.google.gwt.core.client.JavaScriptObject;

public interface FolderInfoRequestDataProvider {

	JavaScriptObject getDataRequest(Folder folder);

}
