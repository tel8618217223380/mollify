package org.sjarvela.mollify.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

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
				+ "-dropdown", defaultActionButton.getElement());
		dropdownButton.addStyleName(StyleConstants.MULTIACTION_BUTTON_DROPDOWN);

		panel.add(defaultActionButton);
		panel.add(dropdownButton);
		return panel;
	}

	public void addAction(ActionId action, String title) {
		dropdownButton.addAction(action, title);
	}

	public void setDefaultAction(final ActionId defaultAction) {
		defaultActionButton.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				actionListener.onActionTriggered(defaultAction);
			}
		});
	}
}
