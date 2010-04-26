/**
	Copyright (c) 2008- Samuli Järvelä

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	this entire header must remain intact.
*/

function init(path) {
	servicePath = path;
	getSessionInfo(onSession, onServerError);				
};

function onSession(session) {
	if (!session.features["registration"] || !session["authentication_required"]) return;
	$("#register-button").click(onRegister);
	$("#registration-form").show();
}

function onRegister() {
	$(".registration-field").removeClass("invalid");
	var name = $("#username-field").val();
	var pw = $("#password-field").val();
	var confirmPw = $("#confirm-password-field").val();
	var email = $("#email-field").val();
	
	if (name.length == 0) $("#username-field").addClass("invalid");
	if (pw.length == 0) $("#password-field").addClass("invalid");
	if (confirmPw.length == 0) $("#confirm-password-field").addClass("invalid");
	if (email.length == 0) $("#email-field").addClass("invalid");
	if (name.length == 0 || pw.length == 0 || confirmPw.length == 0 || email.length == 0) return;
	
	if (pw != confirmPw) {
		$("#password-field").addClass("invalid");
		$("#confirm-password-field").addClass("invalid");
		return;
	}
	
	alert(name);
}
