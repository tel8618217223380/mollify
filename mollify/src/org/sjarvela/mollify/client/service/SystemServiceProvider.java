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
import org.sjarvela.mollify.client.service.request.listener.ResultListenerFactory;
import org.sjarvela.mollify.client.session.SessionManager;
import org.sjarvela.mollify.client.ui.ViewManager;

public class SystemServiceProvider implements ServiceProvider,
		ResultListenerFactory {
	private final ServiceEnvironment env;
	private final ViewManager viewManager;
	private final SessionManager sessionManager;

	public SystemServiceProvider(ServiceEnvironment env,
			ViewManager viewManager, SessionManager sessionManager) {
		this.env = env;
		this.viewManager = viewManager;
		this.sessionManager = sessionManager;
	}

	@Override
	public ConfigurationService getConfigurationService() {
		return new ConfigurationServiceAdapter(env.getConfigurationService(),
				this);
	}

	@Override
	public FileSystemService getFileSystemService() {
		return new FileSystemServiceAdapter(env.getFileSystemService(), this);
	}

	@Override
	public FileUploadService getFileUploadService() {
		return new FileUploadServiceAdapter(env.getFileUploadService(), this);
	}

	@Override
	public SessionService getSessionService() {
		return new SessionServiceAdapter(env.getSessionService(), this);
	}

	@Override
	public ExternalService getExternalService() {
		return new ExternalServiceAdapter(env.getExternalService(), this);
	}

	public <T> ResultListener createListener(
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
		if (error.getType().equals(ServiceErrorType.UNAUTHORIZED)) {
			if (sessionManager != null)
				sessionManager.endSession();
			return true;
		}
		if (error.getType().equals(ServiceErrorType.INVALID_CONFIGURATION)) {
			viewManager.showErrorInMainView("Configuration Error", error);
			return true;
		}
		if (error.getType().equals(ServiceErrorType.RESOURCE_NOT_FOUND)
				|| error.getType().equals(ServiceErrorType.INVALID_RESPONSE)
				|| error.getType().equals(ServiceErrorType.DATA_TYPE_MISMATCH)) {
			viewManager.showErrorInMainView("Protocol error", error);
			return true;
		}
		return false;
	}

}
