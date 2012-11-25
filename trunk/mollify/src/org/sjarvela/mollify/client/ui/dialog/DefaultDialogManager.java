/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.dialog;

import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.js.JsObjBuilder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.service.ServiceError;
import org.sjarvela.mollify.client.ui.ConfirmationListener;
import org.sjarvela.mollify.client.ui.filesystem.SelectFolderHandler;

import com.google.gwt.core.client.JavaScriptObject;

public class DefaultDialogManager implements DialogManager {
	private final TextProvider textProvider;
	private JsObj handler;

	public DefaultDialogManager(TextProvider textProvider) {
		this.textProvider = textProvider;
	}

	@Override
	public void setHandler(JavaScriptObject h) {
		this.handler = h.cast();
	}

	@Override
	public void showError(ServiceError error) {
		JsObjBuilder spec = new JsObjBuilder().string("title",
				textProvider.getText(Texts.infoDialogErrorTitle)).string(
				"text", error.getType().getMessage(textProvider));
		this.handler.call("error", spec.create());
	}

	@Override
	public void showInfo(String title, String text) {
		JsObjBuilder spec = new JsObjBuilder().string("title", title).string(
				"message", text);
		this.handler.call("info", spec.create());
	}

	@Override
	public void showInfo(String title, String text, String info) {
		JsObjBuilder spec = new JsObjBuilder().string("title", title)
				.string("text", text).string("info", info);
		this.handler.call("details", spec.create());
	}

	@Override
	public void showConfirmationDialog(String title, String message,
			ConfirmationListener listener) {
		JsObjBuilder spec = new JsObjBuilder().string("title", title)
				.string("message", message)
				.obj("callback", createNativeListener(listener));
		this.handler.call("confirmation", spec.create());
	}

	private native final JavaScriptObject createNativeListener(
			ConfirmationListener listener) /*-{
		return function() {
			listener.@org.sjarvela.mollify.client.ui.ConfirmationListener::onConfirm()();
		};
	}-*/;

	@Override
	public void showInputDialog(String title, String message,
			String defaultValue, String yesTitle, String noTitle,
			InputListener listener) {
		JsObjBuilder spec = new JsObjBuilder().string("title", title)
				.string("message", message).string("default", defaultValue)
				.string("yesTitle", yesTitle).string("noTitle", noTitle)
				.obj("handler", createNativeListener(listener));
		this.handler.call("input", spec.create());
	}

	private native final JavaScriptObject createNativeListener(
			InputListener listener) /*-{
		return {
			isAcceptable : function(i) {
				return listener.@org.sjarvela.mollify.client.ui.dialog.InputListener::isInputAcceptable(Ljava/lang/String;)(i);
			},
			onInput : function(i) {
				return listener.@org.sjarvela.mollify.client.ui.dialog.InputListener::onInput(Ljava/lang/String;)(i);
			}
		};
	}-*/;

	@Override
	public WaitDialog openWaitDialog(String title, String message) {
		JsObjBuilder spec = new JsObjBuilder().string("title", title).string(
				"message", message);
		final JsObj h = this.handler.call("wait", spec.create()).cast();
		return new WaitDialog() {
			@Override
			public void close() {
				h.call("close");
			}
		};
	}

	@Override
	public void openFolderSelector(String title, String message,
			String actionTitle, SelectFolderHandler handler) {
		JsObjBuilder spec = new JsObjBuilder().string("title", title)
				.string("message", message).string("actionTitle", actionTitle)
				.obj("handler", createNativeHandler(handler));
		this.handler.call("folderSelector", spec.create());
	}

	private native final JavaScriptObject createNativeHandler(
			SelectFolderHandler handler) /*-{
		return {
			onSelect : function(f) {
				handler.@org.sjarvela.mollify.client.ui.filesystem.SelectFolderHandler::onSelect(Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;)(f);
			},
			canSelect : function(f) {
				return handler.@org.sjarvela.mollify.client.ui.filesystem.SelectFolderHandler::canSelect(Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;)(f);
			}
		};
	}-*/;
}
