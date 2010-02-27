/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dnd;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomPickupDragController extends PickupDragController {
	private final DragController controller;

	public CustomPickupDragController(AbsolutePanel boundaryPanel,
			DragController controller) {
		super(boundaryPanel, false);
		this.controller = controller;
	}

	@Override
	public boolean getBehaviorDragProxy() {
		return controller.useProxy();
	}

	@Override
	protected Widget newDragProxy(DragContext context) {
		return controller.createProxy(context);
	}

}
