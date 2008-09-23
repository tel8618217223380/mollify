package org.sjarvela.mollify.client.ui.mainview;

import org.sjarvela.mollify.client.file.FileActionProvider;
import org.sjarvela.mollify.client.file.FileActionProviderImpl;
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
		MainViewModel model = new MainViewModel();
		MainView mainView = new MainView(model, localizator);
		MainViewPresenter presenter = new MainViewPresenter(service,
				windowManager, model, mainView, fileActionProvider);
		new MainViewGlue(mainView, presenter);
		return mainView;
	}
}
