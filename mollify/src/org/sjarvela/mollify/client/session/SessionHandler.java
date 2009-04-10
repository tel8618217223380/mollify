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

public class SessionHandler implements SessionProvider {

	private SessionInfo session;

	public void setSession(SessionInfo session) {
		this.session = session;
	}

	public SessionInfo getSession() {
		return session;
	}

}
