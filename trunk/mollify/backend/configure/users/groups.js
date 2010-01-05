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
	this.groups = null;
		
	this.onLoadView = onLoadView;
	
	function onLoadView() {
		loadScript("users/common.js");
		
		$("#button-add-group").click(openAddGroup);
		
		that.refresh();
	}
	
	this.refresh = function() {
		getUserGroups(refreshGroups, onServerError);
	}
	
	function refreshGroups(groups) {
		that.groups = groups;

		var grid = $("#groups-list");
		grid.jqGrid('clearGridData');
		
		grid.jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['ID', 'Name','Permission'],
		   	colModel:[
			   	{name:'id',index:'id', width:20, sortable:true, sorttype:"int"},
		   		{name:'name',index:'name', width:200, sortable:true},
				{name:'permission_mode',index:'permission_mode',width:150, sortable:true},
		   	],
		   	sortname:'id',
		   	sortorder:'asc',
			onSelectRow: function(id){
				that.updateButtons();
			}
		});
		
		for(var i=0;i < that.groups.length;i++) {
			grid.jqGrid('addRowData', that.groups[i].id, that.groups[i]);
		}
		
		that.updateButtons();
	}
		
	this.updateButtons = function() {
	}
	
	function openAddGroup() {
		$("#group-dialog").dialog({
			bgiframe: true,
			height: 300,
			width: 270,
			modal: true,
			resizable: false,
			title: "Add Group",
			buttons: {
				Cancel: function() {
					$(this).dialog('close');
				},
				Add: function() {
				}
			}
		});
		
		$("#groupname").val("");
		$("#permission").val("ro");
		$("#add-group-dialog").dialog('open');
	}
		
}