//package org.sjarvela.mollify.client.ui.mainview.impl;
//
//import java.util.List;
//
//import org.sjarvela.mollify.client.filesystem.js.JsFolder;
//import org.sjarvela.mollify.client.filesystem.js.JsRootFolder;
//import org.sjarvela.mollify.client.js.JsObj;
//import org.sjarvela.mollify.client.js.JsObjBuilder;
//import org.sjarvela.mollify.client.ui.NativeView;
//import org.sjarvela.mollify.client.ui.mainview.MainView;
//import org.sjarvela.mollify.client.ui.mainview.MainViewListener;
//import org.sjarvela.mollify.client.util.JsUtil;
//
//import com.google.gwt.core.client.JavaScriptObject;
//
//public class NativeMainView extends NativeView implements MainView {
//	public NativeMainView(JsObj viewHandler) {
//		super(viewHandler);
//	}
//
//	@Override
//	public void init(List<JsRootFolder> rootFolders, MainViewListener listener) {
//		JsObj p = new JsObjBuilder()
//				.obj("roots", JsUtil.asJsArray(rootFolders, JsRootFolder.class))
//				.obj("listener", createJsListener(listener)).create();
//		viewHandler.call("init", p);
//	}
//
//	private native JavaScriptObject createJsListener(MainViewListener listener) /*-{
//		return {
//			onViewLoaded : function(u, p, r) {
//				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onViewLoaded()();
//			},
//			onHomeSelected : function(f) {
//				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onHomeSelected()();
//			},
////			onCopy : function(t, i) {
////				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onCopy(Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;Lcom/google/gwt/core/client/JavaScriptObject;)(t, i);
////			},
////			onMove : function(t, i) {
////				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onMove(Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;Lcom/google/gwt/core/client/JavaScriptObject;)(t, i);
////			},
//			//			onSubFolderSelected : function(f) {
//			//				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onSubFolderSelected(Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;)(f);
//			//			},
//			onSearch : function(s, cb) {
//				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onSearch(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(s, cb);
//			},
//			onRefresh : function() {
//				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onRefresh()();
//			},
//			onCreateFolder : function(n) {
//				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onCreateFolder(Ljava/lang/String;)(n);
//			},
//			onFolderSelected : function(f) {
//				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onFolderSelected(Lorg/sjarvela/mollify/client/filesystem/js/JsFolder;)(f);
//			},
//			getSessionActions : function(cb) {
//				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::getSessionActions(Lcom/google/gwt/core/client/JavaScriptObject;)(cb);
//			},
//			onChangePassword : function(o, n, cb) {
//				listener.@org.sjarvela.mollify.client.ui.mainview.MainViewListener::onChangePassword(Ljava/lang/String;Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(o,n,cb);
//			}
//		};
//	}-*/;
//
//	@Override
//	public JavaScriptObject getDataRequest(JsFolder folder) {
//		return viewHandler.call("getDataRequest", folder);
//	}
//
//	@Override
//	public void showAllRoots() {
//		viewHandler.call("showAllRoots");
//	}
//
//	@Override
//	public void showNoRoots() {
//		viewHandler.call("showNoRoots");
//	}
//
//	@Override
//	public void showProgress() {
//		viewHandler.call("showProgress");
//	}
//
//	@Override
//	public void hideProgress() {
//		viewHandler.call("hideProgress");
//	}
//
//	@Override
//	public void clear() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void onChangePassword() {
//		viewHandler.call("changePassword");
//	}
//
//	@Override
//	public void onOpenAdminUtil(String url) {
//		viewHandler.call("openAdminUtil", url);
//	}
//
//	@Override
//	public void setFolder(List<JsFolder> folderHierarchy, boolean canWrite) {
//		viewHandler.call(
//				"folder",
//				new JsObjBuilder()
//						.obj("hierarchy",
//								JsUtil.asJsArray(folderHierarchy,
//										JsFolder.class))
//						.bool("canWrite", canWrite).create());
//	}
//
//	@Override
//	public void setData(List<JavaScriptObject> allItems, JsObj data) {
//		viewHandler.call(
//				"data",
//				new JsObjBuilder()
//						.obj("items",
//								JsUtil.asJsArray(allItems,
//										JavaScriptObject.class))
//						.obj("data", data).create());
//	}
//
//}
