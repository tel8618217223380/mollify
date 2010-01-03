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
	
	this.pageUrl = "configure/users/users.html";
	this.users = null;
		
	this.onLoadView = onLoadView;
	
	function onLoadView() {
		$("#button-add-user").click(openAddUser);
		$("#button-remove-user").click(onRemoveUser);
		$("#button-refresh-users").click(refresh);
		
		refresh();
	}
	
	function refresh() {
		getUsers(refreshUsers, onServerError);
	}
	
	function refreshUsers(users) {
		that.users = users;

		var grid = $("#users-list");
		grid.jqGrid('clearGridData');
		
		grid.jqGrid({        
			datatype: "local",
			multiselect: true,
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
			}
		});
		
//		grid[0].addJSONData(that.users);
		for(var i=0;i < that.users.length;i++) {
			grid.jqGrid('addRowData', that.users[i].id, that.users[i]);
		}
	}
	
	function permissionModeFormatter(mode, options, rowObject) {
		switch (mode.toLowerCase()) {
			case 'a': return "Admin";
			case 'rw': return "Read and Write";
			case 'ro': return "Read Only";
			default: return "-";
		}
	}
	
	function onAddUser() {
		var name = $("#username").val();
		var pw = $("#password").val();
		var permission = $("#permission").val();
		
		onSuccess = function() {
			$("#user-dialog").dialog('close');
			refresh();
		}
		addUser(name, pw, permission, onSuccess, onServerError);
	}
	
	function validateUserData() {
		$("#user-dialog > .user-data").removeClass("invalid");
	
		var result = true;
		if ($("#username").val().length == 0) {
			$("#user-username").addClass("invalid");
			result = false;
		}
		if ($("#password").val().length == 0) {
			$("#user-password").addClass("invalid");
			result = false;
		}
		return result;
	}
	
	function openAddUser() {
		$("#user-dialog").dialog({
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
					if (validateUserData()) onAddUser();
				}
			}
		});
		$("#button-generate-user-password").click(function() {
			$("#password").val(generatePassword());
		});
		$("#user-dialog").dialog('open');
	}
	
	function onRemoveUser() {
		var ids = $("#users-list").getGridParam("selarrrow");
		if (!ids || ids.length != 1) return;
		removeUser(ids[0], refresh, onServerError);
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