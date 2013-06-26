!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.config.admin.Notificator = {
		NotificationsView : function() {
			var that = this;

			this.init = function() {
				that.title = mollify.ui.texts.get("pluginNotificatorAdminNavTitle");
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
				var list = false;
				var listView = false;
				that._details = mollify.ui.controls.slidePanel($("#mollify-mainview-viewcontent"));
							
				var updateList = function() {
					$c.addClass("loading");
					mollify.service.get("notificator/list/").done(function(l) {
						$c.removeClass("loading");
						list = l;
						listView.table.set(list);
					});
				};
	
				listView = new mollify.view.ConfigListView($c, {
					actions: [
						{ id: "action-add", content:'<i class="icon-plus"></i>', callback: function() { that.onAddEditNotification(false, updateList); }},
						{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { mollify.service.del("notificator/list/", { ids: mollify.helpers.extractValue(sel, "id") }).done(updateList); }},
						{ id: "action-refresh", content:'<i class="icon-refresh"></i>', callback: updateList }
					],
					table: {
						id: "config-admin-registrations",
						key: "id",
						narrow: true,
						hilight: true,
						columns: [
							{ type:"selectrow" },
							{ id: "icon", title:"", type:"static", content: '<i class="icon-envelope-alt"></i>' },
							{ id: "name", title: mollify.ui.texts.get('pluginNotificatorAdminNameTitle') },
							{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
							{ id: "edit", title: "", type: "action", content: '<i class="icon-edit"></i>' },
							{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
						],
						onRowAction: function(id, n) {
							if (id == "remove") {
								mollify.service.del("notificator/list/"+n.id).done(updateList);
							} else if (id == "edit") {
								that.onAddEditNotification(n, updateList);
							}
						},
						onHilight: function(n) {
							if (n) {
								that._showNotificationDetails(n, that._details.getContentElement().empty(), that._allUsersgroups);
								that._details.show(false, 400);
							} else {
								that._details.hide();
							}
						}
					}
				});
				updateList();
				
				$c.addClass("loading");
				var gp = mollify.service.get("configuration/usersgroups").done(function(g) {
					that._allUsersgroups = g;
				}).done(function(){$c.removeClass("loading");});
			};
			
			this.onDeactivate = function() {
				that._details.remove();
			};
			
			this.onAddEditNotification = function(n, cb) {
				mollify.ui.dialogs.input({
					resizable: true,
					initSize: [600, 400],
					title: mollify.ui.texts.get(n ? 'pluginNotificationAdminEditNotificationTitle' : 'pluginNotificationAdminAddNotificationTitle'),
					message: mollify.ui.texts.get(n ? 'pluginNotificationAdminEditNotificationMessage' : 'pluginNotificationAdminAddNotificationMessage'),
					yesTitle: mollify.ui.texts.get('dialogSave'),
					noTitle: mollify.ui.texts.get('dialogCancel'),
					defaultValue: n ? n.name : "",
					handler: {
						isAcceptable : function(name) {
							if (!name || name.length === 0) return false;
							if (n && name == n.name) return false;
							return true;
						},
						onInput: function(name) {
							if (n)
								mollify.service.put("notificator/list/"+n.id, {name:name}).done(cb);
							else
								mollify.service.post("notificator/list/", {name:name}).done(cb);
						}
					}
				});
			}
			
			this._showNotificationDetails = function(n, $e, allUsersGroups) {
				mollify.templates.load("plugin-notification-content", mollify.helpers.noncachedUrl(mollify.plugins.adminUrl("Notificator", "content.html")), function() {
					mollify.dom.template("mollify-tmpl-plugin-notificator-notificationdetails", {notification: n}).appendTo($e);
					mollify.ui.process($e, ["localize"]);
					var $events = $e.find(".mollify-notificator-notificationdetails-events");
					var $usersgroups = $e.find(".mollify-notificator-notificationdetails-usersgroups");
					var eventsView = false;
					var usersgroupsView = false;
					var events = false;
					var usersgroups = false;
					
					/*var updateGroups = function() {
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
					var onAddUserGroups = function() {
						var currentIds = mollify.helpers.extractValue(groups, "id");
						var selectable = mollify.helpers.filter(allGroups, function(f) { return currentIds.indexOf(f.id) < 0; });
						if (selectable.length === 0) return;
		
						mollify.ui.dialogs.select({
							title: mollify.ui.texts.get('configAdminUserAddGroupTitle'),
							message: mollify.ui.texts.get('configAdminUserAddGroupMessage'),
							key: "id",
							initSize: [600, 400],
							columns: [
								{ id: "icon", title:"", type:"static", content: '<i class="icon-folder"></i>' },
								{ id: "id", title: mollify.ui.texts.get('configAdminTableIdTitle') },
								{ id: "name", title: mollify.ui.texts.get('configAdminUsersGroupNameTitle') },
								{ id: "description", title: mollify.ui.texts.get('configAdminGroupsDescriptionTitle') },
							],
							list: selectable,
							onSelect: function(sel, o) {
								mollify.service.post("configuration/users/"+u.id+"/groups/", mollify.helpers.extractValue(sel, "id")).done(updateGroups);
							}
						});
					}
		
					foldersView = new mollify.view.ConfigListView($e.find(".mollify-config-admin-userdetails-folders"), {
						title: mollify.ui.texts.get('configAdminUsersFoldersTitle'),
						actions: [
							{ id: "action-add", content:'<i class="icon-plus"></i>', callback: onAddUserFolders },
							{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) {
								mollify.service.del("configuration/users/"+u.id+"/folders/", { ids: mollify.helpers.extractValue(sel, "id") }).done(updateFolders);
							}}
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
									mollify.service.del("configuration/users/"+u.id+"/folders/"+f.id).done(updateGroups);
								}
							}
						}
					});
		
					groupsView = new mollify.view.ConfigListView($e.find(".mollify-config-admin-userdetails-groups"), {
						title: mollify.ui.texts.get('configAdminUsersGroupsTitle'),
						actions: [
							{ id: "action-add", content:'<i class="icon-plus"></i>', callback: onAddUserGroups },
							{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) {
								mollify.service.del("configuration/users/"+u.id+"/groups/", { ids: mollify.helpers.extractValue(sel, "id") }).done(updateGroups);
							}}
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
									mollify.service.del("configuration/users/"+u.id+"/groups/"+g.id).done(updateGroups);
								}
							}
						}
					});
					
					updateGroups();
					updateFolders();*/
				});
			}

		}
	}

	mollify.admin.plugins.Notificator = {
		hasTexts : true,
		views: [
			new mollify.view.config.admin.Notificator.NotificationsView()
		]
	};
}(window.jQuery, window.mollify);
