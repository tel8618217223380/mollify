package org.sjarvela.mollify.client.ui.mainview;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.request.ResultListener;

public class DefaultDirectoryProvider implements DirectoryProvider {
	private final FileSystemService fileSystemService;
	private final List<Directory> roots;

	public DefaultDirectoryProvider(List<Directory> roots,
			FileSystemService fileSystemService) {
		this.roots = roots;
		this.fileSystemService = fileSystemService;
	}

	public void getDirectories(Directory parent,
			ResultListener<List<Directory>> listener) {
		if (parent.isEmpty())
			listener.onSuccess(roots);
		else
			fileSystemService.getDirectories(parent, listener);
	}

}
