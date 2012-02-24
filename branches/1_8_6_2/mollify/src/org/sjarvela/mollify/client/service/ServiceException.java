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

public class ServiceException extends Exception {
	private static final long serialVersionUID = 1L;

	private final ServiceError error;

	public ServiceException(ServiceErrorType type) {
		this(new ServiceError(type));
	}

	public ServiceException(ServiceError error) {
		super();
		this.error = error;
	}

	public ServiceError getError() {
		return error;
	}

	public ServiceErrorType getType() {
		return error.getType();
	}
}
