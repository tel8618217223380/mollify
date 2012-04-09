/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.popup.impl;

/*import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.Callback;
import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.MultiActionButton;
import org.sjarvela.mollify.client.ui.common.popup.DropdownButton;
import org.sjarvela.mollify.client.ui.common.popup.DropdownPopupMenu;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextAction;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextActionItem;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextActionSeparator;
import org.sjarvela.mollify.client.ui.fileitemcontext.ContextCallbackAction;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContext;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContext.ActionType;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextComponent;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextSection;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ItemContextPopupComponent extends ContextPopupComponent {
	private final TextProvider textProvider;
	private final ActionListener actionListener;

	private Label name;

	private DropdownButton actionsButton;

	private VerticalPanel componentsPanel;
	private Map<ItemContextComponent, Widget> components = new HashMap();
	private FlowPanel buttons;
	private FlowPanel progress;

	public enum Action implements ResourceId {
		addDescription, editDescription, removeDescription, cancelEditDescription, applyDescription, editPermissions, addToDropbox, callback
	}

	public enum DescriptionActionGroup implements ResourceId {
		view, edit
	}

	public ItemContextPopupComponent(TextProvider textProvider,
			boolean generalWritePermissions, ActionListener actionListener) {
		super(StyleConstants.FILE_CONTEXT, null);

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

		progress = new FlowPanel();
		progress.setStyleName(StyleConstants.FILE_CONTEXT_PROGRESS);
		progress.setVisible(false);

		name = new Label();
		name.setStyleName(StyleConstants.FILE_CONTEXT_FILENAME);
		content.add(name);
		content.add(progress);

		content.add(createComponentsPanel());
		content.add(createButtons());
		return content;
	}

	@Override
	public void showPopup() {
		super.showPopup();
		int pointerPos = (parent.getAbsoluteLeft() + (parent.getOffsetWidth() / 2))
				- container.getAbsoluteLeft();
		if (pointerPos > (container.getAbsoluteLeft() + container
				.getOffsetWidth()))
			pointerPos = 30; // TODO is there a better way to set default pos

		pointer.getElement().getStyle()
				.setLeft((double) pointerPos, Style.Unit.PX);
	}

	public List<ItemContextComponent> setup(ItemContext itemContext,
			FileSystemItem item, ItemDetails details) {
		List<ItemContextComponent> contextComponents = new ArrayList(
				itemContext.getComponents());
		this.components.clear();
		this.componentsPanel.clear();

		for (ItemContextComponent c : contextComponents)
			addComponent(c, item, details);

		setupActions(itemContext.getActions());

		return contextComponents;
	}

	private void setupActions(Map<ActionType, List<ContextActionItem>> actions) {
		setupDownloadActions(actions.get(ActionType.Download));
		setupPrimaryActions(actions.get(ActionType.Primary));
		setupSecondaryActions(actions.get(ActionType.Secondary));
	}

	private void setupDownloadActions(List<ContextActionItem> items) {
		if (items.isEmpty())
			return;

		Widget downloadButton;
		if (items.size() > 1) {
			downloadButton = createDropdownButton(items);
		} else {
			downloadButton = createButton(items.get(0));
		}

		if (downloadButton == null)
			return;
		buttons.add(downloadButton);
	}

	private void setupPrimaryActions(List<ContextActionItem> items) {
		for (ContextActionItem item : items) {
			Widget button = createButton(item);
			if (button != null)
				buttons.add(button);
		}
	}

	private Widget createDropdownButton(List<ContextActionItem> items) {
		MultiActionButton downloadButton = createMultiActionButton(
				actionListener,
				textProvider.getText(Texts.fileActionDownloadTitle),
				FileSystemAction.download.name());

		boolean first = true;
		for (ContextActionItem item : items) {
			if (item instanceof ContextAction) {
				ContextAction action = (ContextAction) item;
				downloadButton.addAction(action.getAction(), action.getTitle());
				if (first)
					downloadButton.setDefaultAction(action.getAction());
			} else if (item instanceof ContextActionSeparator) {
				if (!first)
					downloadButton.addSeparator();
			}
			first = false;
		}

		return downloadButton;
	}

	private Widget createButton(ContextActionItem item) {
		if (item instanceof ContextAction) {
			ContextAction action = (ContextAction) item;
			return createActionButton(action.getTitle(), actionListener,
					action.getAction());
		} else if (item instanceof ContextCallbackAction) {
			final ContextCallbackAction action = (ContextCallbackAction) item;
			return createCallbackButton(action.getTitle(), null,
					new Callback() {
						@Override
						public void onCallback() {
							actionListener.onAction(Action.callback, action);
						}
					});
		}

		return null;
	}

	private void setupSecondaryActions(List<ContextActionItem> items) {
		int index = 0;
		for (ContextActionItem item : items) {
			boolean first = (index == 0);
			boolean last = (index == (items.size() - 1));

			if (item instanceof ContextAction) {
				actionsButton.addAction(((ContextAction) item).getAction(),
						((ContextAction) item).getTitle());
			} else if (item instanceof ContextActionSeparator) {
				if (!first && !last)
					actionsButton.addSeparator();
			} else if (item instanceof ContextCallbackAction) {
				final ContextCallbackAction action = (ContextCallbackAction) item;
				actionsButton.addCallbackAction(action.getTitle(), null,
						new Callback() {
							@Override
							public void onCallback() {
								actionListener
										.onAction(Action.callback, action);
							}
						});
			}
			index++;
		}
		if (!items.isEmpty())
			buttons.add(actionsButton);
	}

	public DropdownPopupMenu<FileSystemItem> createMenu(FileSystemItem fsi,
			Element parent, ItemContext itemContext) {
		DropdownPopupMenu menu = new DropdownPopupMenu<FileSystemItem>(
				actionListener, parent, null);
		int index = 0;
		List<ContextActionItem> items = itemContext.getActions().get(
				ActionType.Secondary);
		for (ContextActionItem item : items) {
			boolean first = (index == 0);
			boolean last = (index == (items.size() - 1));

			if (item instanceof ContextAction) {
				menu.addAction(((ContextAction) item).getAction(),
						((ContextAction) item).getTitle());
			} else if (item instanceof ContextActionSeparator) {
				if (!first && !last)
					menu.addSeparator();
			} else if (item instanceof ContextCallbackAction) {
				final ContextCallbackAction action = (ContextCallbackAction) item;
				menu.addCallbackAction(action.getTitle(), null, new Callback() {
					@Override
					public void onCallback() {
						actionListener.onAction(Action.callback, action);
					}
				});
			}
			index++;
		}
		return menu;
	}

	private void addComponent(ItemContextComponent c, FileSystemItem item,
			ItemDetails details) {
		if (c instanceof ItemContextSection) {
			Widget section = createSection((ItemContextSection) c, item,
					details);
			components.put(c, section);
			componentsPanel.add(section);
		} else {
			componentsPanel.add(c.getComponent());
		}
	}

	private Widget createSection(final ItemContextSection section,
			final FileSystemItem item, final ItemDetails details) {
		DisclosurePanel s = new DisclosurePanel(section.getTitle());
		s.setOpen(false);
		s.addStyleName(StyleConstants.ITEM_CONTEXT_SECTION);
		s.getHeader().getElement().getParentElement()
				.setClassName(StyleConstants.ITEM_CONTEXT_SECTION_HEADER);

		s.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				section.onOpen(item, details);
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

	private Widget createButtons() {
		buttons = new FlowPanel();
		buttons.setStyleName(StyleConstants.FILE_CONTEXT_BUTTONS);

		actionsButton = new DropdownButton(actionListener,
				textProvider.getText(Texts.fileDetailsActionsTitle),
				StyleConstants.FILE_CONTEXT_ACTIONS);

		return buttons;
	}

	private Widget createComponentsPanel() {
		componentsPanel = new VerticalPanel();
		componentsPanel
				.setStylePrimaryName(StyleConstants.ITEM_CONTEXT_COMPONENTS_PANEL);
		return componentsPanel;
	}

	public void reset() {
		componentsPanel.clear();
		actionsButton.removeAllActions();
		buttons.clear();
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

	public void showProgress() {
		progress.setVisible(true);
	}

	public void hideProgress() {
		progress.setVisible(false);
	}
}*/
