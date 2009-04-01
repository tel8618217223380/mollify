package org.sjarvela.mollify.client.ui.dialog.configuration;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationMenu extends VerticalPanel {
	private final MenuSelectionListener listener;

	public ConfigurationMenu(MenuSelectionListener listener) {
		super();
		this.listener = listener;
		setStyleName(StyleConstants.CONFIGURATION_DIALOG_MENU);
	}

	public void addItem(ResourceId id, String title, String style) {
		this.add(createItem(id, title, style));
	}

	private Widget createItem(final ResourceId id, String title, String style) {
		Label item = new Label(title);
		item.setStylePrimaryName(StyleConstants.CONFIGURATION_DIALOG_MENU_ITEM);
		item.addStyleDependentName(style);
		HoverDecorator.decorate(item);

		item.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				listener.onItemSelected(id);
			}
		});
		return item;
	}

}
