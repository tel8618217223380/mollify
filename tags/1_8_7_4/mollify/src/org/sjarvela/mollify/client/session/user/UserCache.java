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

import java.util.HashMap;
import java.util.Map;

public class UserCache {
	private final Map<String, UserBase> byId = new HashMap();

	public UserCache(UsersAndGroups usersAndGroups) {
		for (User user : usersAndGroups.getUsers()) {
			byId.put(user.getId(), user);
		}
		for (UserGroup userGroup : usersAndGroups.getUserGroups()) {
			byId.put(userGroup.getId(), userGroup);
		}
	}

	public UserBase getById(String id) {
		return byId.get(id);
	}

}
