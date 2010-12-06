/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.plugin;

import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.service.ConfirmationListener;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.dialog.InputListener;
import org.sjarvela.mollify.client.ui.dialog.WaitDialog;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeDialogManager {
	private final DialogManager dialogManager;

	public NativeDialogManager(DialogManager dialogManager) {
		this.dialogManager = dialogManager;
	}

	public JavaScriptObject asJs() {
		return createJs(this);
	}

	private native JavaScriptObject createJs(NativeDialogManager dm) /*-{
		var o = {};

		o.showInfo = function(s) {
			return dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::showInfo(Lcom/google/gwt/core/client/JavaScriptObject;)(s);
		}
		o.showConfirmation = function(s) {
			return dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::showConfirmation(Lcom/google/gwt/core/client/JavaScriptObject;)(s);
		}
		o.showInput = function(s) {
			return dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::showInput(Lcom/google/gwt/core/client/JavaScriptObject;)(s);
		}
		o.showDialog = function(s) {
			return dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::showDialog(Lcom/google/gwt/core/client/JavaScriptObject;)(s);
		}
		o.showWait = function(m) {
			return dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::showWait(Ljava/lang/String;Ljava/lang/String;)('', m);
		}
		return o;
	}-*/;

	protected void showInfo(JavaScriptObject s) {
		JsObj spec = s.cast();
		String title = spec.getString("title");
		String message = spec.getString("message");

		dialogManager.showInfo(title, message);
	}

	protected void showConfirmation(JavaScriptObject s) {
		JsObj spec = s.cast();
		String title = spec.getString("title");
		String message = spec.getString("message");
		String style = spec.getString("style");
		final JavaScriptObject cb = spec.getJsObj("on_confirm");

		dialogManager.showConfirmationDialog(title, message,
				(style != null && !style.isEmpty()) ? style : "custom",
				new ConfirmationListener() {
					@Override
					public void onConfirm() {
						invokeNativeCallback(cb);
					}
				}, null);
	}

	protected void showInput(JavaScriptObject s) {
		JsObj spec = s.cast();
		String title = spec.getString("title");
		String message = spec.getString("message");
		String defaultVal = spec.getString("default_value");
		final JavaScriptObject cb = spec.getJsObj("on_input");
		final JavaScriptObject validator = spec.getJsObj("input_validator");

		dialogManager.showInputDialog(title, message, defaultVal,
				new InputListener() {
					@Override
					public void onInput(String text) {
						invokeNativeValueCallback(cb, text);
					}

					@Override
					public boolean isInputAcceptable(String input) {
						return invokeNativeValidator(validator, input);
					}
				});
	}

	protected void showDialog(JavaScriptObject s) {
		JsObj spec = s.cast();

	}

	protected JavaScriptObject showWait(String title, String message) {
		WaitDialog waitDialog = dialogManager.openWaitDialog(title, message);
		return createNativeWaitDialog(this, waitDialog);
	}

	private native final JavaScriptObject createNativeWaitDialog(
			NativeDialogManager dm, WaitDialog waitDialog) /*-{
		var o = {};
		o.close = function() {
			dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::closeWait(Lorg/sjarvela/mollify/client/ui/dialog/WaitDialog;)(waitDialog);
		};
		return o;
	}-*/;

	protected void closeWait(WaitDialog waitDialog) {
		waitDialog.close();
	}

	protected static native final void invokeNativeCallback(JavaScriptObject cb) /*-{
		if (cb) cb();
	}-*/;

	protected static native final void invokeNativeValueCallback(
			JavaScriptObject cb, Object v) /*-{
		if (cb) cb(v);
	}-*/;

	protected static native final boolean invokeNativeValidator(
			JavaScriptObject cb, String s) /*-{
		if (cb) return cb(s);
		return true;
	}-*/;

}
