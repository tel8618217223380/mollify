/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dropbox.impl;

import java.util.List;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.session.SessionInfo;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.action.ActionListener;
import org.sjarvela.mollify.client.ui.common.Coords;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;
import org.sjarvela.mollify.client.ui.common.popup.DropdownButton;
import org.sjarvela.mollify.client.ui.formatter.PathFormatter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class DropBoxView extends DialogBox {
	enum Actions implements ResourceId {
		clear, remove, copy, move, delete, copyHere, moveHere, downloadAsZip
	};

	private final ActionListener actionListener;
	private final SessionInfo session;
	private final TextProvider textProvider;
	private final PathFormatter pathFormatter;

	private boolean shown = false;

	private Panel dropTarget;
	private Panel contents;
	private DropdownButton actionsButton;

	public DropBoxView(TextProvider textProvider,
			ActionListener actionListener, SessionInfo session,
			PathFormatter pathFormatter) {
		super(false, false);
		this.textProvider = textProvider;
		this.actionListener = actionListener;
		this.pathFormatter = pathFormatter;
		this.session = session;

		this.setText(textProvider.getText(Texts.dropBoxTitle));
		this.setStylePrimaryName(StyleConstants.DROPBOX_VIEW);
		this.add(createContent());
		this.show();
		this.setVisible(false);
	}

	private Widget createContent() {
		Panel panel = new FlowPanel();
		panel.setStylePrimaryName(StyleConstants.DROPBOX_VIEW_CONTENT);

		dropTarget = new FlowPanel();
		dropTarget.setStylePrimaryName(StyleConstants.DROPBOX_VIEW_DROPZONE);
		panel.add(dropTarget);

		contents = new FlowPanel();
		contents.setStylePrimaryName(StyleConstants.DROPBOX_VIEW_CONTENTS);
		dropTarget.add(contents);

		Panel actions = new FlowPanel();
		actions.setStylePrimaryName(StyleConstants.DROPBOX_VIEW_ACTIONS);
		panel.add(actions);

		actionsButton = new DropdownButton(actionListener,
				textProvider.getText(Texts.dropBoxActions),
				StyleConstants.DROPBOX_VIEW_ACTIONS_BUTTON);
		actionsButton.addAction(Actions.clear,
				textProvider.getText(Texts.dropBoxActionClear));
		actionsButton.addSeparator();
		actionsButton.addAction(Actions.copy,
				textProvider.getText(Texts.dropBoxActionCopy));
		actionsButton.addAction(Actions.copyHere,
				textProvider.getText(Texts.dropBoxActionCopyHere));
		actionsButton.addAction(Actions.move,
				textProvider.getText(Texts.dropBoxActionMove));
		actionsButton.addAction(Actions.moveHere,
				textProvider.getText(Texts.dropBoxActionMoveHere));
		actionsButton.addSeparator();
		actionsButton.addAction(Actions.delete,
				textProvider.getText(Texts.fileActionDeleteTitle));

		if (session.getFeatures().zipDownload()) {
			actionsButton.addSeparator();
			actionsButton.addAction(Actions.downloadAsZip,
					textProvider.getText(Texts.fileActionDownloadZippedTitle));
		}
		actions.add(actionsButton);

		return panel;
	}

	public Widget getDropTarget() {
		return dropTarget;
	}

	public void toggleShow(final Coords position) {
		if (!shown)
			setPopupPositionAndShow(new PositionCallback() {
				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {
					setPopupPosition(
							position.getX() - DropBoxView.this.getOffsetWidth()
									- 5, position.getY() + 10);
				}
			});
		else
			setVisible(!isVisible());

		shown = true;
	}

	public void onDragEnter() {
		dropTarget.addStyleDependentName(StyleConstants.DRAG_OVER);
	}

	public void onDragLeave() {
		dropTarget.removeStyleDependentName(StyleConstants.DRAG_OVER);
	}

	public void setContent(List<FileSystemItem> items) {
		contents.clear();
		for (FileSystemItem item : items)
			contents.add(createItemWidget(item));
	}

	private Widget createItemWidget(final FileSystemItem item) {
		String itemPath = getPath(item);

		Panel w = new FlowPanel();
		w.setTitle(itemPath + item.getName());

		w.setStylePrimaryName(StyleConstants.DROPBOX_VIEW_ITEM);
		if (item.isFile())
			w.addStyleDependentName(StyleConstants.DROPBOX_VIEW_ITEM_FILE);
		else
			w.addStyleDependentName(StyleConstants.DROPBOX_VIEW_ITEM_FOLDER);

		final Label name = new Label(item.getName());
		name.setStylePrimaryName(StyleConstants.DROPBOX_VIEW_ITEM_NAME);
		w.add(name);

		final Label path = new Label(itemPath);
		path.setStylePrimaryName(StyleConstants.DROPBOX_VIEW_ITEM_PATH);
		w.add(path);

		Label remove = new Label();
		remove.setStylePrimaryName(StyleConstants.DROPBOX_VIEW_ITEM_REMOVE);
		HoverDecorator.decorate(remove);
		w.add(remove);

		remove.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				actionListener.onAction(Actions.remove, item);
			}
		});

		return w;
	}

	private String getPath(FileSystemItem item) {
		return pathFormatter.format(item);
	}

	public Widget getActionButton() {
		return actionsButton;
	}

	public void setActionsEnabled(boolean enabled) {
		actionsButton.setEnabled(enabled);
	}
}
