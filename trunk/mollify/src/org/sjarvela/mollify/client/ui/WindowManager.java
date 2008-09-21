package org.sjarvela.mollify.client.ui;

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.ErrorValue;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileActionProviderImpl;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.RenameHandler;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.dialog.ConfirmationDialog;
import org.sjarvela.mollify.client.ui.dialog.FileUploadDialog;
import org.sjarvela.mollify.client.ui.dialog.InfoDialog;
import org.sjarvela.mollify.client.ui.dialog.RenameDialog;
import org.sjarvela.mollify.client.ui.mainview.MainView;
import org.sjarvela.mollify.client.ui.mainview.MainViewGlue;
import org.sjarvela.mollify.client.ui.mainview.MainViewModel;
import org.sjarvela.mollify.client.ui.mainview.MainViewPresenter;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WindowManager {
	private static final String FILEMANAGER_DOWNLOAD_PANEL_ID = "mollify-download-panel";
	private static final String FILEMANAGER_DOWNLOAD_FRAME_ID = "mollify-download-frame";

	private final MollifyService service;
	private final Localizator localizator;

	public WindowManager(MollifyService mollifyService, Localizator localizator) {
		this.service = mollifyService;
		this.localizator = localizator;
	}

	public void addDownloadFrame(Panel panel) {
		panel.add(createDownloadFrame());
	}

	public void addMainView(Panel panel) {
		FileActionProvider fileActionProvider = new FileActionProviderImpl(
				service);

		MainViewModel model = new MainViewModel();
		MainView mainView = new MainView(model, localizator);
		MainViewPresenter presenter = new MainViewPresenter(service, this,
				model, mainView, fileActionProvider);

		new MainViewGlue(mainView, presenter);
		panel.add(mainView);
	}

	private Widget createDownloadFrame() {
		SimplePanel downloadPanel = new SimplePanel();
		downloadPanel.getElement().setId(FILEMANAGER_DOWNLOAD_PANEL_ID);
		downloadPanel.getElement().setAttribute("style",
				"visibility:collapse; height: 0px;");

		Element downloadFrame = DOM.createIFrame();
		downloadFrame
				.setAttribute("style", "visibility:collapse; height: 0px;");
		downloadFrame.setId(FILEMANAGER_DOWNLOAD_FRAME_ID);

		downloadPanel.getElement().appendChild(downloadFrame);

		return downloadPanel;
	}

	public void openDownloadUrl(String url) {
		setFrameUrl(FILEMANAGER_DOWNLOAD_FRAME_ID, url);
	}

	public void openUrlInNewWindow(String url) {
		Window.open(url, "_blank", "");
	}

	public void showRenameDialog(File file, RenameHandler fileHandler) {
		new RenameDialog(file, localizator, fileHandler);
	}

	public void openUploadDialog(Directory directory,
			FileActionProvider fileActionProvider, FileUploadHandler fileHandler) {
		new FileUploadDialog(directory, localizator, fileActionProvider,
				fileHandler);
	}

	public void showError(ServiceError error) {
		new InfoDialog(localizator, localizator.getStrings()
				.infoDialogErrorTitle(), error.getMessage(localizator),
				StyleConstants.INFO_DIALOG_TYPE_ERROR);
	}

	public void showError(ErrorValue errorResult) {
		new InfoDialog(localizator, localizator.getStrings()
				.infoDialogErrorTitle(), localizator
				.getErrorMessage(errorResult),
				StyleConstants.INFO_DIALOG_TYPE_ERROR);
	}

	public void showInfo(String title, String text) {
		new InfoDialog(localizator, title, text,
				StyleConstants.INFO_DIALOG_TYPE_INFO);
	}

	public void showFileDeleteConfirmationDialog(File file,
			ConfirmationListener listener) {
		new ConfirmationDialog(localizator, localizator.getStrings()
				.deleteFileConfirmationDialogTitle(), localizator.getMessages()
				.confirmFileDeleteMessage(file.getName()),
				StyleConstants.CONFIRMATION_DIALOG_TYPE_DELETE_FILE, listener);
	}

	/* UTILITIES */

	private native void setFrameUrl(String id, String url) /*-{
		$doc.getElementById(id).src=url;
	}-*/;
}
