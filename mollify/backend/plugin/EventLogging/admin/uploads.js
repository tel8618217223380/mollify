/**
 * Copyright (c) 2008- Samuli J�rvel�
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

function MollifyUploadsView() {
	var that = this;
	this.pageUrl = "uploads.html";
	this.onLoadView = onLoadView;
	this.users = null;
	this.usersById = {}
	
	function onLoadView() {
		if (!getSession().features["event_logging"]) {
			onError("Event logging not enabled");
			return;
		}
		$("#button-search").click(that.onSearch);
				
		$("#uploads-range-start").datepicker();
		$("#uploads-range-end").datepicker();
		
		$("#uploaded-files-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['File'],
		   	colModel:[
				{name:'item',index:'item',width:150, sortable:true}
		   	],
		   	sortname:'item',
		   	sortorder:'asc',
			onSelectRow: function(id){
				that.onFileSelectionChanged();
			}
		});
		
		that.onFileSelectionChanged();
	}
	
	function timeFormatter(time, options, obj) {
		return formatDateTime(time);
	}
		
	this.onSearch = function() {
		var start = $("#uploads-range-start").val();
		if (start.length > 0) {
			try {
				start = parseDate(start);
			} catch (e) {
				alert("Invalid start date");
				return;
			}
		} else {
			start = null;
		}
		
		var end = $("#uploads-range-end").val();
		if (end.length > 0) {
			try {
				end = parseDate(end);
			} catch (e) {
				alert("Invalid end date");
				return;
			}
		} else {
			end = null;
		}
		
		if (start && end && start > end) {
			alert("Start date cannot be after end date");
			return;
		}

		that.lastSearch = {start:start, end:end};
		getUploads(start, end, null, that.onRefreshUploads, onServerError);
	}
	
	this.onRefreshUploads = function(files) {
		that.files = files;
		
		var grid = $("#uploaded-files-list");
		grid.jqGrid('clearGridData');

		for(var i=0;i < files.length;i++) {
			var file = files[i];			
			grid.jqGrid('addRowData', i, file);
		}

		that.onFileSelectionChanged();
	}
	
	this.inArray = function(a, o) {
		for (var i=0; i < a.length; i++)
			if (a[i] == o) return true;
		return false;
	}
	
	this.getSelectedFile = function() {
		return $("#uploaded-files-list").getGridParam("selrow");
	}
	
	this.onFileSelectionChanged = function() {
		var file = that.getSelectedFile();
		var selected = (file != null);
		file = selected ? that.files[file].item : null;
				
		$("#uploads-list").jqGrid('clearGridData');
		
		if (!selected) {
			$("#upload-details-data").hide();
			
			if (!that.files)
				$("#upload-details-info").html('<div class="message">Enter search criteria and click "Search"</div>');
			else if (that.files.length == 0)
				$("#upload-details-info").html('<div class="message">No uploads</div>');
			else
				$("#upload-details-info").html('<div class="message">Select file from the list to view details</div>');
		} else {
			$("#upload-details-info").html("<h1>"+file+"</h1>");
		}
	}
}

function getUploads(start, end, file, success, fail) {
	var data = {}
	if (start) data["start_time"] = formatInternalTime(start);
	if (end) data["end_time"] = formatInternalTime(end);
	if (file) data["file"] = item;
	
	request("POST", 'events/uploads', success, fail, JSON.stringify(data));
}