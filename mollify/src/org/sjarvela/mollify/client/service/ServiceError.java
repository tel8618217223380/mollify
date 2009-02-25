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

public class ServiceError {
	private ServiceErrorType type;
	private String details;

	public ServiceError(ServiceErrorType error) {
		this(error, "");
	}

	public ServiceError(ServiceErrorType error, String details) {
		this.type = error;
		this.details = details;
	}

	public ServiceErrorType getType() {
		return type;
	}

	public String getDetails() {
		return details;
	}
	
	public String toString() {
		return "Error '" + type.name() + "' (" + details + ")";
	}
}
