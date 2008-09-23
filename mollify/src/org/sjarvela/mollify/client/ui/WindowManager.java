package org.sjarvela.mollify.client.ui;

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.data.Directory;
import org.sjarvela.mollify.client.data.ErrorValue;
import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.RenameHandler;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.dialog.ConfirmationDialog;
import org.sjarvela.mollify.client.ui.dialog.FileUploadDialog;
import org.sjarvela.mollify.client.ui.dialog.InfoDialog;
import org.sjarvela.mollify.client.ui.dialog.LoginDialog;
import org.sjarvela.mollify.client.ui.dialog.LoginHandler;
import org.sjarvela.mollify.client.ui.dialog.RenameDialog;
import org.sjarvela.mollify.client.ui.mainview.MainViewFactory;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WindowManager {
	private static final String FILEMANAGER_DOWNLOAD_PANEL_ID = "mollify-download-panel";
	private static final String FILEMANAGER_DOWNLOAD_FRAME_ID = "mollify-download-frame";

	private final RootPanel rootPanel;
	private final Localizator localizator;
	private final MainViewFactory mainViewFactory;

	public WindowManager(RootPanel rootPanel, Localizator localizator,
			MainViewFactory mainViewFactory) {
		this.rootPanel = rootPanel;
		this.localizator = localizator;
		this.mainViewFactory = mainViewFactory;
	}

	public Localizator getLocalizator() {
		return localizator;
	}

	public void showMainView() {
		rootPanel.add(mainViewFactory.createMainView(this));
		rootPanel.add(createDownloadFrame());
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

	public void showLoginDialog(LoginHandler loginHandler) {
		new LoginDialog(localizator, loginHandler);
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

	public void showConfirmationDialog(String title, String message,
			String style, ConfirmationListener listener) {
		new ConfirmationDialog(localizator, title, message, style, listener);
	}

	/* UTILITIES */

	private native void setFrameUrl(String id, String url) /*-{
		$doc.getElementById(id).src=url;
	}-*/;

}
