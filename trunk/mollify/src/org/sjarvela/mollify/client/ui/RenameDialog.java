package org.sjarvela.mollify.client.ui;

import org.sjarvela.mollify.client.data.File;
import org.sjarvela.mollify.client.localization.Localizator;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class RenameDialog extends DialogBox {
	private File file;
	private Localizator localizator;

	public RenameDialog(File file, Localizator localizator) {
		super(false, true);

		this.file = file;
		this.localizator = localizator;

		this.addStyleName(StyleConstants.RENAME_FILE_DIALOG);
		this.setText(localizator.getMessages().renameFileDialogTitle(
				file.getName()));

		VerticalPanel content = new VerticalPanel();
		content.add(createContent());
		content.add(createButtons());

		this.add(content);

		this.setPopupPositionAndShow(new PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});
	}

	private Widget createContent() {
		HorizontalPanel panel = new HorizontalPanel();

		Label originalNameTitle = new Label(localizator.getStrings()
				.renameFileDialogOriginalName());
		originalNameTitle
				.setStyleName(StyleConstants.RENAME_FILE_ORIGINAL_NAME_TITLE);
		panel.add(originalNameTitle);

		Label originalName = new Label(file.getName());
		originalName
				.setStyleName(StyleConstants.RENAME_FILE_ORIGINAL_NAME_VALUE);
		panel.add(originalName);

		Label newNameTitle = new Label(localizator.getStrings()
				.renameFileDialogNewName());
		newNameTitle.setStyleName(StyleConstants.RENAME_FILE_NEW_NAME_TITLE);
		panel.add(newNameTitle);

		TextBox name = new TextBox();
		name.addStyleName(StyleConstants.RENAME_FILE_NEW_NAME_VALUE);
		name.setText(file.getName());
		if (file.getExtension().length() > 0)
			name.setSelectionRange(0, file.getName().length()
					- (file.getExtension().length() + 1));
		panel.add(name);

		return panel;
	}

	private Widget createButtons() {
		HorizontalPanel buttons = new HorizontalPanel();

		Button button = new Button("A");
		button.addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				RenameDialog.this.hide();
			}
		});
		buttons.add(button);

		button = new Button("B");
		buttons.add(button);

		return buttons;
	}
}
