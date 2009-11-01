/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.configuration;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.ResultCallback;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.service.ConfigurationService;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.service.request.listener.ResultListener;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.session.user.PasswordGenerator;
import org.sjarvela.mollify.client.session.user.PasswordHandler;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.ViewListener;
import org.sjarvela.mollify.client.ui.common.dialog.CenteredDialog;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;

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

	private final ConfigurationViewManager viewManager;
	private final DialogManager dialogManager;
	private final SessionInfo session;
	private final PasswordHandler passwordHandler;
	private final ConfigurationMenu menu;

	private FlowPanel settingsView;
	private Label title;
	private Label status;

	public enum ConfigurationType implements ResourceId {
		Users, Folders, UserFolders
	}

	public ConfigurationDialog(TextProvider textProvider,
			DialogManager dialogManager, SessionInfo session,
			ConfigurationService service, PasswordHandler passwordHandler,
			PasswordGenerator passwordGenerator) {
		super(textProvider.getStrings().configurationDialogTitle(),
				StyleConstants.CONFIGURATION_DIALOG);
		this.textProvider = textProvider;
		this.dialogManager = dialogManager;
		this.session = session;
		this.passwordHandler = passwordHandler;
		this.viewManager = new ConfigurationViewManager(textProvider, service,
				this, passwordGenerator);

		menu = new ConfigurationMenu(this);
		menu.addItem(ConfigurationType.Users, textProvider.getStrings()
				.configurationDialogSettingUsers(),
				StyleConstants.CONFIGURATION_DIALOG_MENU_ITEM_USERS);
		menu.addItem(ConfigurationType.Folders, textProvider.getStrings()
				.configurationDialogSettingFolders(),
				StyleConstants.CONFIGURATION_DIALOG_MENU_ITEM_FOLDERS);
		menu.addItem(ConfigurationType.UserFolders, textProvider.getStrings()
				.configurationDialogSettingUserFolders(),
				StyleConstants.CONFIGURATION_DIALOG_MENU_ITEM_USER_FOLDERS);

		this.addViewListener(new ViewListener() {
			public void onShow() {
				menu.selectItem(ConfigurationType.Users);
			}
		});
		initialize();
	}

	@Override
	protected Widget createContent() {
		Panel panel = new HorizontalPanel();
		panel.addStyleName(StyleConstants.CONFIGURATION_DIALOG_CONTENT);
		panel.add(createMenu());
		panel.add(createConfigurationPanel());
		return panel;
	}

	private Widget createMenu() {
		Panel panel = new FlowPanel();
		panel.setStyleName(StyleConstants.CONFIGURATION_DIALOG_MENU_PANEL);
		panel.add(menu);
		return panel;
	}

	private Widget createConfigurationPanel() {
		Panel panel = new FlowPanel();
		panel.setStyleName(StyleConstants.CONFIGURATION_DIALOG_CONTENT_PANEL);

		panel.add(createTitle());

		settingsView = new FlowPanel();
		settingsView
				.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_PANEL);
		panel.add(settingsView);

		return panel;
	}

	private Widget createTitle() {
		Panel titlePanel = new FlowPanel();
		titlePanel
				.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_TITLE_PANEL);
		title = new Label();
		title.setStyleName(StyleConstants.CONFIGURATION_DIALOG_VIEW_TITLE);
		titlePanel.add(title);

		status = new Label();
		status
				.setStylePrimaryName(StyleConstants.CONFIGURATION_DIALOG_VIEW_STATUS);
		titlePanel.add(status);

		return titlePanel;
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

	public void onConfigurationItemSelected(ResourceId id) {
		ConfigurationView view = viewManager.getView(id).getView();
		settingsView.clear();
		settingsView.add(view);
		title.setText(view.getTitle());
	}

	public ResultListener createResultListener(final Callback callback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				setLoading(false);
				dialogManager.showError(error);
			}

			public void onSuccess(Object result) {
				setLoading(false);
				callback.onCallback();
			}
		};
	}

	public ResultListener createResultListener(
			final ResultCallback resultCallback) {
		return new ResultListener() {
			public void onFail(ServiceError error) {
				setLoading(false);
				dialogManager.showError(error);
			}

			public void onSuccess(Object result) {
				setLoading(false);
				resultCallback.onCallback(result);
			}
		};
	}

	public Callback createDataChangeNotifier(final ConfigurationType type) {
		return new Callback() {
			public void onCallback() {
				viewManager.onDataChanged(type);
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

	public void setLoading(boolean loading) {
		if (loading)
			status.addStyleDependentName(StyleConstants.LOADING);
		else
			status.removeStyleDependentName(StyleConstants.LOADING);
	}
}
