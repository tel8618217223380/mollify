/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.MultiActionButton;
import org.sjarvela.mollify.client.ui.common.popup.DropdownButton;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FileItemContextComponent extends ContextPopupComponent {
	private final TextProvider textProvider;
	private final ActionListener actionListener;

	private final boolean zipDownloadEnabled;
	private final boolean fileView;
	private final boolean publicLinks;

	private final Mode mode;

	private Label name;
	// private ActionLink editPermissions;

	private DropdownButton actionsButton;
	private Button viewButton;

	private VerticalPanel componentsPanel;
	private Map<ItemContextComponent, Widget> components = new HashMap();

	public enum Mode {
		File, Folder
	}

	public enum Action implements ResourceId {
		addDescription, editDescription, removeDescription, cancelEditDescription, applyDescription, editPermissions, addToDropbox
	}

	public enum DescriptionActionGroup implements ResourceId {
		view, edit
	}

	public FileItemContextComponent(Mode mode, TextProvider textProvider,
			boolean generalWritePermissions, boolean zipDownloadEnabled,
			boolean fileView, boolean publicLinks, ActionListener actionListener) {
		super(Mode.File.equals(mode) ? StyleConstants.FILE_CONTEXT
				: StyleConstants.DIR_CONTEXT, null);
		this.mode = mode;

		this.zipDownloadEnabled = zipDownloadEnabled;
		this.fileView = fileView;
		this.publicLinks = publicLinks;

		this.textProvider = textProvider;
		this.actionListener = actionListener;

		initialize();
	}

	protected Widget createContent() {
		Panel content = new VerticalPanel();
		content.setStyleName(StyleConstants.FILE_CONTEXT_CONTENT);

		Label widthEnforcer = new Label();
		widthEnforcer.setStyleName(StyleConstants.FILE_CONTEXT_WIDTH_ENFORCER);
		content.add(widthEnforcer);

		name = new Label();
		name.setStyleName(StyleConstants.FILE_CONTEXT_FILENAME);
		content.add(name);

		content.add(createComponentsPanel());
		content.add(createButtons());
		return content;
	}

	public List<ItemContextComponent> createComponents(ItemContext itemContext) {
		List<ItemContextComponent> contextComponents = new ArrayList(
				itemContext.getComponents());
		this.components.clear();
		this.componentsPanel.clear();

		for (ItemContextComponent c : contextComponents)
			addComponent(c);

		return contextComponents;
	}

	private void addComponent(ItemContextComponent c) {
		if (c instanceof ItemContextSection) {
			Widget section = createSection((ItemContextSection) c);
			components.put(c, section);
			componentsPanel.add(section);
		} else {
			componentsPanel.add(c.getComponent());
		}
	}

	private Widget createSection(final ItemContextSection section) {
		DisclosurePanel s = new DisclosurePanel(section.getTitle());
		s.setOpen(false);
		s.addStyleName(StyleConstants.ITEM_CONTEXT_SECTION);
		s.getHeader().getElement().getParentElement().setClassName(
				StyleConstants.ITEM_CONTEXT_SECTION_HEADER);

		s.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				section.onOpen();
			}
		});
		s.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				section.onClose();
			}
		});

		s.add(section.getComponent());
		return s;
	}

	// private Widget createPermissionActions() {
	// editPermissions = new ActionLink(textProvider.getStrings()
	// .fileDetailsEditPermissions(),
	// StyleConstants.FILE_CONTEXT_EDIT_PERMISSIONS,
	// StyleConstants.FILE_CONTEXT_PERMISSION_ACTION);
	// editPermissions.setAction(actionListener, Action.editPermissions);
	//
	// Panel permissionActionsEdit = new FlowPanel();
	// permissionActionsEdit
	// .setStyleName(StyleConstants.FILE_CONTEXT_PERMISSION_ACTIONS);
	// permissionActionsEdit.add(editPermissions);
	//
	// return permissionActionsEdit;
	// }

	private Widget createButtons() {
		Panel buttons = new FlowPanel();
		buttons.setStyleName(StyleConstants.FILE_CONTEXT_BUTTONS);

		actionsButton = new DropdownButton(actionListener, textProvider
				.getStrings().fileDetailsActionsTitle(),
				StyleConstants.FILE_CONTEXT_ACTIONS);
		actionsButton.addAction(Action.addToDropbox, textProvider.getStrings()
				.mainViewSelectActionAddToDropbox());
		if (Mode.File.equals(this.mode) && this.publicLinks)
			actionsButton.addAction(FileSystemAction.publicLink, textProvider
					.getStrings().fileActionPublicLinkTitle());
		actionsButton.addSeparator();
		actionsButton.addAction(FileSystemAction.rename, textProvider
				.getStrings().fileActionRenameTitle());
		actionsButton.addAction(FileSystemAction.copy, textProvider
				.getStrings().fileActionCopyTitle());
		if (Mode.File.equals(this.mode))
			actionsButton.addAction(FileSystemAction.copyHere, textProvider
					.getStrings().fileActionCopyHereTitle());
		actionsButton.addAction(FileSystemAction.move, textProvider
				.getStrings().fileActionMoveTitle());
		actionsButton.addAction(FileSystemAction.delete, textProvider
				.getStrings().fileActionDeleteTitle());

		Widget downloadButton = getDownloadButton();
		if (downloadButton != null)
			buttons.add(downloadButton);

		viewButton = createViewButton();
		if (viewButton != null)
			buttons.add(viewButton);

		buttons.add(actionsButton);

		return buttons;
	}

	private Button createViewButton() {
		if (!fileView)
			return null;
		Button button = createActionButton(textProvider.getStrings()
				.fileActionViewTitle(), actionListener, FileSystemAction.view);
		return button;
	}

	private Widget getDownloadButton() {
		Map<FileSystemAction, String> downloadActions = new TreeMap();
		if (Mode.File.equals(this.mode))
			downloadActions.put(FileSystemAction.download, textProvider
					.getStrings().fileActionDownloadTitle());
		if (this.zipDownloadEnabled)
			downloadActions.put(FileSystemAction.download_as_zip, textProvider
					.getStrings().fileActionDownloadZippedTitle());

		if (downloadActions.size() > 1) {
			Iterator<Entry<FileSystemAction, String>> actions = downloadActions
					.entrySet().iterator();

			MultiActionButton downloadButton = createMultiActionButton(
					actionListener, textProvider.getStrings()
							.fileActionDownloadTitle(),
					FileSystemAction.download.name());

			boolean first = true;
			while (actions.hasNext()) {
				Entry<FileSystemAction, String> action = actions.next();
				downloadButton.addAction(action.getKey(), action.getValue());

				if (first)
					downloadButton.setDefaultAction(action.getKey());
				first = false;
			}

			return downloadButton;
		}

		if (downloadActions.size() == 1) {
			Entry<FileSystemAction, String> action = downloadActions.entrySet()
					.iterator().next();
			return createActionButton(action.getValue(), actionListener, action
					.getKey());
		}

		return null;
	}

	private Widget createComponentsPanel() {
		componentsPanel = new VerticalPanel();
		componentsPanel
				.setStylePrimaryName(StyleConstants.ITEM_CONTEXT_COMPONENTS_PANEL);
		return componentsPanel;
	}

	public void reset() {
		componentsPanel.clear();

		if (viewButton != null)
			viewButton.setVisible(false);

		actionsButton.setActionVisible(FileSystemAction.rename, false);
		actionsButton.setActionVisible(FileSystemAction.copy, true);
		if (this.mode.equals(Mode.File))
			actionsButton.setActionVisible(FileSystemAction.copyHere, true);
		actionsButton.setActionVisible(FileSystemAction.move, false);
		actionsButton.setActionVisible(FileSystemAction.delete, false);
	}

	public void update(boolean isWritable, boolean isView) {
		actionsButton.setActionVisible(FileSystemAction.rename, isWritable);
		actionsButton.setActionVisible(FileSystemAction.move, isWritable);
		actionsButton.setActionVisible(FileSystemAction.delete, isWritable);

		if (viewButton != null)
			viewButton.setVisible(isView);
	}

	public Label getName() {
		return name;
	}

	public void removeComponent(ItemContextComponent c) {
		Widget w = components.get(c);
		if (w == null)
			w = c.getComponent();
		componentsPanel.remove(w);
	}

	public void removeComponents(List<ItemContextComponent> list) {
		for (ItemContextComponent c : list)
			removeComponent(c);
	}
}
