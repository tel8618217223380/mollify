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

import org.sjarvela.mollify.client.service.request.data.ErrorValue;

public class ServiceError {
	private final ServiceErrorType type;
	private final ErrorValue error;
	private final String details;

	public ServiceError(ServiceErrorType type) {
		this(type, "");
	}

	public ServiceError(ServiceErrorType type, String details) {
		this.type = type;
		this.details = details;
		this.error = null;
	}

	public ServiceError(ServiceErrorType type, ErrorValue error) {
		this.type = type;
		this.details = error.getDetails();
		this.error = error;
	}

	public ServiceErrorType getType() {
		return type;
	}

	public String getDetails() {
		return details;
	}

	public ErrorValue getError() {
		return error;
	}

	public String toString() {
		return "Error '" + type.name() + "' (" + details + ") "
				+ (error != null ? error.asString() : "");
	}
}
