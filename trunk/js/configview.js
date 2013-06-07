!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.MainViewConfigView = function() {
		var that = this;
		this._views = [];
		this._adminViews = [];
		this._adminViewsLoaded = false;

		this.init = function(mv) {
			that.title = mollify.ui.texts.get('configviewMenuTitle');
			that._views.push(new mollify.view.config.user.AccountView(mv));

			$.each(mollify.plugins.getConfigViewPlugins(), function(i, p) {
				if (!p.configViewHandler.views) return;

				var views = p.configViewHandler.views();
				if (!views) return;
				
				$.each(views, function(i, v) {
					that._views.push(v);
				});
			});
		}

		this.onActivate = function(h) {
			mollify.templates.load("configview", mollify.templates.url("configview.html"), function() {
				mollify.dom.template("mollify-tmpl-configview").appendTo(h.content);

				var navBarItems = [];
				$.each(that._views, function(i, v) {
					navBarItems.push({title:v.title, obj: v, callback:function(){ that._activateView(v); }})
				});

				that._userNav = h.addNavBar({
					title: mollify.ui.texts.get("configViewUserNavTitle"),
					items: navBarItems
				});
			});

			if (mollify.session.admin) {
				if (this._adminViewsLoaded) {
					that._initAdminViews(h);
				} else {
					that._adminViews.push(new mollify.view.config.admin.UsersView());

					var plugins = [];
					for (var k in mollify.session.plugins) {
						if (!mollify.session.plugins[k] || !mollify.session.plugins[k].admin) continue;
						plugins.push(k);
					};
					mollify.admin = {
						plugins : []
					};
					that._loadAdminPlugins(plugins).then(function(){
						$.each(mollify.admin.plugins, function(i, v) {
							that._adminViews.push(v);
						});
						that._initAdminViews(h);
					});
					this._adminViewsLoaded = true;
				}
			}
		}

		this._loadAdminPlugins = function(ids) {
			if (ids.length == 0) return $.Deferred().resolve([]);

			var l = [];
			for (var i=0,j=ids.length;i<j;i++) {
				l.push($.getScript("backend/plugin/"+ids[i]+"/admin/plugin.js"));
			}
			return $.when.apply($, l);
		}

		this._initAdminViews = function(h) {
			if (!mollify.session.admin || that._adminViews.length == 0) return;

			var navBarItems = [];
			$.each(that._adminViews, function(i, v) {
				navBarItems.push({title:v.title, obj: v, callback:function(){ that._activateView(v); }})
			});

			that._adminNav = h.addNavBar({
				title: mollify.ui.texts.get("configViewAdminNavTitle"),
				items: navBarItems
			});
		}

		this._activateView = function(v) {
			that._userNav.setActive(v);

			$("#mollify-configview-header").html(v.title);
			v.onActivate($("#mollify-configview-content").empty());
		}

		this.onDeactivate = function() {}
	}

	mollify.view.ConfigListView = function($e, o) {
		mollify.dom.template("mollify-tmpl-configlistview").appendTo($e);
		var $table = $e.find(".mollify-configview-table");
		var table = mollify.ui.controls.table($table, {
			key: o.table.key,
			columns: o.table.columns,
			onRowAction: function(id, obj) {
				if (o.onTableRowAction) o.onTableRowAction(table, id, obj);
			}
		});

		return {
			table: table
		};
	}

	mollify.view.config = {
		user: {},
		admin: {}
	};

	mollify.view.config.user.AccountView = function(mv) {
		var that = this;
		this.title = mollify.ui.texts.get("configUserAccountNavTitle");

		this.onActivate = function($c) {
			mollify.dom.template("mollify-tmpl-config-useraccountview", mollify.session).appendTo($c);
			mollify.ui.process($c, ["localize"]);
			$("#user-account-change-password-btn").click(mv.changePassword);
		}
	}

	mollify.view.config.admin.UsersView = function() {
		var that = this;
		this.title = mollify.ui.texts.get("configAdminUsersNavTitle");

		this.onActivate = function($c) {
			$c.html("users");
		}
	}
}(window.jQuery, window.mollify);
