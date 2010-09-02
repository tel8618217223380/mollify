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
		var env = {};

		env.showInfo = function(t,m) {
			return dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::showInfo(Ljava/lang/String;Ljava/lang/String;)(t,m);
		}
		env.showConfirmation = function(t,m,cb,s) {
			return dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::showConfirmation(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(t,m,s,cb);
		}
		env.showInput = function(t,m,d,cb,v) {
			return dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::showInput(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(t,m,d,cb,v);
		}
		env.showDialog = function(s) {
			return dm.@org.sjarvela.mollify.client.plugin.NativeDialogManager::showDialog(Lcom/google/gwt/core/client/JavaScriptObject;)(s);
		}

		return env;
	}-*/;

	protected void showInfo(String title, String message) {
		dialogManager.showInfo(title, message);
	}

	protected void showConfirmation(String title, String message, String style,
			final JavaScriptObject cb) {
		dialogManager.showConfirmationDialog(title, message,
				(style != null && !style.isEmpty()) ? style : "custom",
				new ConfirmationListener() {
					@Override
					public void onConfirm() {
						invokeNativeCallback(cb);
					}
				}, null);
	}

	protected void showInput(String title, String message, String defaultVal,
			final JavaScriptObject cb, final JavaScriptObject validator) {
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
