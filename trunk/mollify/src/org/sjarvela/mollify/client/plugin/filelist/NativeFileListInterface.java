package org.sjarvela.mollify.client.plugin.filelist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.js.JsObjBuilder;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;

import com.google.gwt.core.client.JavaScriptObject;

public class NativeFileListInterface {
	private final Map<String, NativeColumnSpec> customColumnSpecs = new HashMap();

	public void addListColumnSpec(String id, JavaScriptObject contentCb,
			JavaScriptObject sortCb, JavaScriptObject dataRequestCb) {
		this.customColumnSpecs.put(id, new NativeColumnSpec(id, contentCb,
				sortCb, dataRequestCb));
	}

	public GridComparator getListColumnComparator(String columnId,
			SortOrder sort) {
		return new NativeFileListComparator(customColumnSpecs.get(columnId),
				sort);
	}

	public GridColumn createNativeGridColumn(String id, String title,
			boolean allowSortable) {
		NativeColumnSpec colSpec = customColumnSpecs.get(id);
		if (colSpec == null)
			return null;
		return new NativeGridColumn(id, colSpec, title, colSpec.isSortable()
				&& allowSortable);
	}

	public GridData getNativeColumnData(GridColumn column, FileSystemItem item,
			JsObj data) {
		return ((NativeGridColumn) column).getData(item, data);
	}

	public JavaScriptObject getDataRequest(FileSystemItem i,
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

}
