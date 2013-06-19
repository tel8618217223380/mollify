!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.config.admin.Registration = {
		PendingRegistrationsView : function() {
			var that = this;

			this.init = function() {
				that.title = "Registrations";
			}

			this.onActivate = function($c) {
				$c.html("todo");
			};
		}
	}

	mollify.admin.plugins.Registration = {
		hasTexts : false,
		views: [
			new mollify.view.config.admin.Registration.PendingRegistrationsView()
		]
	};
}(window.jQuery, window.mollify);
