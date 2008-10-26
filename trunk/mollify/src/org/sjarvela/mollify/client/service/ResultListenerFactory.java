package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.ResultCallback;

public interface ResultListenerFactory {

	ResultListener createListener(ResultCallback resultCallback);

}
