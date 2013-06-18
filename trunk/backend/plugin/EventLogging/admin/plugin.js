!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.config.admin.EventLogging = {
		AllEventsView : function() {
			var that = this;

			this.init = function() {
				that.title = mollify.ui.texts.get("pluginEventLoggingAdminNavTitle");
				that._timestampFormatter = new mollify.ui.formatters.Timestamp(mollify.ui.texts.get('shortDateTimeFormat'));
				mollify.service.get("events/types/").done(function(t) {
					//TODO get keys
					that._types = t;
				});
			}

			this.onActivate = function($c) {
				var listView = false;
				
				var getQueryParams = function(i) {
					var start = $("#eventlogging-start").data("mollify-datepicker").get();
					var end = $("#eventlogging-end").data("mollify-datepicker").get();
					return { start_time: start, end_time: end };
				}
	
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
							paging: { max: 50 },
							queryParams: getQueryParams,
							onLoad: function(pr) { $c.addClass("loading"); pr.done(function() { $c.removeClass("loading"); }); }
						},
						defaultSort: { id: "time", asc: false },
						columns: [
							{ type:"select" },
							{ id: "icon", title:"", type:"static", content: '<i class="icon-folder-close"></i>' },
							{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle'), sortable: true },
							{ id: "type", title: mollify.ui.texts.get('pluginEventLoggingEventTypeTitle'), sortable: true },
							{ id: "user", title: mollify.ui.texts.get('pluginEventLoggingUserTitle'), sortable: true },
							{ id: "time", title: mollify.ui.texts.get('pluginEventLoggingTimeTitle'), formatter: that._timestampFormatter, sortable: true },
							{ id: "ip", title: mollify.ui.texts.get('pluginEventLoggingIPTitle'), sortable: true },
							{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
						],
						onRowAction: function(id, f) {
						}
					}
				});
				var $options = $c.find(".mollify-configlistview-options");
				mollify.templates.load("eventlogging-content", mollify.helpers.noncachedUrl(mollify.plugins.adminUrl("EventLogging", "content.html")), function() {
					mollify.dom.template("mollify-tmpl-eventlogging-options").appendTo($options);
					mollify.ui.process($options, ["localize"]);
					mollify.ui.controls.select("eventlogging-event-type", {
						values: that._types,
						none: "todo"
					});
					mollify.ui.controls.datepicker("eventlogging-start", {
						format: mollify.ui.texts.get('shortDateTimeFormat'),
						time: true
					});
					mollify.ui.controls.datepicker("eventlogging-end", {
						format: mollify.ui.texts.get('shortDateTimeFormat'),
						time: true
					});
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
