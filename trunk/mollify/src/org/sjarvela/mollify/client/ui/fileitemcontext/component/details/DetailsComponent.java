/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

package org.sjarvela.mollify.client.ui.fileitemcontext.component.details;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sjarvela.mollify.client.ResourceId;
import org.sjarvela.mollify.client.filesystem.FileDetails;
import org.sjarvela.mollify.client.filesystem.FileSystemItem;
import org.sjarvela.mollify.client.filesystem.ItemDetails;
import org.sjarvela.mollify.client.localization.TextProvider;
import org.sjarvela.mollify.client.localization.Texts;
import org.sjarvela.mollify.client.ui.StyleConstants;
import org.sjarvela.mollify.client.ui.fileitemcontext.ItemContextContainer;
import org.sjarvela.mollify.client.ui.fileitemcontext.component.ItemContextSection;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DetailsComponent implements ItemContextSection {
	private final TextProvider textProvider;
	private final DateTimeFormat dateTimeFormat;
	private Panel component;

	private enum Details implements ResourceId {
		Accessed, Modified, Changed
	}

	static List<ResourceId> order = (List<ResourceId>) Arrays.asList(
			(ResourceId) Details.Modified, (ResourceId) Details.Changed,
			(ResourceId) Details.Accessed);
	static Map<ResourceId, String> headers = null;

	public DetailsComponent(TextProvider textProvider) {
		this.textProvider = textProvider;
		this.dateTimeFormat = com.google.gwt.i18n.client.DateTimeFormat
				.getFormat(textProvider.getText(Texts.shortDateTimeFormat));
		if (headers == null) {
			headers = new HashMap();
			headers.put(Details.Accessed,
					textProvider.getText(Texts.fileDetailsLabelLastAccessed));
			headers.put(Details.Changed,
					textProvider.getText(Texts.fileDetailsLabelLastChanged));
			headers.put(Details.Modified,
					textProvider.getText(Texts.fileDetailsLabelLastModified));
		}
	}

	@Override
	public Comparable getIndex() {
		return 2;
	}

	@Override
	public String getTitle() {
		return textProvider.getText(Texts.fileActionDetailsTitle);
	}

	@Override
	public Widget getComponent() {
		if (component == null)
			component = createContent();
		return component;
	}

	private Panel createContent() {
		Panel content = new VerticalPanel();
		content.setStyleName(StyleConstants.FILE_CONTEXT_DETAILS_CONTENT);
		return content;
	}

	@Override
	public boolean onInit(ItemContextContainer container, FileSystemItem item,
			ItemDetails d) {
		FileDetails details = d.cast();
		if (details == null)
			return false;

		createRows(getValues(details));
		return true;
	}

	private Map<ResourceId, String> getValues(FileDetails details) {
		Map<ResourceId, String> values = new HashMap();
		if (details.getLastAccessed() != null)
			values.put(Details.Accessed,
					dateTimeFormat.format(details.getLastAccessed()));
		if (details.getLastModified() != null)
			values.put(Details.Modified,
					dateTimeFormat.format(details.getLastModified()));
		if (details.getLastChanged() != null)
			values.put(Details.Changed,
					dateTimeFormat.format(details.getLastChanged()));
		return values;
	}

	private void createRows(Map<ResourceId, String> values) {
		for (ResourceId id : order) {
			if (values.containsKey(id))
				component.add(createDetailsRow(headers.get(id), id.name()
						.toLowerCase(), values.get(id)));
		}
	}

	private Widget createDetailsRow(String title, String style, String value) {
		Panel detailsRow = new HorizontalPanel();
		detailsRow.setStylePrimaryName(StyleConstants.FILE_CONTEXT_DETAILS_ROW);
		detailsRow.addStyleDependentName(style);

		Label label = new Label(title);
		label.setStylePrimaryName(StyleConstants.FILE_CONTEXT_DETAILS_ROW_LABEL);
		label.addStyleDependentName(style);
		detailsRow.add(label);

		Label valueLabel = new Label();
		valueLabel
				.setStylePrimaryName(StyleConstants.FILE_CONTEXT_DETAILS_ROW_VALUE);
		valueLabel.addStyleDependentName(style);
		valueLabel.setText(value);
		detailsRow.add(valueLabel);

		return detailsRow;
	}

	@Override
	public void onOpen() {
	}

	@Override
	public void onClose() {
	}

	@Override
	public void onContextClose() {
	}
}
