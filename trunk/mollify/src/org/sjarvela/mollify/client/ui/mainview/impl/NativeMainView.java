package org.sjarvela.mollify.client.ui.mainview.impl;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.js.JsFolder;
import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.js.JsObjBuilder;
import org.sjarvela.mollify.client.ui.NativeView;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;
import org.sjarvela.mollify.client.ui.mainview.MainView;
import org.sjarvela.mollify.client.ui.mainview.MainViewListener;
import org.sjarvela.mollify.client.util.JsUtil;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class NativeMainView extends NativeView implements MainView {
	public NativeMainView(JsObj viewHandler) {
		super(viewHandler);
	}

	@Override
	public void init(List<JsRootFolder> rootFolders, MainViewListener listener) {
		JsObj p = new JsObjBuilder().obj("roots", JsArray.createArray())
				.obj("listener", createJsListener(listener)).create();
		viewHandler.call("init", p);
	}

	private native JavaScriptObject createJsListener(MainViewListener listener) /*-{
		return {
			onViewLoaded : function(u, p, r) {
				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onViewLoaded()();
			}
		};
	}-*/;

	@Override
	public void showNoPublishedFolders() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showAddButton(boolean show) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showProgress() {
		viewHandler.call("showProgress");
	}

	@Override
	public void hideProgress() {
		viewHandler.call("hideProgress");
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public ViewType getViewType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFolder(List<JsFolder> folderHierarchy, boolean canWrite) {
		viewHandler.call(
				"folder",
				new JsObjBuilder().obj("items",
						JsUtil.asJsArray(folderHierarchy, JsFolder.class))
						.create());
	}

	@Override
	public void setData(List<JavaScriptObject> allItems, JsObj data) {
		viewHandler.call(
				"data",
				new JsObjBuilder()
						.obj("items",
								JsUtil.asJsArray(allItems,
										JavaScriptObject.class))
						.obj("data", data).create());
	}

	@Override
	public void sortColumn(String columnId, SortOrder sort) {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectNone() {
		// TODO Auto-generated method stub

	}

}