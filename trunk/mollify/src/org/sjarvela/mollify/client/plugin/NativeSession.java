/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin;

import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.user.UserPermissionMode;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeSession {
	private SessionInfo session;

	public NativeSession(SessionInfo session) {
		this.session = session;
	}

	public JavaScriptObject asJs() {
		return createJs(this);
	}

	private native JavaScriptObject createJs(NativeSession s) /*-{
		return $wnd.$.extend(
			{
				admin:s.@org.sjarvela.mollify.client.plugin.NativeSession::isAdmin()()
			},
			s.@org.sjarvela.mollify.client.plugin.NativeSession::session
		);
	}-*/;

	public boolean isAdmin() {
		UserPermissionMode permissionMode = session.getDefaultPermissionMode();
		return permissionMode.isAdmin();
	}
}
