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

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
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

	public PluploaderBuilder maxFileSize(String size) {
		set("max_file_size", size);
		return this;
	}

	public PluploaderBuilder chunk(String size) {
		set("chunk_size", size);
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
			public void onCallback(Pluploader pl, JavaScriptObject p) {
				listener.onInit(pl, getString(p, "runtime"));
			}
		}));

		uploader.bind("PostInit", createFunc(new Callback() {
			@Override
			public void onCallback(Pluploader pl, JavaScriptObject p) {
				listener.postInit(pl);
			}
		}));

		uploader.bind("FilesAdded", createFunc(new Callback() {
			@Override
			public void onCallback(Pluploader pl, JavaScriptObject p) {
				listener.onFilesAdded(pl,
						asList((JsArray) p.cast(), File.class));
			}
		}));

		uploader.bind("FilesRemoved", createFunc(new Callback() {
			@Override
			public void onCallback(Pluploader pl, JavaScriptObject p) {
				listener.onFilesRemoved(pl, asList((JsArray) p.cast(),
						File.class));
			}
		}));

		uploader.bind("QueueChanged", createFunc(new Callback() {
			@Override
			public void onCallback(Pluploader pl, JavaScriptObject p) {
				listener.onQueueChanged(pl);
			}
		}));

		uploader.bind("Refresh", createFunc(new Callback() {
			@Override
			public void onCallback(Pluploader pl, JavaScriptObject p) {
				listener.onRefresh(pl);
			}
		}));

		uploader.bind("StateChanged", createFunc(new Callback() {
			@Override
			public void onCallback(Pluploader pl, JavaScriptObject p) {
				listener.onStateChanged(pl);
			}
		}));

		uploader.bind("UploadFile", createFunc(new Callback() {
			@Override
			public void onCallback(Pluploader pl, JavaScriptObject p) {
				listener.onFileUpload(pl, (File) p.cast());
			}
		}));

		uploader.bind("UploadProgress", createFunc(new Callback() {
			@Override
			public void onCallback(Pluploader pl, JavaScriptObject p) {
				listener.onFileUploadProgress(pl, (File) p.cast());
			}
		}));
	}

	private native JavaScriptObject createFunc(Callback callback) /*-{
		return function(uploader, p) {
			@plupload.client.PluploaderBuilder::fireCallback(Lplupload/client/PluploaderBuilder$Callback;Lplupload/client/Pluploader;Lcom/google/gwt/core/client/JavaScriptObject;)(callback, uploader, p);
		};
	}-*/;

	private native void set(String name, String value) /*-{
		this.@plupload.client.PluploaderBuilder::settings[name] = value;
	}-*/;

	private native void setFilters(String extensions) /*-{
		this.@plupload.client.PluploaderBuilder::settings['filters'] = [{title:'', extensions: extensions}];
	}-*/;

	@SuppressWarnings("unused")
	private static void fireCallback(Callback cb, Pluploader pl,
			JavaScriptObject p) {
		cb.onCallback(pl, p);
	}

	public interface Callback {
		void onCallback(Pluploader pl, JavaScriptObject p);
	}

	private static String toString(JavaScriptObject o) {
		if (o == null)
			return "null";
		return new JSONObject(o).toString();
	}

	private static <T> List<T> asList(JsArray array, Class<T> t) {
		List<T> result = new ArrayList();
		for (int index = 0; index < array.length(); index++)
			result.add((T) array.get(index));
		return result;
	}

	protected native String getString(JavaScriptObject p, String name) /*-{
		return p[name];
	}-*/;
}
