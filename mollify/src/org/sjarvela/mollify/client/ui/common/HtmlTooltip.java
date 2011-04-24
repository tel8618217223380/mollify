/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.common;

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HtmlTooltip extends Tooltip {

	public HtmlTooltip(String style, String html) {
		super(style, html);
	}

	@Override
	protected Widget createContent(String content) {
		HTML c = new HTML(content);
		c.setStylePrimaryName(StyleConstants.TOOLTIP_CONTENT);
		return c;
	}

}
