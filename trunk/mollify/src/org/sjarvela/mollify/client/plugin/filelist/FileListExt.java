package org.sjarvela.mollify.client.plugin.filelist;

/*import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.js.JsObj;
import org.sjarvela.mollify.client.js.JsObjBuilder;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.ui.common.grid.GridColumn;
import org.sjarvela.mollify.client.ui.common.grid.GridComparator;
import org.sjarvela.mollify.client.ui.common.grid.GridData;
import org.sjarvela.mollify.client.ui.common.grid.SortOrder;

import com.google.gwt.core.client.JavaScriptObject;

public class FileListExt {
	private final Map<String, NativeColumnSpec> customColumnSpecs = new HashMap();
	private final TextProvider textProvider;

	public FileListExt(TextProvider textProvider) {
		this.textProvider = textProvider;
	}

	public void addListColumnSpec(JavaScriptObject s) {
		JsObj spec = s.cast();
		String id = spec.getString("id");
		String requestId = spec.getString("request-id");
		JavaScriptObject contentCb = spec.getObject("content");
		JavaScriptObject sortCb = spec.getObject("sort");
		JavaScriptObject dataRequestCb = spec.getObject("request");
		JavaScriptObject onRenderCb = spec.getObject("on-render");
		String defaultTitleKey = spec.getString("default-title-key");
		this.customColumnSpecs.put(id, new NativeColumnSpec(id, requestId,
				defaultTitleKey, contentCb, sortCb, dataRequestCb, onRenderCb));
	}

	public GridComparator getComparator(String columnId, SortOrder sort,
			JsObj data) {
		final NativeFileListComparator comparator = new NativeFileListComparator(
				customColumnSpecs.get(columnId), sort, data);
		return new GridComparator<FileSystemItem>() {
			@Override
			public int compare(FileSystemItem i1, FileSystemItem i2) {
				if (i1.isFile() && !i2.isFile())
					return 1;
				if (!i1.isFile() && i2.isFile())
					return -1;
				return comparator.compare(i1, i2);
			}

			@Override
			public String getColumnId() {
				return comparator.getColumnId();
			}

			@Override
			public SortOrder getSort() {
				return comparator.getSort();
			}
		};
	}

	public GridColumn getColumn(String id, String titleKey,
			boolean allowSortable) {
		NativeColumnSpec colSpec = customColumnSpecs.get(id);
		if (colSpec == null)
			return null;

		String effectiveTitleKey = (titleKey != null ? titleKey : (colSpec
				.getDefaultTitleKey() != null ? colSpec.getDefaultTitleKey()
				: ""));
		String title = (effectiveTitleKey != null && !effectiveTitleKey
				.isEmpty()) ? textProvider.getText(effectiveTitleKey) : "";
		return new NativeGridColumn(id, colSpec, title, colSpec.isSortable()
				&& allowSortable);
	}

	public GridData getData(GridColumn column, FileSystemItem item, JsObj data) {
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
				rq.obj(colSpec.getRequestId(), JavaScriptObject.createObject());
			else
				rq.obj(colSpec.getRequestId(),
						invokeDataRequestCallback(
								colSpec.getDataRequestCallback(), i.asJs()));
		}
		return rq.create();
	}

	protected static native final JavaScriptObject invokeDataRequestCallback(
			JavaScriptObject cb, JavaScriptObject i) /*-{
		return cb(i);
	}-;

	public void onFileListRendered(GridColumn col) {
		NativeGridColumn c = (NativeGridColumn) col;
		if (c.getColSpec().getOnRenderCb() == null)
			return;
		invokeRenderCallback(c.getColSpec().getOnRenderCb());
	}

	private static native void invokeRenderCallback(JavaScriptObject cb) /*-{
		cb();
	}-;
}*/
