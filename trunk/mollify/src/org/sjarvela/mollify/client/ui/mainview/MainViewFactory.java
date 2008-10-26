package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.impl.FileActionHandlerImpl;
import org.sjarvela.mollify.client.file.impl.FileActionProviderImpl;
import org.sjarvela.mollify.client.file.impl.FileUploadHandlerImpl;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.ui.WindowManager;
import org.sjarvela.mollify.client.ui.fileaction.FileDetailsPopupFactory;

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
		FileServices fileOperator = new FileServices(service, model);
		FileUploadHandler fileUploadHandler = new FileUploadHandlerImpl(service);
		FileActionHandler fileActionHandler = new FileActionHandlerImpl(
				fileActionProvider, fileOperator, windowManager);
		FileDetailsPopupFactory fileDetailsPopupFactory = new FileDetailsPopupFactory(
				fileActionHandler, fileOperator, localizator);
		MainView mainView = new MainView(model, localizator,
				fileDetailsPopupFactory);
		MainViewPresenter presenter = new MainViewPresenter(service,
				windowManager, model, mainView, fileOperator);
		new MainViewGlue(windowManager, model, mainView, presenter,
				fileActionProvider, fileActionHandler, fileUploadHandler);
		return mainView;
	}
}
