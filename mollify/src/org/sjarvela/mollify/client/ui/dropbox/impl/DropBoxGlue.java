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

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.ui.dnd.DragAndDropManager;
import org.sjarvela.mollify.client.ui.dropbox.DropBox;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Widget;

public class DropBoxGlue implements DropBox, DropController {
	private final DropBoxView view;
	private final DropBoxPresenter presenter;

	public DropBoxGlue(DropBoxView view, DropBoxPresenter presenter,
			DragAndDropManager dragAndDropManager) {
		this.view = view;
		this.presenter = presenter;
		
		dragAndDropManager.getController(FileSystemItem.class)
				.registerDropController(this);
	}

	@Override
	public boolean isVisible() {
		return view.isShown();
	}

	@Override
	public void toggle() {
		view.toggleShow();
	}

	@Override
	public Widget getDropTarget() {
		return view.getDropTarget();
	}

	@Override
	public void onDrop(DragContext context) {
		Log.debug("onDrop");
	}

	@Override
	public void onEnter(DragContext context) {
		Log.debug("onEnter");
	}

	@Override
	public void onLeave(DragContext context) {
		Log.debug("onLeave");
	}

	@Override
	public void onMove(DragContext context) {
		Log.debug("onMove");
	}

	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		Log.debug("onPreviewDrop");
	}

}
