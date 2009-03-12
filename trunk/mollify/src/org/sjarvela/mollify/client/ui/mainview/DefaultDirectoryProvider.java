package org.sjarvela.mollify.client.ui.mainview;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.ResultListener;

public class DefaultDirectoryProvider implements DirectoryProvider {
	private final FileSystemService fileSystemService;
	private List<Directory> roots = null;

	public DefaultDirectoryProvider(FileSystemService fileSystemService) {
		this.fileSystemService = fileSystemService;
	}

	public void getDirectories(Directory parent,
			ResultListener<List<Directory>> listener) {
		if (parent.isEmpty())
			getRootDirectories(listener);
		else
			fileSystemService.getDirectories(parent, listener);
	}

	private void getRootDirectories(
			final ResultListener<List<Directory>> listener) {
		if (roots != null) {
			listener.onSuccess(roots);
			return;
		}

		fileSystemService
				.getRootDirectories(new ResultListener<List<Directory>>() {
					public void onFail(ServiceError error) {
						listener.onFail(error);
					}

					public void onSuccess(List<Directory> result) {
						DefaultDirectoryProvider.this.roots = result;
						listener.onSuccess(result);
					}
				});
	}

}
