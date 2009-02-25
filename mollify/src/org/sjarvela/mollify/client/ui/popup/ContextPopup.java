package org.sjarvela.mollify.client.ui.popup;

import org.sjarvela.mollify.client.filesystem.FileSystemAction;
import org.sjarvela.mollify.client.ui.ActionListener;
import org.sjarvela.mollify.client.ui.common.BorderedControl;
import org.sjarvela.mollify.client.ui.common.DropdownPopup;
import org.sjarvela.mollify.client.ui.common.HoverDecorator;
import org.sjarvela.mollify.client.ui.common.MultiActionButton;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContextPopup extends DropdownPopup {
	private final String styleName;

	public ContextPopup(String styleName) {
		super(null, null);
		this.styleName = styleName;
		this.setStyleName(styleName);
	}

	protected void initialize() {
		BorderedControl content = new BorderedControl(styleName + "-border");
		content.setContent(createContent());

		addItem(content);
		addItem(createPointer());
		addItem(createCloseButton());
	}

	protected abstract Widget createContent();

	private Widget createPointer() {
		FlowPanel pointer = new FlowPanel();
		pointer.setStyleName(styleName + "-pointer");
		return pointer;
	}

	private Widget createCloseButton() {
		final Label close = new Label();
		close.setStyleName(styleName + "-close");
		HoverDecorator.decorate(close);
		close.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				HoverDecorator.clear(close);
				ContextPopup.this.hide();
			}
		});
		return close;
	}

	protected Button createActionButton(String title,
			final FileSystemAction action) {
		String base = styleName + "-action";

		Button button = new Button(title);
		button.addStyleName(base);
		button.getElement().setId(base + "-" + action.name().toLowerCase());
		button.addClickListener(new ClickListener() {
			public void onClick(Widget sender) {
				onAction(action);
			}
		});
		return button;
	}

	protected MultiActionButton createMultiActionButton(ActionListener listener,
			String title, String id) {
		return new MultiActionButton(listener, title,
				(styleName + "-multiaction"), id);
	}

	protected abstract void onAction(FileSystemAction action);

	public void setParent(Element element) {
		super.setParentElement(element);
		super.setOpenerElement(element);
	}
}
