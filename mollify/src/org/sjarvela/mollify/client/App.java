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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class App implements EntryPoint {
	private static Logger logger = Logger.getLogger(App.class.getName());

	public void onModuleLoad() {
		logger.log(Level.INFO, "Module load");

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute() {
				onLoad();
			}
		});
	}

	private void onLoad() {
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

}
