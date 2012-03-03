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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sjarvela.mollify.client.event.EventDispatcher;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultSessionManager implements SessionManager {
	private static Logger logger = Logger.getLogger(DefaultSessionManager.class
			.getName());

	private final EventDispatcher eventDispatcher;
	private final List<SessionListener> listeners = new ArrayList();

	private SessionInfo session = null;

	@Inject
	public DefaultSessionManager(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	public void setSession(SessionInfo session) {
		logger.log(Level.INFO, "SESSION: " + session.asString());
		this.session = session;

		for (SessionListener listener : listeners)
			listener.onSessionStarted(session);

		eventDispatcher.onEvent(SessionEvent.onSessionStart(session));
	}

	public void endSession() {
		logger.log(Level.INFO, "SESSION ENDED");
		this.session = null;

		for (SessionListener listener : listeners)
			listener.onSessionEnded();

		eventDispatcher.onEvent(SessionEvent.onSessionEnd());
	}

	public void addSessionListener(SessionListener listener) {
		listeners.add(listener);
	}

	public SessionInfo getSession() {
		return session;
	}

	public boolean hasSession() {
		return session != null;
	}

}
