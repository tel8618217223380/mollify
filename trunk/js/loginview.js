!function($, mollify) {

	"use strict"; // jshint ;_;
	
	mollify.view.LoginView = function(){
		var that = this;
		
		that.init = function($c) {
			mollify.dom.loadContentInto($c, mollify.templates.url("loginview.html"), that, ['localize', 'bubble']);
		}
		
		that.onLoad = function() {
			$(window).resize(that.onResize);
			that.onResize();
		
			if (mollify.features.hasFeature('lost_password')) $("#login-lost-password").show();
			if (mollify.features.hasFeature('registration')) {
				$("#mollify-login-register").click(function() {
					mollify.ui.window.open(mollify.plugins.url("Registration"));
				});
				$("#mollify-login-register").show();
			}
			
			mollify.ui.process($("#mollify-login-data"), ["center"]);
			//mollify.ui.handlers.bubble($data, that);
			$("#mollify-login-name, #mollify-login-password").bind('keypress', function(e) {
				if ((e.keyCode || e.which) == 13) that.onLogin();
			});
			$("#mollify-login-button").click(that.onLogin);
			$("#mollify-login-name").focus();
		}
		
		that.onResize = function() {
			var h = $(window).height();
			$("#mollify-login-main").height(h);
			
			var $data = $("#mollify-login-data");
			$data.css('margin-top', (h / 2) - ($data.height() / 2));
		}
		
		that.onRenderBubble = function(id, bubble) {
			if (id === 'mollify-login-forgot-password') {
				$("#mollify-login-forgot-button").click(function() {				
					var email = $("#mollify-login-forgot-email").val();
					if (!email) return;
					
					bubble.hide();
					that.wait = mollify.ui.dialogs.wait({target: "mollify-login-main"});
					//TODO that.listener.onResetPassword(email);
				});
			}
		}
		
		that.onShowBubble = function(id, bubble) {
			if (id === 'mollify-login-forgot-password') {
				$("#mollify-login-forgot-email").val("").focus();
			}
		}
		
		that.onLogin = function() {
			var username = $("#mollify-login-name").val();
			var password = $("#mollify-login-password").val();
			var remember = $("#mollify-login-remember-cb").is(':checked');
			
			if (!username || username.length < 1) {
				$("#mollify-login-name").focus();
				return;
			}
			if (!password || password.length < 1) {
				$("#mollify-login-password").focus();
				return;
			}
			that.wait = mollify.ui.dialogs.wait({target: "mollify-login-main"});
			mollify.service.post("session/authenticate", {protocol_version: 3, username: username, password: window.Base64.encode(password), remember: remember}, function(s) {
				mollify.App.setSession(s);
			}, function(c, e) {
				that.showLoginError();
			});
		}
		
		that.onResetPassword = function(email) {
			
		}
		
		that.showLoginError = function() {
			that.wait.close();
			
			mollify.ui.dialogs.notification({
				message: mollify.ui.texts.get('loginDialogLoginFailedMessage')
			});
		}
		
		that.onResetPasswordSuccess = function() {
			that.wait.close();
			
			mollify.ui.dialogs.notification({
				message: mollify.ui.texts.get('resetPasswordPopupResetSuccess')
			});
		}
		
		that.onResetPasswordFailed = function() {
			that.wait.close();
			
			mollify.ui.dialogs.info({
				message: mollify.ui.texts.get('resetPasswordPopupResetFailed')
			});
		}
	};
}(window.jQuery, window.mollify);