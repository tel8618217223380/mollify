!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.config.admin.EventLogging = {
		AllEventsView : function() {
			var that = this;

			this.init = function() {
				that.title = mollify.ui.texts.get("pluginEventLoggingAdminNavTitle");
				that._timestampFormatter = new mollify.ui.formatters.Timestamp(mollify.ui.texts.get('shortDateTimeFormat'));
				mollify.service.get("events/types/").done(function(t) {
					that._types = [];
					that._typeTexts = t;
					for (var k in t) {
						if (t[k])
							that._types.push(k);
					}
				});
			}

			this.onActivate = function($c) {
				$c.addClass("loading");
				
				mollify.service.get("configuration/users/").done(function(users) {
					$c.removeClass("loading");

					var listView = false;
					var $optionType = false;
					var $optionUser = false;
					var $optionStart = false;
					var $optionEnd = false;
								
					var getQueryParams = function(i) {
						var start = $optionStart.get();
						var end = $optionEnd.get();
						var tp = $optionType.get();
						if (tp == "custom") tp = $("#eventlogging-event-type-custom").val();
						if (!tp || tp.length === 0) tp = null;
						var user = $optionUser.get();
						
						var params = {};
						if (start) params.start_time = mollify.helpers.formatInternalTime(start);
						if (end) params.end_time = mollify.helpers.formatInternalTime(end);
						if (user) params.user = user.name;
						if (tp) params.type = tp;
						
						return params;
					}
		
					listView = new mollify.view.ConfigListView($c, {
						actions: [
							{ id: "action-refresh", content:'<i class="icon-refresh"></i>', callback: function() { listView.table.refresh(); } }
						],
						table: {
							id: "config-admin-folders",
							key: "id",
							narrow: true,
							hilight: true,
							remote: {
								path : "eventlog/query",
								paging: { max: 50 },
								queryParams: getQueryParams,
								onLoad: function(pr) { $c.addClass("loading"); pr.done(function() { $c.removeClass("loading"); }); }
							},
							defaultSort: { id: "time", asc: false },
							columns: [
								{ type:"selectrow" },
								{ id: "icon", title:"", type:"static", content: '<i class="icon-folder-close"></i>' },
								{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle'), sortable: true },
								{ id: "type", title: mollify.ui.texts.get('pluginEventLoggingEventTypeTitle'), sortable: true },
								{ id: "user", title: mollify.ui.texts.get('pluginEventLoggingUserTitle'), sortable: true },
								{ id: "time", title: mollify.ui.texts.get('pluginEventLoggingTimeTitle'), formatter: that._timestampFormatter, sortable: true },
								{ id: "ip", title: mollify.ui.texts.get('pluginEventLoggingIPTitle'), sortable: true }
							]
						}
					});
					var $options = $c.find(".mollify-configlistview-options");
					mollify.templates.load("eventlogging-content", mollify.helpers.noncachedUrl(mollify.plugins.adminUrl("EventLogging", "content.html")), function() {
						mollify.dom.template("mollify-tmpl-eventlogging-options").appendTo($options);
						mollify.ui.process($options, ["localize"]);
						
						$optionType = mollify.ui.controls.select("eventlogging-event-type", {
							values: that._types.concat(["custom"]),
							valueMapper: function(v) { if (v == "custom") return mollify.ui.texts.get('pluginEventLoggingAdminEventTypeCustom'); return that._typeTexts[v] + " ("+v+")"; },
							none: mollify.ui.texts.get('pluginEventLoggingAdminAny'),
							onChange: function(t) {
								if (t == "custom")
									$("#eventlogging-event-type-custom").show().val("").focus();
								else
									$("#eventlogging-event-type-custom").hide();
							}
						});
						$optionUser = mollify.ui.controls.select("eventlogging-user", {
							values: users,
							valueMapper: function(u) { return u.name; },
							none: mollify.ui.texts.get('pluginEventLoggingAdminAny')
						});
						$optionStart = mollify.ui.controls.datepicker("eventlogging-start", {
							format: mollify.ui.texts.get('shortDateTimeFormat'),
							time: true
						});
						$optionEnd = mollify.ui.controls.datepicker("eventlogging-end", {
							format: mollify.ui.texts.get('shortDateTimeFormat'),
							time: true
						});
						listView.table.refresh();
					});
				});
			};
		}
	}

	mollify.admin.plugins.EventLogging = {
		hasTexts : true,
		views: [
			new mollify.view.config.admin.EventLogging.AllEventsView()
		]
	};
}(window.jQuery, window.mollify);
