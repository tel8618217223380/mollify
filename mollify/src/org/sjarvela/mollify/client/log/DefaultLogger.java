/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.log;

import com.google.gwt.core.client.GWT;

public class DefaultLogger implements MollifyLogger {

	public void logError(String error) {
		GWT.log(error, null);
	}

	public void logInfo(String info) {
		GWT.log(info, null);
	}

}
