package org.sjarvela.mollify.client.ui.mainview;

import java.util.ArrayList;
import java.util.List;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.filesystem.Directory;
import org.sjarvela.mollify.client.filesystem.File;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.directorymodel.DirectoryProvider;
import org.sjarvela.mollify.client.filesystem.handler.FileSystemActionHandler;
import org.sjarvela.mollify.client.filesystem.handler.RenameHandler;
import org.sjarvela.mollify.client.service.FileSystemService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.dialog.SelectDirectoryListener;

public class DefaultFileSystemActionHandler implements FileSystemActionHandler,
		RenameHandler {
	private WindowManager windowManager;
	private FileSystemService fileSystemService;
	private DirectoryProvider directoryProvider;
	private Callback actionCallback;

	public DefaultFileSystemActionHandler(WindowManager windowManager,
			FileSystemService fileSystemService,
			DirectoryProvider directoryProvider, Callback actionCallback) {
		this.windowManager = windowManager;
		this.fileSystemService = fileSystemService;
		this.directoryProvider = directoryProvider;
		this.actionCallback = actionCallback;
	}

	public void onAction(FileSystemItem item, FileSystemAction action) {
		if (item.isFile())
			onFileAction((File) item, action);
		else
			onDirectoryAction((Directory) item, action);
	}

	private void onFileAction(final File file, FileSystemAction action) {
		if (action.equals(FileSystemAction.download)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadUrl(file));
		} else if (action.equals(FileSystemAction.download_as_zip)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadAsZipUrl(file));
		} else if (action.equals(FileSystemAction.rename)) {
			windowManager.getDialogManager().showRenameDialog(file, this);
		} else {
			if (action.equals(FileSystemAction.copy)) {
				List<Directory> directoryList = new ArrayList(); // model.getDirectoryModel().getDirectoryList();

				windowManager.getDialogManager().showSelectFolderDialog(
						windowManager.getTextProvider().getStrings()
								.copyFileDialogTitle(),
						windowManager.getTextProvider().getMessages()
								.copyFileMessage(file.getName()),
						windowManager.getTextProvider().getStrings()
								.copyFileDialogAction(), directoryProvider,
						new SelectDirectoryListener() {
							public void onSelect(Directory selected) {
								copyFile(file, selected);
							}

							public boolean isDirectoryAllowed(
									Directory directory, List<Directory> path) {
								return !directory.getId().equals(
										file.getPathId());
							}
						}, directoryList);
			} else if (action.equals(FileSystemAction.move)) {
				List<Directory> directoryList = new ArrayList(); // model.getDirectoryModel().getDirectoryList();

				windowManager.getDialogManager().showSelectFolderDialog(
						windowManager.getTextProvider().getStrings()
								.moveFileDialogTitle(),
						windowManager.getTextProvider().getMessages()
								.moveFileMessage(file.getName()),
						windowManager.getTextProvider().getStrings()
								.moveFileDialogAction(), directoryProvider,
						new SelectDirectoryListener() {
							public void onSelect(Directory selected) {
								moveFile(file, selected);
							}

							public boolean isDirectoryAllowed(
									Directory directory, List<Directory> path) {
								return !directory.getId().equals(
										file.getPathId());
							}
						}, directoryList);
			} else if (action.equals(FileSystemAction.delete)) {
				String title = windowManager.getTextProvider().getStrings()
						.deleteFileConfirmationDialogTitle();
				String message = windowManager.getTextProvider().getMessages()
						.confirmFileDeleteMessage(file.getName());
				windowManager.getDialogManager().showConfirmationDialog(title,
						message,
						StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE,
						new ConfirmationListener() {
							public void onConfirm() {
								delete(file);
							}
						});
			} else {
				windowManager.getDialogManager().showInfo("ERROR",
						"Unsupported action:" + action.name());
			}
		}
	}

	private void onDirectoryAction(final Directory directory,
			FileSystemAction action) {
		if (action.equals(FileSystemAction.download_as_zip)) {
			windowManager.openDownloadUrl(fileSystemService
					.getDownloadAsZipUrl(directory));
		} else if (action.equals(FileSystemAction.rename)) {
			windowManager.getDialogManager().showRenameDialog(directory, this);
		} else if (action.equals(FileSystemAction.move)) {
			List<Directory> directoryList = new ArrayList(); // model.getDirectoryModel().getDirectoryList();

			windowManager.getDialogManager().showSelectFolderDialog(
					windowManager.getTextProvider().getStrings()
							.moveDirectoryDialogTitle(),
					windowManager.getTextProvider().getMessages()
							.moveDirectoryMessage(directory.getName()),
					windowManager.getTextProvider().getStrings()
							.moveDirectoryDialogAction(), directoryProvider,
					new SelectDirectoryListener() {
						public void onSelect(Directory selected) {
							moveDirectory(directory, selected);
						}

						public boolean isDirectoryAllowed(Directory candidate,
								List<Directory> path) {
							return !directory.equals(candidate)
									&& !directory.equals(candidate)
									&& !path.contains(directory);
						}
					}, directoryList);
		} else if (action.equals(FileSystemAction.delete)) {
			String title = windowManager.getTextProvider().getStrings()
					.deleteDirectoryConfirmationDialogTitle();
			String message = windowManager.getTextProvider().getMessages()
					.confirmDirectoryDeleteMessage(directory.getName());
			windowManager.getDialogManager().showConfirmationDialog(title,
					message, StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE,
					new ConfirmationListener() {
						public void onConfirm() {
							delete(directory);
						}
					});
		} else {
			windowManager.getDialogManager().showInfo("ERROR",
					"Unsupported action:" + action.name());
		}
	}

	public void rename(FileSystemItem item, String newName) {
		fileSystemService.rename(item, newName, createListener());
	}

	protected void copyFile(File file, Directory toDirectory) {
		fileSystemService.copy(file, toDirectory, createListener());
	}

	protected void moveFile(File file, Directory toDirectory) {
		fileSystemService.move(file, toDirectory, createListener());
	}

	protected void moveDirectory(Directory directory, Directory toDirectory) {
		fileSystemService.move(directory, toDirectory, createListener());
	}

	private void delete(FileSystemItem item) {
		fileSystemService.delete(item, createListener());
	}

	private ResultListener<Boolean> createListener() {
		return new ResultListener<Boolean>() {
			public void onFail(ServiceError error) {
				windowManager.getDialogManager().showError(error);
			}

			public void onSuccess(Boolean result) {
				actionCallback.onCallback();
			}
		};
	}
}
