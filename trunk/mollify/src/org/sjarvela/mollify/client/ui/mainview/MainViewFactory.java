package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.file.FileActionHandler;
import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileUploadHandler;
import org.sjarvela.mollify.client.file.impl.FileActionHandlerImpl;
import org.sjarvela.mollify.client.file.impl.FileActionProviderImpl;
import org.sjarvela.mollify.client.file.impl.FileUploadHandlerImpl;
import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.FileServices;
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

		FileServices fileServices = new FileServices(service);
		FileViewModel model = new FileViewModel(fileServices);
		FileUploadHandler fileUploadHandler = new FileUploadHandlerImpl(service);
		FileActionHandler fileActionHandler = new FileActionHandlerImpl(
				fileActionProvider, fileServices, windowManager);
		DirectorySelectorFactory directorySelectorFactory = new DirectorySelectorFactory();
		FileDetailsPopupFactory fileDetailsPopupFactory = new FileDetailsPopupFactory(
				fileActionHandler, fileServices, localizator);

		MainView view = new MainView(model, localizator,
				directorySelectorFactory, fileDetailsPopupFactory);
		MainViewPresenter presenter = new MainViewPresenter(windowManager,
				model, view, fileActionProvider, fileActionHandler,
				fileUploadHandler);
		new MainViewGlue(view, presenter);
		return view;
	}
}
