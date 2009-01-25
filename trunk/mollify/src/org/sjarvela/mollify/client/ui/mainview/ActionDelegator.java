/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.mainview;

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.ui.ActionHandler;
import org.sjarvela.mollify.client.ui.ActionId;
import org.sjarvela.mollify.client.ui.ActionListener;

public class ActionDelegator implements ActionListener {
	Map<ActionId, ActionHandler> actions = new HashMap();

	public void onActionTriggered(ActionId action) {
		if (actions.containsKey(action))
			actions.get(action).onAction();
	}

	public void setActionHandler(ActionId action, ActionHandler actionHandler) {
		this.actions.put(action, actionHandler);
	}

}
