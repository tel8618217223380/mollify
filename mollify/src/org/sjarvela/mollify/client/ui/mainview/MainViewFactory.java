package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileActionProviderImpl;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.ui.WindowManager;

public class MainViewFactory {
	private MollifyService service;
	private Localizator localizator;

	public MainViewFactory(Localizator localizator, MollifyService service) {
		super();
		this.localizator = localizator;
		this.service = service;
	}

	public MainView createMainView(WindowManager windowManager) {
		FileActionProvider fileActionProvider = new FileActionProviderImpl(
				service);
		FileViewModel model = new FileViewModel();
		FileOperator fileOperator = new FileOperator(service, model);
		FileUploadHandler uploadHandler = new FileUploadHandlerImpl(service);

		FileDetailsPopupFactory fileDetailsPopupFactory = new FileDetailsPopupFactory(
				null, fileOperator, localizator);
		MainView mainView = new MainView(model, localizator,
				fileDetailsPopupFactory);
		MainViewPresenter presenter = new MainViewPresenter(service,
				windowManager, model, mainView, fileActionProvider,
				fileOperator, uploadHandler);
		new MainViewGlue(mainView, presenter);
		return mainView;
	}
}
