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
		that.refresh();
	}
	
	this.refresh = function() {
		getFolders(refreshFolders, onServerError);
	}
	
	function refreshFolders(folders) {
		that.folders = folders;

		var grid = $("#folders-list");
		grid.jqGrid('clearGridData');
		
		grid.jqGrid({        
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
				that.updateButtons();
			}
		});
		
		for(var i=0;i < that.folders.length;i++) {
			grid.jqGrid('addRowData', that.folders[i].id, that.folders[i]);
		}
		
		that.updateButtons();
	}
		
	this.updateButtons = function() {
	}				
}