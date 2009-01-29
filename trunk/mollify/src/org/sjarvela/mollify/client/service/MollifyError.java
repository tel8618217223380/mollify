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

public class MollifyError {
	private ServiceError error;
	private String details;

	public MollifyError(ServiceError error) {
		this(error, "");
	}

	public MollifyError(ServiceError error, String details) {
		this.error = error;
		this.details = details;
	}

	public ServiceError getError() {
		return error;
	}

	public String getDetails() {
		return details;
	}
}
