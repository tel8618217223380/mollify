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

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.RootPanel;

public class DefaultDragAndDropManager implements DragAndDropManager {
	private final Map<Class, PickupDragController> dragControllers = new HashMap();
	private final RootPanel boundaryPanel;

	public DefaultDragAndDropManager(RootPanel boundaryPanel) {
		this.boundaryPanel = boundaryPanel;
	}

	public PickupDragController getController(Class c) {
		return dragControllers.get(c);
	}

	@Override
	public void addDragAndDropController(Class content,
			final DragAndDropController controller) {
		PickupDragController dragController = new CustomPickupDragController(
				boundaryPanel, controller);
		dragController.setBehaviorDragStartSensitivity(3);
		dragControllers.put(content, dragController);
	}
}
