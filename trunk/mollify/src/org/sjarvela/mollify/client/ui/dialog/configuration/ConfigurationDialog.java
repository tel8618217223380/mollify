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
import org.sjarvela.mollify.client.service.request.Callback;
import org.sjarvela.mollify.client.service.request.ResultCallback;
import org.sjarvela.mollify.client.service.request.ResultListener;
import org.sjarvela.mollify.client.session.PasswordHandler;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.DialogManager;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationDialog extends CenteredDialog implements
		ConfigurationMenuSelectionListener {

	private final TextProvider textProvider;
	private final Map<ResourceId, ConfigurationSettingsView> cache = new HashMap();
	private final ConfigurationViewManager viewManager;
	private final DialogManager dialogManager;
	private final SessionInfo session;
	private final PasswordHandler passwordHandler;

	private ConfigurationMenu menu;
	private FlowPanel settingsView;
	private Label title;

	public enum Settings implements ResourceId {
		Users, Folders
	}

	public ConfigurationDialog(TextProvider textProvider,
			DialogManager dialogManager, SessionInfo session,
			SettingsService service, PasswordHandler passwordHandler) {
		super(textProvider.getStrings().configurationDialogTitle(),
				StyleConstants.CONFIGURATION_DIALOG);
		this.textProvider = textProvider;
		this.dialogManager = dialogManager;
		this.session = session;
		this.passwordHandler = passwordHandler;
		this.viewManager = new ConfigurationViewManager(textProvider, service,
				this);

		menu = new ConfigurationMenu(this);
		menu.addItem(Settings.Users, textProvider.getStrings()
				.configurationDialogSettingUsers(),
				StyleConstants.CONFIGURATION_DIALOG_MENU_ITEM_USERS);
		menu.addItem(Settings.Folders, textProvider.getStrings()
				.configurationDialogSettingFolders(),
				StyleConstants.CONFIGURATION_DIALOG_MENU_ITEM_FOLDERS);

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
		Panel panel = new FlowPanel();
		panel.setStyleName(StyleConstants.CONFIGURATION_DIALOG_MENU_PANEL);
		panel.add(menu);
		return panel;
	}

	private Widget createSettingsPanel() {
		Panel panel = new FlowPanel();
		panel.setStyleName(StyleConstants.CONFIGURATION_DIALOG_CONTENT_PANEL);

		title = new Label();
		title.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_TITLE);
		panel.add(title);

		settingsView = new FlowPanel();
		settingsView
				.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_PANEL);
		panel.add(settingsView);

		return panel;
	}

	@Override
	protected Widget createButtons() {
		Panel buttons = new FlowPanel();
		buttons.addStyleName(StyleConstants.CONFIGURATION_DIALOG_BUTTONS);

		buttons.add(createButton(textProvider.getStrings()
				.configurationDialogCloseButton(), new ClickHandler() {
			public void onClick(ClickEvent event) {
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
		settingsView.clear();
		settingsView.add(view);
		title.setText(view.getTitle());
	}

	private ConfigurationSettingsView getSettingsView(ResourceId id) {
		if (!cache.containsKey(id))
			cache.put(id, viewManager.createView(id));
		return cache.get(id);
	}

	public ResultListener createResultListener(final Callback callback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				dialogManager.showError(error);
			}

			public void onSuccess(Object result) {
				callback.onCallback();
			}
		};
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

	public DialogManager getDialogManager() {
		return dialogManager;
	}

	public SessionInfo getSessionInfo() {
		return session;
	}

	public PasswordHandler getPasswordHandler() {
		return passwordHandler;
	}
}
