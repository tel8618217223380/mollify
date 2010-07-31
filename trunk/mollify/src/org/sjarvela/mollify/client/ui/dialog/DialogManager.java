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

import org.sjarvela.mollify.client.service.ConfirmationListener;
import org.sjarvela.mollify.client.service.ServiceError;

import com.google.gwt.user.client.ui.Widget;

public interface DialogManager {

	void showError(ServiceError error);

	void showInfo(String title, String text);

	void showInfo(String title, String text, String info);

	void showConfirmationDialog(String title, String message, String style,
			ConfirmationListener listener, Widget source);

	void showInputDialog(String title, String message, String defaultValue);

}