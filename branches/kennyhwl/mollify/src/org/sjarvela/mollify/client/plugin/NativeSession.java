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
		var o = {};

		o.info = function() {
			return s.@org.sjarvela.mollify.client.plugin.NativeSession::session;
		}
		o.isAdminOrStaff = function() {
			return s.@org.sjarvela.mollify.client.plugin.NativeSession::isAdminOrStaff()();
		}
		return o;
	}-*/;

	public boolean isAdminOrStaff() {
		UserPermissionMode permissionMode = session.getDefaultPermissionMode();
		return permissionMode.isAdmin() || permissionMode.isStaff();
	}
}
