/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration.users;

import org.sjarvela.mollify.client.session.user.User;
import org.sjarvela.mollify.client.session.user.UserHandler;

public interface UserDialogFactory {

	void openAddUserDialog(UserHandler userHandler);

	void openEditUserDialog(UserHandler userHandler, User selected);

}
