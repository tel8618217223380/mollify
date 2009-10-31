package org.sjarvela.mollify.client.ui.common;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.popup.DropdownButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class MultiActionButton extends Composite {
	private Button defaultActionButton;
	private DropdownButton dropdownButton;
	private final ActionListener actionListener;

	public MultiActionButton(ActionListener actionListener, String title,
			String styleBase, String id) {
		this.actionListener = actionListener;
		initWidget(createContent(actionListener, title, id));

		if (id != null)
			getElement().setId(id);

		this.setStyleName(StyleConstants.MULTIACTION_BUTTON);
		if (styleBase != null)
			this.addStyleName(styleBase);
	}

	private FlowPanel createContent(ActionListener actionListener,
			String title, String id) {
		FlowPanel panel = new FlowPanel();
		defaultActionButton = new Button(title);
		defaultActionButton
				.addStyleName(StyleConstants.MULTIACTION_DEFAULT_BUTTON);
		defaultActionButton.getElement().setId(id + "-button");

		dropdownButton = new DropdownButton(actionListener, "", id
				+ "-dropdown", defaultActionButton);
		dropdownButton.addStyleName(StyleConstants.MULTIACTION_BUTTON_DROPDOWN);

		panel.add(defaultActionButton);
		panel.add(dropdownButton);
		return panel;
	}

	public void addAction(ResourceId action, String title) {
		dropdownButton.addAction(action, title);
	}

	public void setDefaultAction(final ResourceId defaultAction) {
		defaultActionButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				actionListener.onAction(defaultAction, null);
			}
		});
	}

	public void setActionEnabled(ResourceId action, boolean enabled) {
		dropdownButton.setActionEnabled(action, enabled);
	}
}
