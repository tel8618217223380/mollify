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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.FileView;
import org.sjarvela.mollify.client.event.DefaultEventDispatcher;
import org.sjarvela.mollify.client.event.EventDispatcher;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.js.JsObjBuilder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.plugin.filelist.NativeColumnDataProvider;
import org.sjarvela.mollify.client.plugin.filelist.NativeColumnSpec;
import org.sjarvela.mollify.client.plugin.filelist.NativeFileListComparator;
import org.sjarvela.mollify.client.plugin.filelist.NativeGridColumn;
import org.sjarvela.mollify.client.plugin.itemcontext.NativeItemContextProvider;
import org.sjarvela.mollify.client.plugin.response.NativeResponseProcessor;
import org.sjarvela.mollify.client.plugin.service.NativeService;
import org.sjarvela.mollify.client.service.ServiceProvider;
import org.sjarvela.mollify.client.service.request.ResponseInterceptor;
import org.sjarvela.mollify.client.session.SessionProvider;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;
import org.sjarvela.mollify.client.ui.dialog.DialogManager;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextHandler;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextProvider;
import org.sjarvela.mollify.client.ui.fileupload.FileUploadDialogFactory;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DefaultPluginEnvironment implements PluginEnvironment {
	private final EventDispatcher eventDispatcher;
	private final ResponseInterceptor responseInterceptor;
	private final ItemContextProvider itemContextProvider;
	private final SessionProvider sessionProvider;
	private final ServiceProvider serviceProvider;
	private final DialogManager dialogManager;
	private final TextProvider textProvider;
	private final Map<String, NativeColumnSpec> customColumnSpecs = new HashMap();
	private FileUploadDialogFactory uploader = null;
	private List<Plugin> plugins;

	@Inject
	public DefaultPluginEnvironment(EventDispatcher eventDispatcher,
			ResponseInterceptor responseInterceptor,
			ItemContextProvider itemContextProvider,
			SessionProvider sessionProvider, ServiceProvider serviceProvider,
			DialogManager dialogManager, TextProvider textProvider) {
		this.eventDispatcher = eventDispatcher;
		this.responseInterceptor = responseInterceptor;
		this.itemContextProvider = itemContextProvider;
		this.sessionProvider = sessionProvider;
		this.serviceProvider = serviceProvider;
		this.dialogManager = dialogManager;
		this.textProvider = textProvider;
	}

	@Override
	public void onPluginsInitialized(List<Plugin> plugins) {
		this.plugins = plugins;
	}

	public void addResponseProcessor(JavaScriptObject rp) {
		responseInterceptor.addProcessor(new NativeResponseProcessor(rp));
	}

	public void addEventHandler(JavaScriptObject eh) {
		// TODO use proper interface here instead of casting
		((DefaultEventDispatcher) eventDispatcher).addEventHandler(eh);
	}

	public void addUploader(JavaScriptObject uploader) {
		this.uploader = new NativeUploader(uploader);
	}

	public void addItemContextProvider(JavaScriptObject dp) {
		((ItemContextHandler) itemContextProvider)
				.addItemContextProvider(new NativeItemContextProvider(dp));
	}

	public void addListColumnSpec(String id, JavaScriptObject contentCb,
			JavaScriptObject sortCb, JavaScriptObject dataRequestCb) {
		this.customColumnSpecs.put(id, new NativeColumnSpec(id, contentCb,
				sortCb, dataRequestCb));
	}

	@Override
	public GridComparator getListColumnComparator(String columnId,
			SortOrder sort) {
		return new NativeFileListComparator(customColumnSpecs.get(columnId),
				sort);
	}

	@Override
	public GridColumn createNativeGridColumn(String id, String title,
			boolean allowSortable) {
		NativeColumnSpec colSpec = customColumnSpecs.get(id);
		if (colSpec == null)
			return null;
		return new NativeGridColumn(id, colSpec, title, colSpec.isSortable()
				&& allowSortable);
	}

	@Override
	public JavaScriptObject getFileListDataRequest(FileSystemItem i,
			List<GridColumn> cols) {
		JsObjBuilder rq = new JsObjBuilder();
		for (GridColumn c : cols) {
			if (!(c instanceof NativeGridColumn))
				continue;

			NativeColumnSpec colSpec = ((NativeGridColumn) c).getColSpec();
			if (!colSpec.hasDataRequest())
				continue;
			rq.obj(colSpec.getId(),
					invokeDataRequestCallback(colSpec.getDataRequestCallback(),
							i.asJs()));
		}
		return rq.create();
	}

	protected static native final JavaScriptObject invokeDataRequestCallback(
			JavaScriptObject cb, JavaScriptObject i) /*-{
		return cb(i);
	}-*/;

	@Override
	public GridData getNativeColumnData(GridColumn column, FileSystemItem item,
			JsObj data) {
		return new NativeColumnDataProvider((NativeGridColumn) column).getData(
				item, data);
	}

	protected JavaScriptObject getSession() {
		return new NativeSession(sessionProvider.getSession()).asJs();
	}

	@Override
	public JavaScriptObject getJsEnv(FileView fileView, String pluginBaseUrl) {
		JavaScriptObject fs = new NativeFileView(fileView).asJs();
		return createNativeEnv(this, fs, pluginBaseUrl);
	}

	protected JavaScriptObject getService() {
		return new NativeService(serviceProvider.getExternalService()).asJs();
	};

	protected JavaScriptObject getDialogManager() {
		return new NativeDialogManager(dialogManager).asJs();
	};

	protected JavaScriptObject getTextProvider() {
		return new NativeTextProvider(textProvider).asJs();
	};

	protected JavaScriptObject getLogger() {
		return new NativeLogger().asJs();
	};

	protected JavaScriptObject getFileView() {
		return new NativeTextProvider(textProvider).asJs();
	};

	private native JavaScriptObject createNativeEnv(DefaultPluginEnvironment e,
			JavaScriptObject fv, String pluginBaseUrl) /*-{
		var env = {};

		env.addResponseProcessor = function(cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::addResponseProcessor(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.addUploader = function(cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::addUploader(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.addEventHandler = function(cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::addEventHandler(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.addItemContextProvider = function(cb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::addItemContextProvider(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
		}

		env.addListColumnSpec = function(id, contentCb, sortCb, dataRequestCb) {
			e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::addListColumnSpec(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;Lcom/google/gwt/core/client/JavaScriptObject;)(id, contentCb, sortCb, dataRequestCb);
		}

		env.session = function() {
			return e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::getSession()();
		}

		env.service = function() {
			return e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::getService()();
		}

		env.dialog = function() {
			return e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::getDialogManager()();
		}

		env.texts = function() {
			return e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::getTextProvider()();
		}

		env.log = function() {
			return e.@org.sjarvela.mollify.client.plugin.DefaultPluginEnvironment::getLogger()();
		}

		env.fileview = function() {
			return fv;
		}

		env.pluginUrl = function(id) {
			return pluginBaseUrl + id + "/";
		}

		return env;
	}-*/;

	@Override
	public FileUploadDialogFactory getCustomUploader() {
		return uploader;
	}

}
