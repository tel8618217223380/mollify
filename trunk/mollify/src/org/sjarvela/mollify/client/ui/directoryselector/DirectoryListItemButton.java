package org.sjarvela.mollify.client.ui.directoryselector;

import org.sjarvela.mollify.client.ui.StyleConstants;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.Widget;

public class DirectoryListItemButton extends FlowPanel {
	private final Label left;
	private final Label center;
	private final Label right;

	public DirectoryListItemButton(String itemStyle) {
		this.setStylePrimaryName(StyleConstants.DIRECTORY_LISTITEM_BUTTON);
		if (itemStyle != null)
			this.addStyleDependentName(itemStyle);

		MouseListener mouseListener = new MouseListenerAdapter() {
			@Override
			public void onMouseLeave(Widget sender) {
				DirectoryListItemButton.this.onMouseUp();
			}

			@Override
			public void onMouseDown(Widget sender, int x, int y) {
				DirectoryListItemButton.this.onMouseDown();
			}

			@Override
			public void onMouseUp(Widget sender, int x, int y) {
				DirectoryListItemButton.this.onMouseUp();
			}
		};

		left = createPart(StyleConstants.DIRECTORY_LISTITEM_BUTTON_L,
				itemStyle, mouseListener);
		center = createPart(StyleConstants.DIRECTORY_LISTITEM_BUTTON_C,
				itemStyle, mouseListener);
		right = createPart(StyleConstants.DIRECTORY_LISTITEM_BUTTON_R,
				itemStyle, mouseListener);

		this.add(left);
		this.add(center);
		this.add(right);
	}

	private Label createPart(String style, String itemStyle,
			MouseListener mouseListener) {
		Label label = new Label();
		label.setStylePrimaryName(style);
		if (itemStyle != null)
			this.addStyleDependentName(itemStyle);
		label.addMouseListener(mouseListener);
		return label;
	}

	private void onMouseDown() {
		right.addStyleDependentName(StyleConstants.PRESSED);
		center.addStyleDependentName(StyleConstants.PRESSED);
		left.addStyleDependentName(StyleConstants.PRESSED);
	}

	protected void onMouseUp() {
		right.removeStyleDependentName(StyleConstants.PRESSED);
		center.removeStyleDependentName(StyleConstants.PRESSED);
		left.removeStyleDependentName(StyleConstants.PRESSED);
	}

	public void setText(String text) {
		center.setText(text);
	}

	public void addClickListener(ClickListener clickListener) {
		right.addClickListener(clickListener);
		center.addClickListener(clickListener);
		left.addClickListener(clickListener);
	}
}
