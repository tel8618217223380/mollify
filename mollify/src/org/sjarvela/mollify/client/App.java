package org.sjarvela.mollify.client;

import org.sjarvela.mollify.client.service.FileService;
import org.sjarvela.mollify.client.ui.filemanager.FileManagerController;
import org.sjarvela.mollify.client.ui.filemanager.FileManagerModel;
import org.sjarvela.mollify.client.ui.filemanager.FileManagerView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint {

	public void onModuleLoad() {
		Window.setTitle(Localizator.getInstance().getStrings()
				.applicationTitle());

		FileService service = new FileService();
		FileManagerModel model = new FileManagerModel();
		FileManagerView view = new FileManagerView(model, Localizator
				.getInstance());
		new FileManagerController(service, model, view);

		RootPanel.get("mollify").add(view);
	}
}
