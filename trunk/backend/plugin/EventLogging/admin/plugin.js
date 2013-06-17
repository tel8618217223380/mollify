!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.config.admin.EventLogging = {
		AllEventsView : function() {
			var that = this;

			this.init = function() {
				that.title = mollify.ui.texts.get("pluginEventLoggingAdminNavTitle");
			}

			this.onActivate = function($c) {
				var listView = false;
	
				listView = new mollify.view.ConfigListView($c, {
					actions: [
						{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) {  }},
						{ id: "action-refresh", content:'<i class="icon-refresh"></i>', callback: function() { listView.table.refresh(); } }
					],
					table: {
						id: "config-admin-folders",
						key: "id",
						narrow: true,
						remote: {
							path : "eventlog/query",
							paging: 100,
							onLoad: function(pr) { $c.addClass("loading"); pr.done(function() { $c.removeClass("loading"); }); }
						},
						columns: [
							{ type:"select" },
							{ id: "icon", title:"", type:"static", content: '<i class="icon-folder-close"></i>' },
							{ id: "id", title: mollify.ui.texts.get('configAdminFoldersNameTitle') },
							{ id: "type", title: mollify.ui.texts.get('configAdminFoldersNameTitle') },
							{ id: "user", title: mollify.ui.texts.get('configAdminFoldersNameTitle') },
							{ id: "time", title: mollify.ui.texts.get('configAdminFoldersPathTitle') },
							{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
						],
						onRowAction: function(id, f) {
						}
					}
				});
				listView.table.refresh();
			}
		}
	}

	mollify.admin.plugins.EventLogging = {
		hasTexts : true,
		views: [
			new mollify.view.config.admin.EventLogging.AllEventsView()
		]
	};
}(window.jQuery, window.mollify);
