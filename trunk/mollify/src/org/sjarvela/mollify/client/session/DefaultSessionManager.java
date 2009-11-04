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

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Singleton;

@Singleton
public class DefaultSessionManager implements SessionManager {
	private final List<SessionListener> listeners = new ArrayList();

	private SessionInfo session = null;

	public void setSession(SessionInfo session) {
		Log.debug("SESSION: " + session.asString());
		this.session = session;

		for (SessionListener listener : listeners)
			listener.onSessionChanged();
	}

	public SessionInfo getSession() {
		return session;
	}

	public void setSessionListener(SessionListener listener) {
		listeners.add(listener);
	}
}
