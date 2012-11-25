/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.ConfirmationListener;
import org.sjarvela.mollify.client.ui.filesystem.SelectFolderHandler;

import com.google.gwt.core.client.JavaScriptObject;

public interface DialogManager {

	void setHandler(JavaScriptObject h);

	void showError(ServiceError error);

	void showInfo(String title, String text);

	void showInfo(String title, String text, String info);

	void showConfirmationDialog(String title, String message,
			ConfirmationListener listener);

	void showInputDialog(String title, String message, String defaultValue,
			String yesTitle, String noTitle, InputListener listener);

	WaitDialog openWaitDialog(String title, String message);

	void openFolderSelector(String title, String message, String actionTitle,
			SelectFolderHandler handler);

}