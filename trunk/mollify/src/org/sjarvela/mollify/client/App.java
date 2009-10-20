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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

public class App implements EntryPoint {
	public void onModuleLoad() {
		Log.setUncaughtExceptionHandler();

		DeferredCommand.addCommand(new Command() {
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
			GWT.log("Error initializing application", e);
			Log.error("Error initializing application", e);

			if (container != null)
				container.getViewManager().showPlainError(
						"Unexpected error: " + e.getMessage());

			return;
		}

		container.getClient().start();
	}

}
