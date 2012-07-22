/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session.user;

import java.util.List;

public class UsersAndGroups {
	private List<User> users;
	private List<UserGroup> userGroups;

	public UsersAndGroups(List<User> users, List<UserGroup> userGroups) {
		this.users = users;
		this.userGroups = userGroups;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<UserGroup> getUserGroups() {
		return userGroups;
	}

}
