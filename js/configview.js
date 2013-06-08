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
					this._adminViewsLoaded = true;
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
				
				o.push(mollify.service.get("configuration/options").done(function(opt) { that._options = opt; }));

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
						if (v.init) v.init(that._options);
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
		
		this.init = function(opt) {
			this.title = mollify.ui.texts.get("configAdminUsersNavTitle");
	
			that.permissionOptions = [
				{ title: mollify.ui.texts.get('configAdminUsersPermissionModeAdmin'), value: "a"},
				{ title: mollify.ui.texts.get('pluginPermissionsValueRW'), value: "rw"},
				{ title: mollify.ui.texts.get('pluginPermissionsValueRO'), value: "ro"},
				{ title: mollify.ui.texts.get('pluginPermissionsValueN'), value: "n"}
			];
			that.permissionOptionsByKey = mollify.helpers.mapByKey(that.permissionOptions, "value");
	
			that.authenticationOptions = [];
			$.each(opt.authentication_methods, function(i, am) { that.authenticationOptions.push({ title: am, value: am }); });
			that.authenticationOptionsByKey = mollify.helpers.mapByKey(that.authenticationOptions, "value");
			that.defaultAuthMethod = opt.authentication_methods[0];
		}
		
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
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: function() { that.onAddEditUser(false, updateUsers); }},
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
					onRowAction: function(id, u) {
						if (id == "edit") {
							that.onAddEditUser(u, updateUsers);
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
		
		this._generatePassword = function() {
			var length = 8;
			var password = '';
			
		    for (i = 0; i < length; i++) {
		    	while (true) {
			        c = (parseInt(Math.random() * 1000) % 94) + 33;
			        if (that._isValidPasswordChar(c)) break;
				}
		        password += String.fromCharCode(c);
		    }
		    return password;
		}
		
		this._isValidPasswordChar = function(c) {
		    if (c >= 33 && c <= 47) return false;
		    if (c >= 58 && c <= 64) return false;
		    if (c >= 91 && c <= 96) return false;
		    if (c >= 123 && c <=126) return false;
		    return true;
		}
		
		this.onAddEditUser = function(u, cb) {
			var $content = false;
			var $name = false;
			var $email = false;
			var $password = false;
			var $permission = false;
			var $authentication = false;
			var $expiration = false;
			
			mollify.ui.dialogs.custom({
				resizable: true,
				initSize: [600, 400],
				title: mollify.ui.texts.get(u ? 'configAdminUsersUserDialogEditTitle' : 'configAdminUsersUserDialogAddTitle'),
				content: mollify.dom.template("mollify-tmpl-config-admin-userdialog", {user: u}),
				buttons: [
					{ id: "yes", "title": mollify.ui.texts.get('dialogSave') },
					{ id: "no", "title": mollify.ui.texts.get('dialogCancel') }
				],
				"on-button": function(btn, d) {
					if (btn.id == 'no') {
						d.close();
						return;
					}
					var username = $username.val();
					var password = $password.val();
					var permissionMode = $permission.selected();
					var expiration = $expiration.get();
					
					if (!username || username.length == 0 || !password || password.length == 0) return;
					
					var user = { username: username, password: password, permissionMode : permissionMode };
					mollify.service.post("configuration/users", user).done(d.close).done(cb).fail(d.close);
				},
				"on-show": function(h, $d) {
					$content = $d.find("#mollify-config-admin-userdialog-content");
					$name = $d.find("#usernameField");
					$email = $d.find("#usernameField");
					$password = $d.find("#passwordField");
					$("#generatePasswordBtn").click(function(){ $password.val(that._generatePassword()); return false; });
					$permission = mollify.ui.controls.select("permissionModeField", {
						values: that.permissionOptions,
						title : "title"
					});
					$authentication = mollify.ui.controls.select("authenticationField", {
						values: that.authenticationOptions,
						none: {title: mollify.ui.texts.get('configAdminUsersUserDialogAuthDefault') + " (" + that.authenticationOptionsByKey[that.defaultAuthMethod].title + ")"},
						title : "title"
					});
					$expiration = mollify.ui.controls.datepicker("expirationField", {
						format: mollify.ui.texts.get('shortDateTimeFormat'),
						time: true
					});
					
					if (u) {
						$name.val(u.name);
						$email.val(u.email || "");
						$permission.select(that.permissionOptionsByKey[u.permission_mode]);
					} else {
						$permission.select(that.permissionOptionsByKey["n"]);	
					}
					$name.focus();

					h.center();
				}
			});
		}
	}
}(window.jQuery, window.mollify);
