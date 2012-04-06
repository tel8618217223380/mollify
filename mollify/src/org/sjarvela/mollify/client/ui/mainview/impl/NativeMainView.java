package org.sjarvela.mollify.client.ui.mainview.impl;

import java.util.List;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.ui.NativeView;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;
import org.sjarvela.mollify.client.ui.mainview.MainView;

public class NativeMainView extends NativeView implements MainView {
	public NativeMainView(JsObj viewHandler) {
		super(viewHandler);
	}
	
	@Override
	public void setUsername(String user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideButtons() {
		// TODO Auto-generated method stub

	}

	@Override
	public void showAddButton(boolean show) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showProgress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hideProgress() {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public ViewType getViewType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setData(List<FileSystemItem> allItems, JsObj data) {
		// TODO Auto-generated method stub

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
