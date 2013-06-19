!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.config.admin.Notificator = {
		NotificationsView : function() {
			var that = this;

			this.init = function() {
				that.title = "Notifications";
			}

			this.onActivate = function($c) {
				$c.html("todo");
			};
		}
	}

	mollify.admin.plugins.Registration = {
		hasTexts : false,
		views: [
			new mollify.view.config.admin.Notificator.NotificationsView()
		]
	};
}(window.jQuery, window.mollify);
