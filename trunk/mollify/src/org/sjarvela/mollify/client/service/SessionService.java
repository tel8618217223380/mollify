package org.sjarvela.mollify.client.service;

import org.sjarvela.mollify.client.request.ResultListener;

public interface SessionService {

	void getSessionInfo(ResultListener resultListener);

	void authenticate(String userName, String password,
			ResultListener resultListener);

	void logout(ResultListener resultListener);

}
