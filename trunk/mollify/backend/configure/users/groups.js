/**
	Copyright (c) 2008- Samuli Järvelä

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	this entire header must remain intact.
*/

function MollifyUserGroupsConfigurationView() {
	var that = this;
	
	this.pageUrl = "users/groups.html";
	this.users = null;
	this.groups = null;
	this.groupUsers = null;
		
	this.onLoadView = onLoadView;
	
	function onLoadView() {
		loadScript("users/common.js", that.init);
	}
	
	this.init = function() {		
		$("#button-add-group").click(openAddGroup);
		$("#button-remove-group").click(onRemoveGroup);
		$("#button-edit-group").click(onEditGroup);
		$("#button-refresh-groups").click(that.refresh);

		$("#groups-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['ID', 'Name','Permission'],
		   	colModel:[
			   	{name:'id',index:'id', width:60, sortable:true, sorttype:"int"},
		   		{name:'name',index:'name', width:200, sortable:true},
				{name:'permission_mode',index:'permission_mode',width:150, sortable:true, formatter:permissionModeFormatter},
		   	],
		   	sortname:'id',
		   	sortorder:'asc',
			onSelectRow: function(id){
				that.onGroupSelectionChanged();
			}
		});

		$("#button-add-group-users").click(openAddGroupUsers);
		$("#button-remove-group-users").click(onRemoveGroupUsers);
		$("#button-refresh-group-users").click(that.refreshGroupUsers);

		$("#group-users-list").jqGrid({        
			datatype: "local",
			multiselect: true,
			autowidth: true,
			height: '100%',
		   	colNames:['ID', 'Name'],
		   	colModel:[
			   	{name:'id',index:'id', width:60, sortable:true, sorttype:"int"},
		   		{name:'name',index:'name', width:200, sortable:true}
		   	],
		   	sortname:'id',
		   	sortorder:'asc',
			onSelectRow: function(id){
				that.onGroupUserSelectionChanged();
			}
		});

		$("#add-users-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			multiselect: true,
		   	colNames:['ID', 'Name'],
		   	colModel:[
			   	{name:'id',index:'id', width:60, sortable:true, sorttype:"int"},
		   		{name:'name',index:'name', width:200, sortable:true}
		   	],
		   	sortname:'id',
		   	sortorder:'asc'
		});
		
		that.refresh();
	}
	
	this.getUserGroup = function(id) {
		return that.groups[id];
	}

	this.getGroupUser = function(id) {
		return that.groupUsers[id];
	}

	this.getSelectedGroup = function() {
		return $("#groups-list").getGridParam("selrow");
	}

	this.getSelectedGroupUsers = function() {
		return $("#group-users-list").getGridParam("selarrrow");
	}

	this.refresh = function() {
		getUserGroups(refreshGroups, onServerError);
	}
	
	function refreshGroups(groups) {
		that.groups = {};

		var grid = $("#groups-list");
		grid.jqGrid('clearGridData');
		
		for(var i=0;i < groups.length;i++) {
			var group = groups[i];
			that.groups[group.id] = group;
			grid.jqGrid('addRowData', group.id, group);
		}
		
		that.onGroupSelectionChanged();
		
		getUsers(that.refreshUsers, onServerError);
	}
	
	this.refreshUsers = function(users) {
		that.users = {};
		
		for (var i=0; i < users.length; i++) {
			user = users[i];
			that.users[user.id] = user;
		}
	}
	
	this.refreshGroupUsers = function() {
		var id = $("#groups-list").getGridParam("selrow");
		if (!id) return;
		
		getGroupUsers(id, that.onRefreshGroupUsers, onServerError);
	}
	
	this.onRefreshGroupUsers = function(groupUsers) {
		that.groupUsers = {};
		
		var grid = $("#group-users-list");
		grid.jqGrid('clearGridData');

		for (var i=0; i < groupUsers.length; i++) {
			var groupUser = groupUsers[i];
			that.groupUsers[groupUser.id] = groupUser;
			grid.jqGrid('addRowData', groupUser.id, groupUser);
		}
				
		that.onGroupUserSelectionChanged();
	}
		
	this.onGroupSelectionChanged = function() {
		var group = that.getSelectedGroup();
		var selected = (group != null);
		if (selected) group = that.getUserGroup(group);
		
		that.groupUsers = null;
		
		enableButton("button-remove-group", selected);
		enableButton("button-edit-group", selected);
		
		if (that.groups.length == 0) {
			$("#group-details-info").html('<div class="message">Click "Add Group" to create a new user group</div>');
		} else {
			if (selected) {
				$("#group-users-list").jqGrid('setGridWidth', $("#group-details").width(), true);
				$("#group-details-info").html("<h1>Group '"+group.name+"'</h1>");
				
				that.refreshGroupUsers();
			} else {
				$("#group-details-info").html('<div class="message">Select a group from the list to view details</div>');
			}
		}
		
		if (!selected) {
			$("#group-details-data").hide();
		} else {
			$("#group-details-data").show();
		}
	}

	this.onGroupUserSelectionChanged = function() {
		var selected = (that.getSelectedGroupUsers().length > 0);
		enableButton("button-remove-group-users", selected);		
	}
	
	function validateGroupData() {
		$("#group-dialog > .user-data").removeClass("invalid");
	
		var result = true;
		if ($("#groupname").val().length == 0) {
			$("#user-username").addClass("invalid");
			result = false;
		}
		return result;
	}

	function openAddGroup() {
		openAddEditGroup(null);
	}
	
	function openAddEditGroup(id) {		
		var buttons = {
			Cancel: function() {
				$(this).dialog('close');
			}
		}

		var action = function() {
			if (!validateGroupData()) return;
			
			var name = $("#groupname").val();
			var permission = $("#permission").val();
			
			onSuccess = function() {
				$("#group-dialog").dialog('close');
				that.refresh();
			}

			if (id)
				editUserGroup(id, name, permission, onSuccess, onServerError);
			else
				addUserGroup(name, permission, onSuccess, onServerError);
		}
		
		if (id)
			buttons["Edit"] = action;
		else
			buttons["Add"] = action;
				
		$("#group-dialog").dialog({
			bgiframe: true,
			height: 300,
			width: 270,
			modal: true,
			resizable: false,
			title: id ? "Edit Group" : "Add Group",
			buttons: buttons
		});
		
		if (id) {
			var group = that.getUserGroup(id);
			$("#groupname").val(group.name);
			$("#permission").val(group["permission_mode"].toLowerCase());
		} else {
			$("#groupname").val("");
			$("#permission").val("ro");
		}
		$("#group-dialog").dialog('open');
	}
	
	function onRemoveGroup() {
		var id = that.getSelectedGroup();
		if (id == null) return;
		removeUserGroup(id, that.refresh, onServerError);
	}
	
	function onEditGroup() {
		var id = that.getSelectedGroup();
		if (id == null) return;
		openAddEditGroup(id);
	}

	function openAddGroupUsers() {
		if (that.users == null) return;
		
		var availableUsers = that.getAvailableGroupUsers();
		if (availableUsers.length == 0) {
			alert("No more users available");
			return;
		}
		
		var grid = $("#add-users-list");
		grid.jqGrid('clearGridData');
		
		for(var i=0;i < availableUsers.length;i++) {
			grid.jqGrid('addRowData', availableUsers[i].id, availableUsers[i]);
		}

		var buttons = {
			Cancel: function() {
				$(this).dialog('close');
			},
			Add: function() {
				var sel = $("#add-users-list").getGridParam("selarrrow");
				if (sel.length == 0) return;

				var onSuccess = function() {
					$("#add-group-users-dialog").dialog('close');
					that.refreshGroupUsers();
				}
				
				addGroupUsers(that.getSelectedGroup(), sel, onSuccess, onServerError);
			}
		}
				
		$("#add-group-users-dialog").dialog({
			bgiframe: true,
			height: 300,
			width: 330,
			modal: true,
			resizable: true,
			title: "Add Users to Group",
			buttons: buttons
		});
		
		$("#add-group-users-dialog").dialog('open');
	}
	
	this.getAvailableGroupUsers = function() {
		var result = [];
		for (id in that.users) {
			if (!that.groupUsers[id])
				result.push(that.users[id]);
		}
		return result;
	}

	function onRemoveGroupUsers() {
		var sel = that.getSelectedGroupUsers();
		if (sel.length == 0) return;
		removeGroupUsers(that.getSelectedGroup(), sel, that.refreshGroupUsers, onServerError);
	}
}