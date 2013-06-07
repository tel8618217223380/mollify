!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.config.admin.EventLogging = function() {
		var that = this;
		this.title = mollify.ui.texts.get("pluginEventLoggingAdminNavTitle");

		this.onActivate = function($c) {
			$c.html("event logging");
		}
	}

	mollify.admin.plugins.push(new mollify.view.config.admin.EventLogging());
}(window.jQuery, window.mollify);
