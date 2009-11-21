/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.service.environment.ServiceEnvironment;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.ui.ViewManager;
import org.sjarvela.mollify.client.util.JsUtil;

public class SystemServiceAdapter implements ServiceProvider,
		AdapterListenerCreator {
	private final ServiceEnvironment env;
	private final ViewManager viewManager;

	public SystemServiceAdapter(ServiceEnvironment env, ViewManager viewManager) {
		this.env = env;
		this.viewManager = viewManager;
	}

	public ConfigurationService getConfigurationService() {
		return null;
	}

	public FileSystemService getFileSystemService() {
		// TODO Auto-generated method stub
		return null;
	}

	public FileUploadService getFileUploadService() {
		// TODO Auto-generated method stub
		return null;
	}

	public SessionService getSessionService() {
		return new SessionServiceAdapter(env.getSessionService(), this);
	}

	public <T> ResultListener createAdapterListener(
			final ResultListener<T> resultListener) {
		return new ResultListener<T>() {
			public void onFail(ServiceError error) {
				if (handleError(error))
					return;
				resultListener.onFail(error);
			}

			public void onSuccess(T result) {
				resultListener.onSuccess(result);
			}
		};
	}

	protected boolean handleError(ServiceError error) {
		if (error.getType().equals(ServiceErrorType.AUTHENTICATION_FAILED)) {
			// TODO logout
		}
		if (error.getType().equals(ServiceErrorType.INVALID_CONFIGURATION)) {
			viewManager.showCriticalError("Configuration Error", error
					.getError().getDetails(), JsUtil.asList(error.getError()
					.getDebugInfo()));
			return true;
		}
		return false;
	}

}
