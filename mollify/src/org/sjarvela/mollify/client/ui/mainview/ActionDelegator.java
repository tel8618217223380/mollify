package org.sjarvela.mollify.client.ui.mainview;

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.ui.ActionId;
import org.sjarvela.mollify.client.ui.ActionListener;

public class ActionDelegator implements ActionListener {
	Map<ActionId, ActionListener> actions = new HashMap();

	public void onActionTriggered(ActionId action) {
		if (actions.containsKey(action))
			actions.get(action).onActionTriggered(action);
	}

	public void setActionListener(ActionId action, ActionListener actionListener) {
		this.actions.put(action, actionListener);
	}

}
