/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client;

import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.DefaultViewManager;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint {
	private static final String MOLLIFY_PANEL_ID = "mollify";
	public static final String PROTOCOL_VERSION = "1_0_0";

	static final String META_PROPERTY = "mollify:property";

	static final String PARAM_FILE_UPLOADER = "file-uploader";
	static final String VALUE_FILE_UPLOADER_FLASH = "flash";

	private Container container;
	private RootPanel panel;

	public void onModuleLoad() {
		Log.setUncaughtExceptionHandler();

		DeferredCommand.addCommand(new Command() {
			public void execute() {
				onLoad();
			}
		});
	}

	private void onLoad() {
		panel = RootPanel.get(MOLLIFY_PANEL_ID);
		if (panel == null)
			return;

		try {
			container = GWT.create(Container.class);
			((DefaultViewManager) container.getViewManager())
					.setRootPanel(panel);
		} catch (RuntimeException e) {
			showExceptionError("Error initializing application", e);
			return;
		}

		start();
	}

	private void start() {
		Log.info("Starting Mollify, protocol version " + PROTOCOL_VERSION);

		container.getEnvironment().getSessionService().getSessionInfo(
				new ResultListener<SessionInfo>() {
					public void onFail(ServiceError error) {
						showNativeError(error);
					}

					public void onSuccess(SessionInfo session) {
						open(session);
					}
				});
	};

	private void open(SessionInfo session) {
		if (session.isAuthenticationRequired() && !session.getAuthenticated()) {
			container.getUiSessionManager().login(new Callback() {
				public void onCallback() {
					showMain();
				}
			}, new Callback() {
				public void onCallback() {
					start();
				}
			});
		} else {
			container.getSessionManager().setSession(session);
			showMain();
		}
	}

	private void showMain() {
		container.getViewManager().openView(
				container.getMainViewFactory().createMainView(
						container.getUiSessionManager()).getViewWidget());

	}

	private void showExceptionError(String message, Throwable e) {
		GWT.log(message, e);
		Log.error(message, e);

		if (container != null)
			container.getDialogManager().showInfo("Mollify",
					"Unexpected error: " + e.getMessage());
		else
			e.printStackTrace();
	}

	private void showNativeError(ServiceError error) {
		panel.clear();
		panel.add(new HTML(container.getTextProvider().getStrings()
				.infoDialogErrorTitle()
				+ ": "
				+ error.getType().getMessage(container.getTextProvider())));
	}

}
