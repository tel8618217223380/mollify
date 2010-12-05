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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class App implements EntryPoint {
	private static Logger logger = Logger.getLogger(App.class.getName());
	public static final String MOLLIFY_PANEL_ID = "mollify";

	public void onModuleLoad() {
		logger.log(Level.INFO, "Module load");

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				onLoad();
			}
		});
	}

	private void onLoad() {
		showInitMessage();

		GWT.runAsync(new RunAsyncCallback() {
			@Override
			public void onSuccess() {
				doInit();
			}

			@Override
			public void onFailure(Throwable reason) {
				logger.log(Level.SEVERE, "Error initializing application",
						reason);
				showError(reason);
			}
		});
	}

	protected void doInit() {
		Container container = null;

		try {
			container = (Container) GWT.create(Container.class);
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error initializing application", e);

			if (container != null)
				container.getViewManager().showPlainError(
						"Unexpected error: " + e.getMessage());

			return;
		}

		container.getClient().start();
	}

	private void showInitMessage() {
		RootPanel rootPanel = RootPanel.get(MOLLIFY_PANEL_ID);
		if (rootPanel == null)
			throw new RuntimeException("No placeholder found for Mollify");
		rootPanel.clear();
		rootPanel.add(new HTML("<div class='mollify_loading'/>"));
	}

	private void showError(Throwable reason) {
		RootPanel rootPanel = RootPanel.get(MOLLIFY_PANEL_ID);
		rootPanel.clear();
		rootPanel.add(new HTML("Failed to initialize Mollify: "
				+ reason.getMessage()));
	}
}
