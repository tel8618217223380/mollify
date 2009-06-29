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
import java.util.List;
import java.util.Map;

public class UserCache {

	private final Map<String, User> usersById = new HashMap();

	public UserCache(List<User> users) {
		for (User user : users)
			usersById.put(user.getId(), user);
	}

	public User getUser(String id) {
		return usersById.get(id);
	}

}
