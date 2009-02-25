package org.sjarvela.mollify.client.filesystem;

import org.sjarvela.mollify.client.filesystem.js.JsDirectory;

public class Directory extends FileSystemItem {
	public static Directory Empty = new Directory();
	public static FileSystemItem Parent = new Directory("..");

	@SuppressWarnings("unused")
	private final JsDirectory dir;

	private Directory(String name) {
		super("", name);
		dir = null;
	}

	private Directory() {
		super("", "");
		dir = null;
	}

	protected Directory(JsDirectory dir) {
		this(dir.getId(), dir.getName());
	}

	public Directory(String id, String name) {
		super(id, name);
		dir = null;
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
