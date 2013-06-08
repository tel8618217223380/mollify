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
					that._loadAdminPlugins(plugins).done(function(){
						that._initAdminViews(h);
					});
					this._adminViewsLoaded = true;
				}
			}
		}

		this._loadAdminPlugins = function(ids) {
			var df = $.Deferred();
			if (ids.length == 0) return df.resolve();

			var l = [];
			for (var i=0,j=ids.length;i<j;i++) {
				l.push($.getScript("backend/plugin/"+ids[i]+"/admin/plugin.js"));
			}
			
			$.when.apply($, l).done(function() {
				var o = [];

				for (var pk in mollify.admin.plugins) {
					var p = mollify.admin.plugins[pk];
					if (!p || !p.views) continue;

					if (p.hasTexts) o.push($.getScript("backend/plugin/"+pk+"/admin/texts_"+mollify.ui.texts.locale+".js"));
					$.each(p.views, function(i, v) {
						that._adminViews.push(v);
					});
				};

				$.when.apply($, o).done(function() {
					$.each(that._adminViews, function(i, v) {
						if (v.init) v.init();
					})
				}).done(df.resolve);
			});
			return df;
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

		this._activateView = function(v, admin) {
			if (admin) that._adminNav.setActive(v);
			else that._userNav.setActive(v);

			$("#mollify-configview-header").html(v.title);
			v.onActivate($("#mollify-configview-content").empty());
		}

		this.onDeactivate = function() {}
	}

	mollify.view.ConfigListView = function($e, o) {
		mollify.dom.template("mollify-tmpl-configlistview", {actions: o.actions || false}).appendTo($e);
		var $table = $e.find(".mollify-configview-table");
		var table = mollify.ui.controls.table($table, o.table);
		if (o.actions) {
			$e.find(".mollify-configview-actions > .mollify-configlistview-action").click(function() {
				if ($(this).hasClass("disabled")) return;
				var action = $(this).tmplItem().data;				
				if (action.callback) action.callback();
			});
		}

		return {
			table: table,
			enableAction: function(id, e) {
				if (e)
					$e.find("#mollify-configlistview-action-"+id).removeClass("disabled");
				else
					$e.find("#mollify-configlistview-action-"+id).addClass("disabled");
			}
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

		that.permissionOptions = [
			{ title: mollify.ui.texts.get('configAdminUsersPermissionModeAdmin'), value: "a"},
			{ title: mollify.ui.texts.get('pluginPermissionsValueRW'), value: "rw"},
			{ title: mollify.ui.texts.get('pluginPermissionsValueRO'), value: "ro"},
			{ title: mollify.ui.texts.get('pluginPermissionsValueN'), value: "n"}
		];
		that.permissionOptionsByKey = mollify.helpers.mapByKey(that.permissionOptions, "value");

		this.onActivate = function($c) {
			var users = false;
			var listView = false;

			var updateUsers = function() {
				$c.addClass("loading");
				mollify.service.get("configuration/users/").done(function(l) {
					$c.removeClass("loading");
					users = l;
					listView.table.set(users);
				});
			}
			var updateActions = function() {
				var sel = (listView.table.getSelected().length > 0);
				listView.enableAction("action-remove", sel);
			};
			listView = new mollify.view.ConfigListView($c, {
				actions: [
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: function() { alert("foo"); }},
					{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", callback: function() { alert("bar"); }}
				],
				table: {
					key: "id",
					columns: [
						{ type:"select" },
						{ id: "icon", title:"", type:"static", content: '<i class="icon-user"></i>' },
						{ id: "name", title: mollify.ui.texts.get('configAdminUsersNameTitle') },
						{ id: "permission_mode", title: mollify.ui.texts.get('configAdminUsersPermissionTitle'), valueMapper: function(item, pk) {
							var pkl = pk.toLowerCase();
							return that.permissionOptionsByKey[pkl] ? that.permissionOptionsByKey[pkl].title : pk;
						} },
						{ id: "edit", title: "", type: "action", content: '<i class="icon-edit"></i>' },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRowAction: function(id, item) {
						if (id == "edit") {
							//that.onOpenShares(item);
						} else if (id == "remove") {
							//that.removeAllItemShares(item).done(updateShares);
						}
					},
					onSelectionChanged: updateActions
				}
			});
			updateUsers();
			updateActions();
		}
	}
}(window.jQuery, window.mollify);
