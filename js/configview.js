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

		this.onResize = function() {
			$("#mollify-configview").height($("#mollify-mainview-content").height());
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

				that.onResize();

				if (mollify.session.admin) {
					if (that._adminViewsLoaded) {
						that._initAdminViews(h);
					} else {
						that._adminViewsLoaded = true;
						
						// default admin views
						that._adminViews.push(new mollify.view.config.admin.FoldersView());
						that._adminViews.push(new mollify.view.config.admin.UsersView());
						that._adminViews.push(new mollify.view.config.admin.GroupsView());
						
						var plugins = [];
						for (var k in mollify.session.plugins) {
							if (!mollify.session.plugins[k] || !mollify.session.plugins[k].admin) continue;
							plugins.push(k);
						}
						mollify.admin = {
							plugins : []
						};
						that._loadAdminPlugins(plugins).done(function(){
							that._initAdminViews(h);
						});
					}
				}
			});
		}

		this._loadAdminPlugins = function(ids) {
			var df = $.Deferred();
			if (ids.length === 0) return df.resolve();

			var l = [];
			for (var i=0,j=ids.length;i<j;i++) {
				l.push($.getScript("backend/plugin/"+ids[i]+"/admin/plugin.js"));
			}
			
			$.when.apply($, l).done(function() {
				var o = [];
				
				o.push(mollify.service.get("configuration/options").done(function(opt) { that._options = opt; }));

				var addView = function(i, v) {
					that._adminViews.push(v);
				};
				for (var pk in mollify.admin.plugins) {
					var p = mollify.admin.plugins[pk];
					if (!p || !p.views) continue;

					if (p.hasTexts) o.push($.getScript("backend/plugin/"+pk+"/admin/texts_"+mollify.ui.texts.locale+".js"));
					$.each(p.views, addView);
				}

				$.when.apply($, o).done(function() {
					$.each(that._adminViews, function(i, v) {
						if (v.init) v.init(that._options);
					})
				}).done(df.resolve);
			});
			return df;
		}

		this._initAdminViews = function(h) {
			if (!mollify.session.admin || that._adminViews.length === 0) return;

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
			if (that._activeView && that._activeView.onDeactivate) that._activeView.onDeactivate();
			if (admin) that._adminNav.setActive(v);
			else that._userNav.setActive(v);

			that._activeView = v;
			$("#mollify-configview-header").html(v.title);
			v.onActivate($("#mollify-configview-content").empty());
		}

		this.onDeactivate = function() {
			if (that._activeView && that._activeView.onDeactivate) that._activeView.onDeactivate();
		}
	}

	mollify.view.ConfigListView = function($e, o) {
		mollify.dom.template("mollify-tmpl-configlistview", {title: o.title, actions: o.actions || false}).appendTo($e);
		var $table = $e.find(".mollify-configlistview-table");
		var table = mollify.ui.controls.table($table, o.table);
		var enableAction = function(id, e) {
			if (e)
				$e.find("#mollify-configlistview-action-"+id).removeClass("disabled");
			else
				$e.find("#mollify-configlistview-action-"+id).addClass("disabled");
		};
		if (o.actions) {
			table.onSelectionChanged(function() {
				var sel = table.getSelected();
				var any = sel.length > 0;
				var one = sel.length == 1;
				var many = sel.length > 1;
				$.each(o.actions, function(i, a) {
					if (!a.depends) return;
					if (a.depends == "table-selection") enableAction(a.id, any);
					else if (a.depends == "table-selection-one") enableAction(a.id, one);
					else if (a.depends == "table-selection-many") enableAction(a.id, many);
				});
			});
			$e.find(".mollify-configlistview-actions > .mollify-configlistview-action").click(function() {
				if ($(this).hasClass("disabled")) return;
				var action = $(this).tmplItem().data;
				if (!action.callback) return;
				
				var p;
				if (action.depends && action.depends.startsWith("table-selection")) p = table.getSelected();
				action.callback(p);
			});
		}

		return {
			table: table,
			enableAction: enableAction
		};
	}

	mollify.view.config = {
		user: {},
		admin: {}
	};

	/* Account */
	mollify.view.config.user.AccountView = function(mv) {
		var that = this;
		this.title = mollify.ui.texts.get("configUserAccountNavTitle");

		this.onActivate = function($c) {
			mollify.dom.template("mollify-tmpl-config-useraccountview", mollify.session).appendTo($c);
			mollify.ui.process($c, ["localize"]);
			$("#user-account-change-password-btn").click(mv.changePassword);
		}
	}

	/* Users */
	mollify.view.config.admin.UsersView = function() {
		var that = this;
		
		this.init = function(opt) {
			this.title = mollify.ui.texts.get("configAdminUsersNavTitle");
	
			that._permissionOptions = ["a", "rw", "ro", "no"];
			that._permissionTexts = {
				"a" : mollify.ui.texts.get('configAdminUsersPermissionModeAdmin'),
				"rw" : mollify.ui.texts.get('pluginPermissionsValueRW'),
				"ro" : mollify.ui.texts.get('pluginPermissionsValueRO'),
				"no" : mollify.ui.texts.get('pluginPermissionsValueNO')
			};
	
			that._authenticationOptions = opt.authentication_methods;
			that._authFormatter = function(am) { return am; /* TODO */ }
			that._defaultAuthMethod = opt.authentication_methods[0];
		}
		
		this.onActivate = function($c) {
			var users = false;
			var listView = false;
			that._details = mollify.ui.controls.slidePanel($("#mollify-mainview-viewcontent"));

			var updateUsers = function() {
				that._details.hide();
				$c.addClass("loading");
				mollify.service.get("configuration/users/").done(function(l) {
					$c.removeClass("loading");
					users = l;
					listView.table.set(users);
				});
			};
			
			listView = new mollify.view.ConfigListView($c, {
				actions: [
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: function() { that.onAddEditUser(false, updateUsers); }},
					{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { that._removeUsers(sel).done(updateUsers); }},
					{ id: "action-refresh", content:'<i class="icon-refresh"></i>', callback: updateUsers }
				],
				table: {
					id: "config-admin-users",
					key: "id",
					narrow: true,
					hilight: true,
					columns: [
						{ type:"selectrow" },
						{ id: "icon", title:"", type:"static", content: '<i class="icon-user"></i>' },
						{ id: "name", title: mollify.ui.texts.get('configAdminUsersNameTitle') },
						{ id: "permission_mode", title: mollify.ui.texts.get('configAdminUsersPermissionTitle'), valueMapper: function(item, pk) {
							var pkl = pk.toLowerCase();
							return that._permissionTexts[pkl] ? that._permissionTexts[pkl] : pk;
						} },
						{ id: "email", title: mollify.ui.texts.get('configAdminUsersEmailTitle') },
						{ id: "edit", title: "", type: "action", content: '<i class="icon-edit"></i>' },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRowAction: function(id, u) {
						if (id == "edit") {
							that.onAddEditUser(u, updateUsers);
						} else if (id == "remove") {
							mollify.service.del("configuration/users/"+u.id).done(updateUsers);
						}
					},
					onHilight: function(u) {
						if (u) {
							that._showUserDetails(u, that._details.getContentElement().empty(), that._allGroups, that._allFolders);
							that._details.show(false, 400);
						} else {
							that._details.hide();
						}
					}
				}
			});
			updateUsers();

			$c.addClass("loading");
			var gp = mollify.service.get("configuration/usergroups").done(function(g) {
				that._allGroups = g;
			});
			var fp = mollify.service.get("configuration/folders").done(function(f) {
				that._allFolders = f;
			});
			$.when(gp, fp).done(function(){$c.removeClass("loading");});
		}
		
		this.onDeactivate = function() {
			that._details.remove();
		};

		this._showUserDetails = function(u, $e, allGroups, allFolders) {
			mollify.dom.template("mollify-tmpl-config-admin-userdetails", {user: u}).appendTo($e);
			mollify.ui.process($e, ["localize"]);
			var $groups = $e.find(".mollify-config-admin-userdetails-groups");
			var $folders = $e.find(".mollify-config-admin-userdetails-folders");
			var foldersView = false;
			var groupsView = false;
			var folders = false;
			var groups = false;
			
			var updateGroups = function() {
				$groups.addClass("loading");
				mollify.service.get("configuration/users/"+u.id+"/groups/").done(function(l) {
					$groups.removeClass("loading");
					groups = l;
					groupsView.table.set(groups);
				});
			};
			var updateFolders = function() {
				$folders.addClass("loading");
				mollify.service.get("configuration/users/"+u.id+"/folders/").done(function(l) {
					$folders.removeClass("loading");
					folders = l;
					foldersView.table.set(folders);
				});
			};
			var onAddUserFolders = function() {
				var currentIds = mollify.helpers.extractValue(folders, "id");
				var selectable = mollify.helpers.filter(allFolders, function(f) { return currentIds.indexOf(f.id) < 0; });
				if (selectable.length === 0) return;

				mollify.ui.dialogs.select({
					title: mollify.ui.texts.get('configAdminUserAddFolderTitle'),
					message: mollify.ui.texts.get('configAdminUserAddFolderMessage'),
					key: "id",
					initSize: [600, 400],
					columns: [
						{ id: "icon", title:"", type:"static", content: '<i class="icon-folder"></i>' },
						{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
						{ id: "name", title: mollify.ui.texts.get('configAdminUsersFolderDefaultNameTitle') },
						{ id: "user_name", title: mollify.ui.texts.get('configAdminUsersFolderNameTitle'), type:"input" },
						{ id: "path", title: mollify.ui.texts.get('configAdminFoldersPathTitle') }
					],
					list: selectable,
					onSelect: function(sel, o) {
						var folders = [];
						$.each(sel, function(i, f) {
							var folder = {id: f.id};
							var name = o[f.id] ? o[f.id].user_name : false;
							if (name && f.name != name)
									folder.name = name;
							folders.push(folder);
						});
						mollify.service.post("configuration/users/"+u.id+"/folders/", folders).done(updateFolders);
					}
				});
			}

			foldersView = new mollify.view.ConfigListView($e.find(".mollify-config-admin-userdetails-folders"), {
				title: mollify.ui.texts.get('configAdminUsersFoldersTitle'),
				actions: [
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: onAddUserFolders },
					{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { }}
				],
				table: {
					id: "config-admin-userfolders",
					key: "id",
					narrow: true,
					columns: [
						{ type:"selectrow" },
						{ id: "icon", title:"", type:"static", content: '<i class="icon-folder"></i>' },
						{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
						{ id: "name", title: mollify.ui.texts.get('configAdminUsersFolderNameTitle'), valueMapper: function(f, v) {
							var n = f.name;
							if (n && n.length > 0) return n;
							return mollify.ui.texts.get('configAdminUsersFolderDefaultName', f.default_name);
						} },
						{ id: "path", title: mollify.ui.texts.get('configAdminFoldersPathTitle') },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRowAction: function(id, f) {
						if (id == "remove") {
							mollify.service.del("configuration/users/"+u.id+"/folders/", {id: f.id}).done(updateGroups);
						}
					}
				}
			});

			groupsView = new mollify.view.ConfigListView($e.find(".mollify-config-admin-userdetails-groups"), {
				title: mollify.ui.texts.get('configAdminUsersGroupsTitle'),
				actions: [
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: function() {  }},
					{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { }}
				],
				table: {
					id: "config-admin-usergroups",
					key: "id",
					narrow: true,
					columns: [
						{ type:"selectrow" },
						{ id: "icon", title:"", type:"static", content: '<i class="icon-user"></i>' },
						{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
						{ id: "name", title: mollify.ui.texts.get('configAdminUsersGroupNameTitle') },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRowAction: function(id, g) {
						if (id == "remove") {
							mollify.service.del("configuration/users/"+u.id+"/groups/", {id:g.id}).done(updateGroups);
						}
					}
				}
			});
			
			updateGroups();
			updateFolders();
		}
		
		this._generatePassword = function() {
			var length = 8;
			var password = '';
			var c;
			
			for (var i = 0; i < length; i++) {
				while (true) {
					c = (parseInt(Math.random() * 1000, 10) % 94) + 33;
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
		
		this._removeUsers = function(users) {
			return mollify.service.del("configuration/users", {ids: mollify.helpers.extractValue(users, "id")});
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
					var username = $name.val();
					var email = $email.val();
					var permissionMode = $permission.selected();
					var expiration = mollify.helpers.formatInternalTime($expiration.get());
					var auth = $authentication.selected();
					if (!username || username.length === 0) return;
					
					var user = { name: username, email: email, permission_mode : permissionMode, expiration: expiration, auth: auth };
					
					if (u) {	
						mollify.service.put("configuration/users/"+u.id, user).done(d.close).done(cb);
					} else {
						var password = $password.val();
						if (!password || password.length === 0) return;
						
						user.password = window.Base64.encode(password);
						mollify.service.post("configuration/users", user).done(d.close).done(cb);
					}
				},
				"on-show": function(h, $d) {
					$content = $d.find("#mollify-config-admin-userdialog-content");
					$name = $d.find("#usernameField");
					$email = $d.find("#emailField");
					$password = $d.find("#passwordField");
					$("#generatePasswordBtn").click(function(){ $password.val(that._generatePassword()); return false; });
					$permission = mollify.ui.controls.select("permissionModeField", {
						values: that._permissionOptions,
						valueMapper : function(p) {
							return that._permissionTexts[p];
						}
					});
					$authentication = mollify.ui.controls.select("authenticationField", {
						values: that._authenticationOptions,
						none: mollify.ui.texts.get('configAdminUsersUserDialogAuthDefault', that._defaultAuthMethod),
						valueMapper: that._authFormatter
					});
					$expiration = mollify.ui.controls.datepicker("expirationField", {
						format: mollify.ui.texts.get('shortDateTimeFormat'),
						time: true
					});
					
					if (u) {
						$name.val(u.name);
						$email.val(u.email || "");
						$permission.select(u.permission_mode.toLowerCase());
						$authentication.select(u.auth ? u.auth.toLowerCase() : null);
						$expiration.set(mollify.helpers.parseInternalTime(u.expiration));
					} else {
						$permission.select("no");	
					}
					$name.focus();

					h.center();
				}
			});
		}
	}

	/* Groups */
	mollify.view.config.admin.GroupsView = function() {
		var that = this;
		
		this.init = function(opt) {
			this.title = mollify.ui.texts.get("configAdminGroupsNavTitle");	
		}
		
		this.onActivate = function($c) {
			var groups = false;
			var listView = false;
			that._details = mollify.ui.controls.slidePanel($("#mollify-mainview-viewcontent"));

			var updateGroups = function() {
				that._details.hide();
				$c.addClass("loading");
				mollify.service.get("configuration/usergroups/").done(function(l) {
					$c.removeClass("loading");
					groups = l;
					listView.table.set(groups);
				});
			};
			
			listView = new mollify.view.ConfigListView($c, {
				actions: [
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: function() { that.onAddEditGroup(false, updateGroups); }},
					{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { that._removeGroups(sel).done(updateGroups); }},
					{ id: "action-refresh", content:'<i class="icon-refresh"></i>', callback: updateGroups }
				],
				table: {
					id: "config-admin-groups",
					key: "id",
					narrow: true,
					hilight: true,
					columns: [
						{ type:"selectrow" },
						{ id: "icon", title:"", type:"static", content: '<i class="icon-user"></i>' },
						{ id: "name", title: mollify.ui.texts.get('configAdminUsersNameTitle') },
						{ id: "description", title: mollify.ui.texts.get('configAdminGroupsDescriptionTitle') },
						{ id: "edit", title: "", type: "action", content: '<i class="icon-edit"></i>' },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRowAction: function(id, g) {
						if (id == "edit") {
							that.onAddEditGroups(g, updateGroups);
						} else if (id == "remove") {
							mollify.service.del("configuration/usergroups/"+g.id).done(updateGroups);
						}
					},
					onHilight: function(u) {
						if (u) {
							that._showGroupDetails(u, that._details.getContentElement().empty(), that._allUsers, that._allFolders);
							that._details.show(false, 400);
						} else {
							that._details.hide();
						}
					}
				}
			});
			updateGroups();

			$c.addClass("loading");
			var up = mollify.service.get("configuration/users").done(function(u) {
				that._allUsers = u;
			});
			var fp = mollify.service.get("configuration/folders").done(function(f) {
				that._allFolders = f;
			});
			$.when(up, fp).done(function(){$c.removeClass("loading");});
		}
		
		this.onDeactivate = function() {
			that._details.remove();
		};

		this._showGroupDetails = function(u, $e, allGroups, allFolders) {
			/*mollify.dom.template("mollify-tmpl-config-admin-userdetails", {user: u}).appendTo($e);
			mollify.ui.process($e, ["localize"]);
			var $groups = $e.find(".mollify-config-admin-userdetails-groups");
			var $folders = $e.find(".mollify-config-admin-userdetails-folders");
			var foldersView = false;
			var groupsView = false;
			var folders = false;
			var groups = false;
			
			var updateGroups = function() {
				$groups.addClass("loading");
				mollify.service.get("configuration/users/"+u.id+"/groups/").done(function(l) {
					$groups.removeClass("loading");
					groups = l;
					groupsView.table.set(groups);
				});
			};
			var updateFolders = function() {
				$folders.addClass("loading");
				mollify.service.get("configuration/users/"+u.id+"/folders/").done(function(l) {
					$folders.removeClass("loading");
					folders = l;
					foldersView.table.set(folders);
				});
			};
			var onAddUserFolders = function() {
				var currentIds = mollify.helpers.extractValue(folders, "id");
				var selectable = mollify.helpers.filter(allFolders, function(f) { return currentIds.indexOf(f.id) < 0; });
				if (selectable.length === 0) return;

				mollify.ui.dialogs.select({
					title: mollify.ui.texts.get('configAdminUserAddFolderTitle'),
					message: mollify.ui.texts.get('configAdminUserAddFolderMessage'),
					key: "id",
					initSize: [600, 400],
					columns: [
						{ id: "icon", title:"", type:"static", content: '<i class="icon-folder"></i>' },
						{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
						{ id: "name", title: mollify.ui.texts.get('configAdminUsersFolderDefaultNameTitle') },
						{ id: "user_name", title: mollify.ui.texts.get('configAdminUsersFolderNameTitle'), type:"input" },
						{ id: "path", title: mollify.ui.texts.get('configAdminFoldersPathTitle') }
					],
					list: selectable,
					onSelect: function(sel, o) {
						var folders = [];
						$.each(sel, function(i, f) {
							var folder = {id: f.id};
							var name = o[f.id] ? o[f.id].user_name : false;
							if (name && f.name != name)
									folder.name = name;
							folders.push(folder);
						});
						mollify.service.post("configuration/users/"+u.id+"/folders/", folders).done(updateFolders);
					}
				});
			}

			foldersView = new mollify.view.ConfigListView($e.find(".mollify-config-admin-userdetails-folders"), {
				title: mollify.ui.texts.get('configAdminUsersFoldersTitle'),
				actions: [
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: onAddUserFolders },
					{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { }}
				],
				table: {
					id: "config-admin-userfolders",
					key: "id",
					narrow: true,
					columns: [
						{ type:"selectrow" },
						{ id: "icon", title:"", type:"static", content: '<i class="icon-folder"></i>' },
						{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
						{ id: "name", title: mollify.ui.texts.get('configAdminUsersFolderNameTitle'), valueMapper: function(f, v) {
							var n = f.name;
							if (n && n.length > 0) return n;
							return mollify.ui.texts.get('configAdminUsersFolderDefaultName', f.default_name);
						} },
						{ id: "path", title: mollify.ui.texts.get('configAdminFoldersPathTitle') },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRowAction: function(id, f) {
						if (id == "remove") {
							mollify.service.del("configuration/users/"+u.id+"/folders/", {id: f.id}).done(updateGroups);
						}
					}
				}
			});

			groupsView = new mollify.view.ConfigListView($e.find(".mollify-config-admin-userdetails-groups"), {
				title: mollify.ui.texts.get('configAdminUsersGroupsTitle'),
				actions: [
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: function() {  }},
					{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { }}
				],
				table: {
					id: "config-admin-usergroups",
					key: "id",
					narrow: true,
					columns: [
						{ type:"selectrow" },
						{ id: "icon", title:"", type:"static", content: '<i class="icon-user"></i>' },
						{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
						{ id: "name", title: mollify.ui.texts.get('configAdminUsersGroupNameTitle') },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRowAction: function(id, g) {
						if (id == "remove") {
							mollify.service.del("configuration/users/"+u.id+"/groups/", {id:g.id}).done(updateGroups);
						}
					}
				}
			});
			
			updateGroups();
			updateFolders();*/
		}
		
		this._removeGroups = function(groups) {
			return mollify.service.del("configuration/usergroups", {ids: mollify.helpers.extractValue(groups, "id")});
		}
		
		this.onAddEditGroup = function(g, cb) {
			/*var $content = false;
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
					var username = $name.val();
					var permissionMode = $permission.selected();
					var expiration = mollify.helpers.formatInternalTime($expiration.get());
					var auth = $authentication.selected();
					if (!username || username.length == 0) return;
					
					var user = { name: username, permission_mode : permissionMode, expiration: expiration, auth: auth };
					
					if (u) {	
						mollify.service.put("configuration/users/"+u.id, user).done(d.close).done(cb);
					} else {
						var password = $password.val();
						if (!password || password.length == 0) return;
						
						user.password = window.Base64.encode(password);
						mollify.service.post("configuration/users", user).done(d.close).done(cb);
					}
				},
				"on-show": function(h, $d) {
					$content = $d.find("#mollify-config-admin-userdialog-content");
					$name = $d.find("#usernameField");
					$email = $d.find("#emailField");
					$password = $d.find("#passwordField");
					$("#generatePasswordBtn").click(function(){ $password.val(that._generatePassword()); return false; });
					$permission = mollify.ui.controls.select("permissionModeField", {
						values: that._permissionOptions,
						valueMapper : function(p) {
							return that._permissionTexts[p];
						}
					});
					$authentication = mollify.ui.controls.select("authenticationField", {
						values: that._authenticationOptions,
						none: mollify.ui.texts.get('configAdminUsersUserDialogAuthDefault', that._defaultAuthMethod),
						valueMapper: that._authFormatter
					});
					$expiration = mollify.ui.controls.datepicker("expirationField", {
						format: mollify.ui.texts.get('shortDateTimeFormat'),
						time: true
					});
					
					if (u) {
						$name.val(u.name);
						$email.val(u.email || "");
						$permission.select(u.permission_mode.toLowerCase());
						$authentication.select(u.auth ? u.auth.toLowerCase() : null);
					} else {
						$permission.select("no");	
					}
					$name.focus();

					h.center();
				}
			});*/
		}
	}

	/* Folders */
	mollify.view.config.admin.FoldersView = function() {
		var that = this;
		
		this.init = function(opt) {
			that.title = mollify.ui.texts.get("configAdminFoldersNavTitle");
		}
		
		this.onActivate = function($c) {
			var folders = false;
			var listView = false;
			that._details = mollify.ui.controls.slidePanel($("#mollify-mainview-viewcontent"));

			var updateFolders = function() {
				$c.addClass("loading");
				mollify.service.get("configuration/folders/").done(function(l) {
					$c.removeClass("loading");
					folders = l;
					listView.table.set(folders);
				});
			};

			listView = new mollify.view.ConfigListView($c, {
				actions: [
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: function() { that.onAddEditFolder(false, updateFolders); }},
					{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { that._removeFolders(sel).done(updateFolders); }},
					{ id: "action-refresh", content:'<i class="icon-refresh"></i>', callback: updateFolders }
				],
				table: {
					id: "config-admin-folders",
					key: "id",
					narrow: true,
					hilight: true,
					columns: [
						{ type:"selectrow" },
						{ id: "icon", title:"", type:"static", content: '<i class="icon-folder-close"></i>' },
						{ id: "name", title: mollify.ui.texts.get('configAdminFoldersNameTitle') },
						{ id: "path", title: mollify.ui.texts.get('configAdminFoldersPathTitle') },
						{ id: "edit", title: "", type: "action", content: '<i class="icon-edit"></i>' },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRowAction: function(id, f) {
						if (id == "edit") {
							that.onAddEditFolder(f, updateFolders);
						} else if (id == "remove") {
							mollify.service.del("configuration/folders/"+f.id).done(updateFolders);
						}
					},
					onHilight: function(f) {
						if (f) {
							that._showFolderDetails(f, that._details.getContentElement().empty(), that._allGroups, that._allUsers);
							that._details.show(false, 400);
						} else {
							that._details.hide();
						}
					}
				}
			});
			updateFolders();
			
			$c.addClass("loading");
			var gp = mollify.service.get("configuration/usersgroups").done(function(r) {
				that._allUsers = r.users;
				that._allGroups = r.groups;
				$c.removeClass("loading");
			});
		}
		
		this.onDeactivate = function() {
			that._details.remove();
		};
		
		this._showFolderDetails = function(f, $e, allUsers, allGroups) {
			mollify.dom.template("mollify-tmpl-config-admin-folderdetails", {folder: f}).appendTo($e);
			mollify.ui.process($e, ["localize"]);
			var $usersAndGroups = $e.find(".mollify-config-admin-folderdetails-usersandgroups");
			var usersAndGroupsView = false;
			var usersAndGroups = false;
			var allUsersAndGroups = allUsers.concat(allGroups);
			
			var updateUsersAndGroups = function() {
				$usersAndGroups.addClass("loading");
				mollify.service.get("configuration/folders/"+f.id+"/users/").done(function(l) {
					$usersAndGroups.removeClass("loading");
					usersAndGroups = l;
					usersAndGroupsView.table.set(l);
				});
			};
			var onAddUserGroup = function() {
				var currentIds = mollify.helpers.extractValue(usersAndGroups, "id");
				var selectable = mollify.helpers.filter(allUsersAndGroups, function(ug) { return currentIds.indexOf(ug.id) < 0; });
				if (selectable.length === 0) return;

				mollify.ui.dialogs.select({
					title: mollify.ui.texts.get('configAdminFolderAddUserTitle'),
					message: mollify.ui.texts.get('configAdminFolderAddUserMessage'),
					key: "id",
					initSize: [600, 400],
					columns: [
						{ id: "icon", title:"", type:"static", content: '<i class="icon-user"></i>' },
						{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
						{ id: "name", title: mollify.ui.texts.get('configAdminUserDialogUsernameTitle') },
						{ id: "is_group", title: mollify.ui.texts.get('configAdminFolderUserTypeTitle') }
					],
					list: selectable,
					onSelect: function(sel, o) {
						mollify.service.post("configuration/folders/"+f.id+"/users/", mollify.helpers.extractValue(sel, "id")).done(updateUsersAndGroups);
					}
				});
			}

			usersAndGroupsView = new mollify.view.ConfigListView($usersAndGroups, {
				title: mollify.ui.texts.get('configAdminFolderUsersTitle'),
				actions: [
					{ id: "action-add", content:'<i class="icon-plus"></i>', callback: onAddUserGroup },
					{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { }}
				],
				table: {
					id: "config-admin-folderusers",
					key: "id",
					narrow: true,
					columns: [
						{ type:"selectrow" },
						{ id: "icon", title:"", type:"static", content: '<i class="icon-user"></i>' },
						{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
						{ id: "name", title: mollify.ui.texts.get('configAdminUserDialogUsernameTitle') },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRowAction: function(id, f) {
						if (id == "remove") {
							//TODO mollify.service.del("configuration/users/"+u.id+"/folders/", {id: f.id}).done(updateGroups);
						}
					}
				}
			});
			
			updateUsersAndGroups();
		}
		
		this._removeFolders = function(f) {
			return mollify.service.del("configuration/folders", {ids: mollify.helpers.extractValue(f, "id")});
		}
		
		this.onAddEditFolder = function(f, cb) {
			var $content = false;
			var $name = false;
			var $path = false;
			
			mollify.ui.dialogs.custom({
				resizable: true,
				initSize: [500, 300],
				title: mollify.ui.texts.get(f ? 'configAdminFoldersFolderDialogEditTitle' : 'configAdminFoldersFolderDialogAddTitle'),
				content: mollify.dom.template("mollify-tmpl-config-admin-folderdialog", {folder: f}),
				buttons: [
					{ id: "yes", "title": mollify.ui.texts.get('dialogSave') },
					{ id: "no", "title": mollify.ui.texts.get('dialogCancel') }
				],
				"on-button": function(btn, d) {
					if (btn.id == 'no') {
						d.close();
						return;
					}
					$content.find(".control-group").removeClass("error");
					var name = $name.val();
					var path = $path.val();
					if (!name) $name.closest(".control-group").addClass("error");
					if (!path) $path.closest(".control-group").addClass("error");
					if (!name || !path) return;
					
					var folder = {name: name, path: path};
					var onFail = function(e){
						if (e.code == 105) {
							this.handled = true;
							
							mollify.ui.dialogs.confirmation({title:mollify.ui.texts.get('configAdminFoldersFolderDialogAddTitle'), message: "TODO Does not exist, create folder?", callback: function() {
								folder.create = true;
								if (!f)
									mollify.service.post("configuration/folders", folder).done(d.close).done(cb);
								else
									mollify.service.put("configuration/folders/"+f.id, folder).done(d.close).done(cb);
							}});
						}
					};
					if (f) {	
						mollify.service.put("configuration/folders/"+f.id, folder).done(d.close).done(cb).fail(onFail);
					} else {
						mollify.service.post("configuration/folders", folder).done(d.close).done(cb).fail(onFail);
					}
				},
				"on-show": function(h, $d) {
					$content = $d.find("#mollify-config-admin-folderdialog-content");
					$name = $d.find("#nameField");
					$path = $d.find("#pathField");
					
					if (f) {
						$name.val(f.name);
						$path.val(f.path);
					}
					$name.focus();

					h.center();
				}
			});
		}
	}

}(window.jQuery, window.mollify);
