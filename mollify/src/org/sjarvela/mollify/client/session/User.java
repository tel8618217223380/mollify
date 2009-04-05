/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.session;


public class User {
	private String name;
	private PermissionMode type;

	public User(String name, PermissionMode type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public PermissionMode getType() {
		return type;
	}

}
