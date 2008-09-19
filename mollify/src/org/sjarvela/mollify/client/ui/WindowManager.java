package org.sjarvela.mollify.client.ui;

import org.sjarvela.mollify.client.localization.Localizator;
import org.sjarvela.mollify.client.service.MollifyService;
import org.sjarvela.mollify.client.ui.filemanager.MainViewGlue;
import org.sjarvela.mollify.client.ui.filemanager.MainViewModel;
import org.sjarvela.mollify.client.ui.filemanager.MainView;
import org.sjarvela.mollify.client.ui.filemanager.MainViewPresenter;

import com.google.gwt.user.client.ui.Widget;

public class WindowManager {
	private final MollifyService service;

	public WindowManager(MollifyService mollifyService) {
		this.service = mollifyService;
	}

	public Widget createMainView() {
		MainViewModel model = new MainViewModel();
		MainView view = new MainView(model, Localizator.getInstance());
		MainViewPresenter presenter = new MainViewPresenter(service, model,
				view);
		new MainViewGlue(service, view, presenter);

		return view;
	}

}
