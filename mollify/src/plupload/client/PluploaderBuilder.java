/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package plupload.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

public class PluploaderBuilder {
	private JavaScriptObject settings = JavaScriptObject.createObject();
	private PluploaderListener listener = null;

	public PluploaderBuilder runtimes(String runtimes) {
		set("runtimes", runtimes);
		return this;
	}

	public PluploaderBuilder flashUrl(String url) {
		set("flash_swf_url", url);
		return this;
	}

	public PluploaderBuilder silverlightUrl(String url) {
		set("silverlight_xap_url", url);
		return this;
	}

	public PluploaderBuilder uploadUrl(String uploadUrl) {
		set("url", uploadUrl);
		return this;
	}

	public PluploaderBuilder allowedFileTypes(String fileTypeList) {
		setFilters(fileTypeList);
		return this;
	}

	public PluploaderBuilder browseButton(String browseButtonId) {
		set("browse_button", browseButtonId);
		return this;
	}

	public PluploaderBuilder listener(PluploaderListener listener) {
		this.listener = listener;
		return this;
	}

	public Pluploader create() {
		if (Log.isDebugEnabled())
			Log.debug("Pluploader: " + toString(settings));
		Pluploader uploader = Pluploader.create(settings);
		if (listener != null)
			bindListener(uploader, listener);
		return uploader;
	}

	private void bindListener(Pluploader uploader,
			final PluploaderListener listener) {
		uploader.bind("Init", createFunc(new Callback() {
			@Override
			public void onCallback(JavaScriptObject p) {
				listener.onInit((InitParams) p.cast());
			}
		}));
	}

	private native JavaScriptObject createFunc(Callback callback) /*-{
		return function(uploader, p) {
			@plupload.client.PluploaderBuilder::fireCallback(Lplupload/client/PluploaderBuilder$Callback;Lcom/google/gwt/core/client/JavaScriptObject;)(callback, p);
		};
	}-*/;

	private native void set(String name, String value) /*-{
		this.@plupload.client.PluploaderBuilder::settings[name] = value;
	}-*/;

	private native void setFilters(String extensions) /*-{
		this.@plupload.client.PluploaderBuilder::settings['filters'] = [{title:'', extensions: extensions}];
	}-*/;

	private static String toString(JavaScriptObject o) {
		if (o == null)
			return "null";
		return new JSONObject(o).toString();
	}

	@SuppressWarnings("unused")
	private static void fireCallback(Callback cb, JavaScriptObject p) {
		cb.onCallback(p);
	}

	public interface Callback {
		void onCallback(JavaScriptObject param);
	}
}
