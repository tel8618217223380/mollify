///**
// * Copyright (c) 2008- Samuli Järvelä
// *
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
// * this entire header must remain intact.
// */
//
//package org.sjarvela.mollify.client.plugin.filelist;
//
//import org.sjarvela.mollify.client.ui.filelist.ColumnSpec;
//
//import com.google.gwt.core.client.JavaScriptObject;
//
//public class NativeColumnSpec implements ColumnSpec {
//	private final String id;
//	private final JavaScriptObject contentCb;
//	private final JavaScriptObject sortCb;
//	private final JavaScriptObject dataRequestCb;
//	private final String defaultTitleKey;
//	private final String requestId;
//	private final JavaScriptObject onRenderCb;
//
//	public NativeColumnSpec(String id, String requestId,
//			String defaultTitleKey, JavaScriptObject contentCb,
//			JavaScriptObject sortCb, JavaScriptObject dataRequestCb,
//			JavaScriptObject onRenderCb) {
//		this.id = id;
//		this.requestId = requestId;
//		this.defaultTitleKey = defaultTitleKey;
//		this.contentCb = contentCb;
//		this.sortCb = sortCb;
//		this.dataRequestCb = dataRequestCb;
//		this.onRenderCb = onRenderCb;
//	}
//
//	@Override
//	public String getId() {
//		return id;
//	}
//
//	public String getRequestId() {
//		return requestId;
//	}
//
//	public String getDefaultTitleKey() {
//		return defaultTitleKey;
//	}
//
//	@Override
//	public boolean isSortable() {
//		return sortCb != null;
//	}
//
//	public JavaScriptObject getSortCallback() {
//		return sortCb;
//	}
//
//	public JavaScriptObject getContentCallback() {
//		return contentCb;
//	}
//
//	public JavaScriptObject getDataRequestCallback() {
//		return dataRequestCb;
//	}
//
//	public JavaScriptObject getOnRenderCb() {
//		return onRenderCb;
//	}
//
//	public boolean hasDataRequest() {
//		return requestId != null && dataRequestCb != null;
//	}
//}
