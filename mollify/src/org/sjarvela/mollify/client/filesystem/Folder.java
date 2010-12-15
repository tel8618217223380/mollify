package org.sjarvela.mollify.client.filesystem;

import org.sjarvela.mollify.client.filesystem.js.JsFolder;

import com.google.gwt.core.client.JavaScriptObject;

public class Folder extends FileSystemItem {
	public static Folder Empty = new Folder();
	public static FileSystemItem Parent = new Folder("..");

	private Folder(String name) {
		super("", "", name, "", "", false);
	}

	private Folder() {
		super("", "", "", "", "", false);
	}

	protected Folder(JsFolder dir) {
		this(dir.getId(), dir.getRootId(), dir.getName(), dir.getPath(), dir
				.getParentId(), dir.isProtected());
	}

	public Folder(String id, String rootId, String name, String path,
			String parentId, boolean isProtected) {
		super(id, rootId, name, path, parentId, isProtected);
	}

	@Override
	public boolean isFile() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return this == Empty;
	}

	public boolean isRoot() {
		return this.id.equals(this.rootId);
	}

	@Override
	public JavaScriptObject asJs() {
		return JsFolder.create(this);
	}
}
