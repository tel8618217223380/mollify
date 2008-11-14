package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.data.JsDirectory;
import org.sjarvela.mollify.client.data.JsFile;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class DirectoriesAndFiles extends JavaScriptObject {
	protected DirectoriesAndFiles() {
	}

	public final native JsArray<JsDirectory> getDirectories() /*-{
		return this.directories;
	}-*/;

	public final native JsArray<JsFile> getFiles() /*-{
		return this.files;
	}-*/;
}
