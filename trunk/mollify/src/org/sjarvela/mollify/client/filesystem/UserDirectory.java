package org.sjarvela.mollify.client.filesystem;

public class UserDirectory extends DirectoryInfo {
	public static UserDirectory create(String id, String name,
			String defaultName, String path) {
		UserDirectory result = UserDirectory.createObject().cast();
		result.putValues(id, name, path);
		result.putDefaultName(defaultName);
		return result;
	}

	protected UserDirectory() {
	}

	public final native String getDefaultName() /*-{
		return this.folder_name;
	}-*/;

	private final native void putDefaultName(String defaultName) /*-{
		this.folder_name = defaultName;
	}-*/;
}
