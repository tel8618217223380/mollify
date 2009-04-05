/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog.configuration;

import java.util.HashMap;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.SettingsService;
import org.sjarvela.mollify.client.service.request.ResultCallback;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationDialog extends CenteredDialog implements
		ConfigurationMenuSelectionListener {

	private final TextProvider textProvider;
	private final Map<ResourceId, ConfigurationSettingsView> cache = new HashMap();
	private final ConfigurationViewManager viewManager;

	private ConfigurationMenu menu;
	private FlowPanel settings;
	private Label title;
	private final DialogManager dialogManager;

	public enum Settings implements ResourceId {
		Users
	}

	public ConfigurationDialog(TextProvider textProvider,
			DialogManager dialogManager, SettingsService service) {
		super(textProvider.getStrings().configurationDialogTitle(),
				StyleConstants.CONFIGURATION_DIALOG);
		this.textProvider = textProvider;
		this.dialogManager = dialogManager;
		this.viewManager = new ConfigurationViewManager(textProvider, service,
				this);

		initialize();
	}

	@Override
	protected Widget createContent() {
		Panel panel = new HorizontalPanel();
		panel.addStyleName(StyleConstants.CONFIGURATION_DIALOG_CONTENT);
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
		Panel panel = new VerticalPanel();
		panel.setStyleName(StyleConstants.CONFIGURATION_DIALOG_CONTENT_PANEL);

		title = new Label();
		title.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_TITLE);
		panel.add(title);

		settings = new FlowPanel();
		settings.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_PANEL);
		panel.add(settings);
		return panel;
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
		menu.selectItem(Settings.Users);
	}

	public void onConfigurationItemSelected(ResourceId id) {
		ConfigurationSettingsView view = getSettingsView(id);
		settings.clear();
		settings.add(view);
		title.setText(view.getTitle());
	}

	private ConfigurationSettingsView getSettingsView(ResourceId id) {
		if (!cache.containsKey(id))
			cache.put(id, viewManager.createView(id));
		return cache.get(id);
	}

	public ResultListener createResultListener(
			final ResultCallback resultCallback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

			public void onSuccess(Object result) {
				resultCallback.onCallback(result);
			}
		};
	}
}
