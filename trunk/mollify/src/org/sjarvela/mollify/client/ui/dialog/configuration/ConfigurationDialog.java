package org.sjarvela.mollify.client.ui.dialog.configuration;

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationDialog extends CenteredDialog implements
		MenuSelectionListener {
	public enum Settings implements ResourceId {
		Users
	}

	private final TextProvider textProvider;
	private final Map<ResourceId, ConfigurationSettingsView> cache = new HashMap();
	private ConfigurationMenu menu;
	private FlowPanel settings;

	public ConfigurationDialog(TextProvider textProvider) {
		super(textProvider.getStrings().configurationDialogTitle(),
				StyleConstants.CONFIGURATION_DIALOG);
		this.textProvider = textProvider;
	}

	@Override
	protected Widget createContent() {
		Panel panel = new HorizontalPanel();
		panel.add(createMenu());
		panel.add(createSettingsPanel());
		return panel;
	}

	private Widget createMenu() {
		menu = new ConfigurationMenu(this);
		menu.addItem(Settings.Users, textProvider.getStrings()
				.configurationDialogSettingUsers(),
				StyleConstants.CONFIGURATION_DIALOG_MENU_ITEM_USERS);
		return menu;
	}

	private Widget createSettingsPanel() {
		settings = new FlowPanel();
		return settings;
	}

	@Override
	protected Widget createButtons() {
		Panel buttons = new FlowPanel();
		buttons.addStyleName(StyleConstants.CONFIGURATION_DIALOG_BUTTONS);
		buttons.add(createButton(textProvider.getStrings()
				.configurationDialogCloseButton(), new ClickListener() {

			public void onClick(Widget sender) {
				ConfigurationDialog.this.hide();
			}
		}, StyleConstants.CONFIGURATION_DIALOG_BUTTON_CLOSE));
		return buttons;
	}

	@Override
	protected void onShow() {
		super.onShow();
		onItemSelected(Settings.Users);
	}

	public void onItemSelected(ResourceId id) {
		settings.clear();
		settings.add(getSettingsView(id));
	}

	private Widget getSettingsView(ResourceId id) {
		if (!cache.containsKey(id))
			cache.put(id, createSettingsView(id));
		return cache.get(id);
	}

	private ConfigurationSettingsView createSettingsView(ResourceId id) {
		if (id.equals(Settings.Users))
			return new ConfigurationSettingsUsers();
		return null;
	}
}
