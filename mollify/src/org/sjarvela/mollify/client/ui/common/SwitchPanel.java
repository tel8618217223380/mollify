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
//package org.sjarvela.mollify.client.ui.common;
//
//import java.util.Map;
//
//import org.sjarvela.mollify.client.ui.StyleConstants;
//
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.Widget;
//
//public class SwitchPanel<T> extends FlowPanel {
//	private final Map<T, Widget> content;
//
//	public SwitchPanel(String style, Map<T, Widget> content) {
//		this.content = content;
//
//		this.setStylePrimaryName(StyleConstants.SWITCH_PANEL);
//		if (style != null)
//			this.addStyleDependentName(style);
//
//		for (T o : content.keySet()) {
//			Widget widget = content.get(o);
//			this.add(widget);
//			widget.setVisible(true);
//		}
//	}
//
//	public void switchTo(T o) {
//		for (T key : content.keySet())
//			content.get(key).setVisible(key.equals(o));
//	}
//}
