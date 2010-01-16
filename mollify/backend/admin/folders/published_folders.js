/**
	Copyright (c) 2008- Samuli Järvelä

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
	this entire header must remain intact.
*/

function MollifyPublishedFoldersConfigurationView() {
	var that = this;
	
	this.pageUrl = "folders/published_folders.html";
	this.folders = null;
		
	this.onLoadView = onLoadView;
	
	function onLoadView() {
		$("#button-add-folder").click(that.openAddFolder);
		$("#button-remove-folder").click(that.onRemoveFolder);
		$("#button-edit-folder").click(that.onEditFolder);
		$("#button-refresh-folders").click(that.refresh);

		$("#folders-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['ID', 'Name','Path'],
		   	colModel:[
			   	{name:'id',index:'id', width:20, sortable:true, sorttype:"int"},
		   		{name:'name',index:'name', width:200, sortable:true},
				{name:'path',index:'path',width:150, sortable:true},
		   	],
		   	sortname:'id',
		   	sortorder:'asc',
			onSelectRow: function(id){
				that.onFolderSelectionChanged();
			}
		});

		that.refresh();
	}
	
	this.getFolder = function(id) {
		return that.folders[id];
	}
	
	this.getSelectedFolder = function() {
		return $("#folders-list").getGridParam("selrow");
	}
	
	this.refresh = function() {
		getFolders(that.onRefreshFolders, onServerError);
	}
	
	this.onRefreshFolders = function(folders) {
		that.folders = {};

		var grid = $("#folders-list");
		grid.jqGrid('clearGridData');
		
		for(var i=0;i < folders.length;i++) {
			var folder = folders[i];
			that.folders[folder.id] = folder;

			grid.jqGrid('addRowData', folder.id, folder);
		}
		
		this.onFolderSelectionChanged();
	}
		
	this.onFolderSelectionChanged = function() {
		var folder = that.getSelectedFolder();
		var selected = (folder != null);
		if (selected) folder = that.getFolder(folder);
		
		enableButton("button-remove-folder", selected);
		enableButton("button-edit-folder", selected);		
	}
	
	this.openAddFolder = function() {}
	this.openEditFolder = function() {}
	this.onRemoveFolder = function() {}
}