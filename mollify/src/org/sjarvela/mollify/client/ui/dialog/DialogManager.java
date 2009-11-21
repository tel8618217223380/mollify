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

import org.sjarvela.mollify.client.ConfirmationListener;
import org.sjarvela.mollify.client.service.ServiceError;

public interface DialogManager {

	public abstract void showError(ServiceError error);

	public abstract void showInfo(String title, String text);

	public abstract void showConfirmationDialog(String title, String message,
			String style, ConfirmationListener listener);

}