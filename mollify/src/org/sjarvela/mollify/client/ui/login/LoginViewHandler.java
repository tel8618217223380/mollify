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
//package org.sjarvela.mollify.client.ui.login;
//
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import org.sjarvela.mollify.client.MollifyClient;
//import org.sjarvela.mollify.client.service.ServiceError;
//import org.sjarvela.mollify.client.service.ServiceErrorType;
//import org.sjarvela.mollify.client.service.SessionService;
//import org.sjarvela.mollify.client.service.request.listener.ResultListener;
//import org.sjarvela.mollify.client.session.SessionInfo;
//import org.sjarvela.mollify.client.session.SessionManager;
//import org.sjarvela.mollify.client.session.user.UserNameValidator;
//import org.sjarvela.mollify.client.ui.ViewManager;
//import org.sjarvela.mollify.client.ui.dialog.DialogManager;
//
//public class LoginViewHandler {
//	private static Logger logger = Logger.getLogger(LoginViewHandler.class
//			.getName());
//
//	private final DialogManager dialogManager;
//	private final SessionService service;
//	private final NativeLoginView view;
//	private final SessionManager sessionManager;
//
//	public LoginViewHandler(ViewManager viewManager,
//			DialogManager dialogManager, SessionService service,
//			SessionManager sessionManager) {
//		this.dialogManager = dialogManager;
//		this.service = service;
//		this.sessionManager = sessionManager;
//		this.view = new NativeLoginView(viewManager.getViewHandler("login"));
//
//		view.init(new LoginViewListener() {
//			@Override
//			public void onLogin(String username, String password,
//					boolean remember) {
//				LoginViewHandler.this.onLogin(username, password, remember);
//			}
//		});
//
//		viewManager.render(this.view);
//	}
//
//	private void onLogin(String username, String password, boolean remember) {
//		if (username == null || username.length() < 1)
//			return;
//
//		if (password == null || password.length() < 1)
//			return;
//
//		if (!new UserNameValidator().validate(username))
//			return;
//
//		logger.log(Level.INFO, "User login: " + username);
//
//		service.authenticate(username, password, remember,
//				MollifyClient.PROTOCOL_VERSION,
//				new ResultListener<SessionInfo>() {
//					public void onFail(ServiceError error) {
//						if (ServiceErrorType.AUTHENTICATION_FAILED.equals(error
//								.getType())) {
//							view.showLoginError();
//							return;
//						}
//						dialogManager.showError(error);
//					}
//
//					public void onSuccess(SessionInfo session) {
//						sessionManager.setSession(session);
//					}
//				});
//	}
//
//	// protected void onResetPassword(String email) {
//	// if (email == null || email.length() == 0)
//	// return;
//	//
//	// String data = new JSONBuilder("email", email).toString();
//	//
//	// resetPasswordService.post(data, new ResultListener() {
//	// @Override
//	// public void onFail(ServiceError error) {
//	// if (error.getError().getCode() == 301) {
//	// view.showResetPasswordFailed();
//	// } else if (error.getType().equals(
//	// ServiceErrorType.REQUEST_FAILED)) {
//	// dialogManager.showInfo(
//	// textProvider.getText(Texts.resetPasswordPopupTitle),
//	// textProvider
//	// .getText(Texts.resetPasswordPopupResetFailed));
//	// } else {
//	// dialogManager.showError(error);
//	// }
//	// }
//	//
//	// @Override
//	// public void onSuccess(Object result) {
//	// view.showResetPasswordSuccess();
//	// }
//	// });
//	// }
//}
