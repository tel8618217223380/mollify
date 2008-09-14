package org.sjarvela.mollify.client.service.listener;

import org.sjarvela.mollify.client.data.FileDetails;

import com.google.gwt.core.client.JavaScriptObject;

public class FileDetailsListener extends ObjectListener {
	public FileDetailsListener(ResultListener resultListener) {
		super(resultListener);
	}

	@Override
	protected boolean validate(JavaScriptObject result) {
		FileDetails fd = result.cast();
		return (fd != null);
	}
}
