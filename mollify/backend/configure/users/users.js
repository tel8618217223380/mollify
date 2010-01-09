/**
	Copyright (c) 2008- Samuli Järvelä

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	this entire header must remain intact.
*/

function MollifyUsersConfigurationView() {
	var that = this;
	
	this.pageUrl = "users/users.html";
	this.users = null;
		
	this.onLoadView = onLoadView;
	
	function onLoadView() {
		loadScript("users/common.js", that.init);
	}
	
	this.init = function() {
		$("#button-add-user").click(openAddUser);
		$("#button-remove-user").click(onRemoveUser);
		$("#button-edit-user").click(onEditUser);
		$("#button-refresh-users").click(that.refresh);
		
		$("#users-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['ID', 'Name','Permission Mode'],
		   	colModel:[
			   	{name:'id',index:'id', width:20, sortable:true, sorttype:"int"},
		   		{name:'name',index:'name', width:200, sortable:true},
				{name:'permission_mode',index:'permission_mode',width:150, sortable:true, formatter:permissionModeFormatter},
		   	],
		   	sortname:'id',
		   	sortorder:'asc',
			onSelectRow: function(id){
				that.onUserSelectionChanged();
			}
		});
		
		that.refresh();
	}
	
	this.refresh = function() {
		getUsers(refreshUsers, onServerError);
	}
	
	function refreshUsers(users) {
		that.users = {};

		var grid = $("#users-list");
		grid.jqGrid('clearGridData');
		
		for(var i=0;i < users.length;i++) {
			var user = users[i];
			that.users[user.id] = user;
			grid.jqGrid('addRowData', user.id, user);
		}
		
		that.onUserSelectionChanged();
	}
	
	this.refreshUserGroups = function() {
		getUsersGroups(that.getSelectedUser(), onRefreshUserGroups, onServerError);
	}
	
	function onRefreshUserGroups(groups) {
		that.userGroups = {};
		
		var grid = $("#user-groups-list");
		grid.jqGrid('clearGridData');

		for (var i=0; i < groups.length; i++) {
			var group = groups[i];
			that.userGroups[group.id] = group;
			grid.jqGrid('addRowData', group.id, group);
		}
				
		//that.onGroupUserSelectionChanged();
	}
	
	this.getSelectedUser = function() {
		return $("#users-list").getGridParam("selrow");
	}
	
	this.getUser = function(id) {
		return that.users[id];
	}
	
	this.onUserSelectionChanged = function() {
		var user = that.getSelectedUser();
		var selected = (user != null);
		if (selected) user = that.getUser(user);
		
		enableButton("button-remove-user", selected);
		enableButton("button-edit-user", selected);
		
		if (that.users.length == 0) {
			$("#user-details-info").html('<div class="message">Click "Add User" to create a new user</div>');
		} else {
			if (selected) {
				$("#user-details-info").html("<h1>User '"+user.name+"'</h1>");
				
				that.refreshUserGroups();
			} else {
				$("#user-details-info").html('<div class="message">Select a user from the list to view details</div>');
			}
		}
		
		if (!selected) {
			$("#user-details-data").hide();
		} else {
			$("#user-details-data").show();
		}
	}
			
	function validateUserData(edit) {
		if (edit) $("#edit-user-dialog > .user-data").removeClass("invalid");
		else $("#user-dialog > .user-data").removeClass("invalid");
	
		var result = true;
		
		if (edit){
			if ($("#edit-username").val().length == 0) {
				$("#edit-user-username").addClass("invalid");
				result = false;
			}		
		} else {
			if ($("#username").val().length == 0) {
				$("#user-username").addClass("invalid");
				result = false;
			}
			if ($("#password").val().length == 0) {
				$("#user-password").addClass("invalid");
				result = false;
			}
		}
		return result;
	}
	
	function openAddUser() {
		$("#add-user-dialog").dialog({
			bgiframe: true,
			height: 300,
			width: 270,
			modal: true,
			resizable: false,
			title: "Add User",
			buttons: {
				Cancel: function() {
					$(this).dialog('close');
				},
				Add: function() {
					if (!validateUserData(false)) return;
					
					var name = $("#username").val();
					var pw = $("#password").val();
					var permission = $("#permission").val();
					
					onSuccess = function() {
						$("#add-user-dialog").dialog('close');
						that.refresh();
					}
					addUser(name, pw, permission, onSuccess, onServerError);
				}
			}
		});
		$("#button-generate-user-password").click(function() {
			$("#password").val(generatePassword());
		});
		
		$("#username").val("");
		$("#password").val("");
		$("#permission").val("ro");
		$("#add-user-dialog").dialog('open');
	}

	function openEditUser(id) {
		var user = that.getUser(id);
		
		$("#edit-user-dialog").dialog({
			bgiframe: true,
			height: 300,
			width: 270,
			modal: true,
			resizable: false,
			title: "Edit User",
			buttons: {
				Cancel: function() {
					$(this).dialog('close');
				},
				Edit: function() {
					if (!validateUserData(true)) return;
					
					var name = $("#edit-username").val();
					var permission = $("#edit-permission").val();
					
					onSuccess = function() {
						$("#edit-user-dialog").dialog('close');
						that.refresh();
					}
					editUser(id, name, permission, onSuccess, onServerError);
				}
			}
		});
		
		$("#edit-username").val(user.name);
		$("#edit-permission").val(user["permission_mode"].toLowerCase());
		$("#edit-user-dialog").dialog('open');
	}
	
	function onRemoveUser() {
		var id = $("#users-list").getGridParam("selrow");
		if (id == null) return;
		removeUser(id, that.refresh, onServerError);
	}
	
	function onEditUser() {
		var id = $("#users-list").getGridParam("selrow");
		if (id == null) return;
		openEditUser(id);
	}
}

function generatePassword() {
	var length = 8;
	var password = '';
	
    for (i = 0; i < length; i++) {
    	while (true) {
	        c = getRandomNumber();
	        if (isValidPasswordChar(c)) break;
		}
        password += String.fromCharCode(c);
    }
    return password;
}

function isValidPasswordChar(c) {
    if (c >= 33 && c<= 47) return false;
    if (c >= 58 && c <= 64) return false;
    if (c >= 91 && c <= 96) return false;
    if (c >= 123 && c <=126) return false;
    return true;
}

function getRandomNumber() {
	return (parseInt(Math.random() * 1000) % 94) + 33;
}