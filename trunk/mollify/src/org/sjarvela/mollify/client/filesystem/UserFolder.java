package org.sjarvela.mollify.client.filesystem;

public class UserFolder extends FolderInfo {
	public static UserFolder create(String id, String name,
			String defaultName, String path) {
		UserFolder result = UserFolder.createObject().cast();
		result.putValues(id, name, path);
		result.putDefaultName(defaultName);
		return result;
	}

	protected UserFolder() {
	}

	public final native String getDefaultName() /*-{
		return this.folder_name;
	}-*/;

	private final native void putDefaultName(String defaultName) /*-{
		this.folder_name = defaultName;
	}-*/;
}
