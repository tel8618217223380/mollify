package org.sjarvela.mollify.client.filesystem;

import org.sjarvela.mollify.client.filesystem.js.JsFolder;

public class Folder extends FileSystemItem {
	public static Folder Empty = new Folder();
	public static FileSystemItem Parent = new Folder("..");

	private Folder(String name) {
		super("", name, "", "");
	}

	private Folder() {
		super("", "", "", "");
	}

	protected Folder(JsFolder dir) {
		this(dir.getId(), dir.getName(), dir.getPath(), dir.getParentId());
	}

	public Folder(String id, String name, String path, String parentId) {
		super(id, name, path, parentId);
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return this == Empty;
	}
}
