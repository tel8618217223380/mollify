/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext;

import org.sjarvela.mollify.client.ResourceId;

public class ContextAction implements ContextActionItem {

	private final ResourceId action;
	private final String title;

	public ContextAction(ResourceId action, String title) {
		this.action = action;
		this.title = title;
	}

	public ResourceId getAction() {
		return action;
	}

	public String getTitle() {
		return title;
	}

}
