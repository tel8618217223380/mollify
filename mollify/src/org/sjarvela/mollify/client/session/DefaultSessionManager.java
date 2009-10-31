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

import com.google.inject.Singleton;

@Singleton
public class DefaultSessionManager implements SessionManager {
	private SessionInfo session = null;

	public void setSession(SessionInfo session) {
		this.session = session;
	}

	public SessionInfo getSession() {
		return session;
	}
}
