package org.sjarvela.mollify.client.filesystem;

public class DirectoryInfo {
	private final Directory directory;
	private final String Location;

	public DirectoryInfo(Directory directory, String location) {
		super();
		this.directory = directory;
		Location = location;
	}

	public Directory getDirectory() {
		return directory;
	}

	public String getLocation() {
		return Location;
	}
}
