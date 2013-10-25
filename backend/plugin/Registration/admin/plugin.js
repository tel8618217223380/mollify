/**
 * plugin.js
 *
 * Copyright 2008- Samuli Järvelä
 * Released under GPL License.
 *
 * License: http://www.mollify.org/license.php
 */
	 
!function($, mollify) {

	"use strict"; // jshint ;_;

	mollify.view.config.admin.Registration = {
		PendingRegistrationsView : function() {
			var that = this;

			this.init = function() {
				that.title = mollify.ui.texts.get("pluginRegistrationAdminNavTitle");
				that._timestampFormatter = new mollify.ui.formatters.Timestamp(mollify.ui.texts.get('shortDateTimeFormat'));
			}

			this.onActivate = function($c) {
				var list = false;
				var listView = false;
							
				var updateList = function() {
					$c.addClass("loading");
					mollify.service.get("registration/list/").done(function(l) {
						$c.removeClass("loading");
						list = l;
						listView.table.set(list);
					});
				};
	
				listView = new mollify.view.ConfigListView($c, {
					actions: [
						{ id: "action-add", content:'<i class="icon-plus"></i>', callback: function() { that.onAddRegistration(updateList); }},
						{ id: "action-remove", content:'<i class="icon-trash"></i>', cls:"btn-danger", depends: "table-selection", callback: function(sel) { mollify.service.del("registration/list/", { ids: mollify.helpers.extractValue(sel, "id") }).done(updateList); }},
						{ id: "action-refresh", content:'<i class="icon-refresh"></i>', callback: updateList }
					],
					table: {
						id: "config-admin-registrations",
						key: "id",
						narrow: true,
						columns: [
							{ type:"selectrow" },
							{ id: "icon", title:"", type:"static", content: '<i class="icon-pencil"></i>' },
							{ id: "name", title: mollify.ui.texts.get('pluginRegistrationAdminNameTitle') },
							{ id: "email", title: mollify.ui.texts.get('pluginRegistrationAdminEmailTitle') },
							{ id: "key", title: mollify.ui.texts.get('pluginRegistrationAdminKeyTitle') },
							{ id: "time", title: mollify.ui.texts.get('pluginRegistrationAdminTimeTitle'), formatter: that._timestampFormatter },
							{ id: "confirm", title: mollify.ui.texts.get('pluginRegistrationAdminConfirmTitle'), type: "action", content: '<i class="icon-ok"></i>' },
							{ id: "remove", title: mollify.ui.texts.get('configAdminActionRemoveTitle'), type: "action", content: '<i class="icon-trash"></i>' }
						],
						onRowAction: function(id, r) {
							if (id == "remove") {
								mollify.service.del("registration/list/"+r.id).done(updateList);
							} else if (id == "confirm") {
								mollify.service.post("registration/confirm/"+r.id).done(updateList);
							}
						}
					}
				});
				updateList();
			};
			
			this.onAddRegistration = function(cb) {
				mollify.templates.load("plugin-registration-content", mollify.helpers.noncachedUrl(mollify.plugins.adminUrl("Registration", "content.html")), function() {
					var $content = false;
					var $name = false;
					var $email = false;
					var $password = false;
								
					mollify.ui.dialogs.custom({
						resizable: true,
						initSize: [600, 400],
						title: mollify.ui.texts.get('pluginRegistrationAdminAddRegistrationTitle'),
						content: mollify.dom.template("mollify-tmpl-registration-add"),
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
							var password = $password.val();
							if (!username || username.length === 0 || !password || password.length === 0) return;
							
							mollify.service.post("registration/create", {name:username, password:window.Base64.encode(password), email:email}).done(d.close).done(cb);
						},
						"on-show": function(h, $d) {
							$content = $d.find("#mollify-registration-add-dialog");
							$name = $d.find("#usernameField");
							$email = $d.find("#emailField");
							$password = $d.find("#passwordField");
							$("#generatePasswordBtn").click(function(){ $password.val(that._generatePassword()); return false; });
							
							$name.focus();
							h.center();
						}
					});
				});
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
		}
	}

	mollify.admin.plugins.Registration = {
		resources : {
			texts: true
		},
		views: [
			new mollify.view.config.admin.Registration.PendingRegistrationsView()
		]
	};
}(window.jQuery, window.mollify);
