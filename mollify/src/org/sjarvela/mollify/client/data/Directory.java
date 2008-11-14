package org.sjarvela.mollify.client.data;

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
		super(dir.getId(), dir.getName());
		this.dir = dir;
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
