/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

function DownloadsView() {
	var that = this;
	this.pageUrl = "custom/downloads.html";
	this.onLoadView = onLoadView;
	this.users = null;
	this.usersById = {}
	
	function onLoadView() {
		if (!getSession().features["event_logging"]) {
			onError("Event logging not enabled");
			return;
		}
		$("#downloads-user-text").hide();
		$("#downloads-pager-controls").hide();
		
		$("#button-search").click(that.onSearch);
		$("#downloads-user").change(that.onUserChanged);
				
		$("#downloads-range-start").datepicker();
		$("#downloads-range-end").datepicker();
		
		$("#downloaded-files-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
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
		
		$("#downloads-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['User', 'Time'],
		   	colModel:[
				{name:'user',index:'user',width:300, sortable:true},
		   		{name:'time',index:'time', width:200, sortable:true, formatter:timeFormatter},
		   	],
		   	sortname:'time',
		   	sortorder:'asc',
		});
		
		$("#users-not-downloaded-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['User'],
		   	colModel:[
				{name:'name',index:'name',width:300, sortable:true},
		   	],
		   	sortname:'user',
		   	sortorder:'asc',
		});
		
		getUsers(that.refreshUsers, onServerError);
	}
	
	this.refreshUsers = function(users) {
		that.users = users;
		that.usersById = {}
		
		for (var i=0; i < users.length; i++) {
			var user = users[i];
			that.usersById[user.id] = user;
		}
	}
	
	function timeFormatter(time, options, obj) {
		return formatDateTime(time);
	}
		
	this.onSearch = function() {
		var start = $("#downloads-range-start").val();
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
		
		var end = $("#downloads-range-end").val();
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
		
		var item = $("#downloads-item-text").val();
		if (!item || item.length == 0) item = null

		getDownloadEvents(start, end, item, that.onRefreshEvents, onServerError);
	}
	
	this.onRefreshEvents = function(result) {
		that.lastSearch = result;
		that.events = [];
		that.eventsById = {};
		that.files = [];
		
		for(var i=0;i < result.events.length;i++) {
			var event = result.events[i];			
			event.time = parseInternalTime(event.time);
			
			that.eventsById[event.id] = event;
			that.events.push(event);
			
			if (!that.inArray(that.files, event.item))
				that.files.push(event.item);
		}

		that.refreshFiles();
		that.onFileSelectionChanged();
	}
	
	this.inArray = function(a, o) {
		for (var i=0; i < a.length; i++)
			if (a[i] == o) return true;
		return false;
	}
	
	this.refreshFiles = function() {
		var grid = $("#downloaded-files-list");
		grid.jqGrid('clearGridData');
		
		for(var i=0;i < that.files.length;i++) {
			var file = that.files[i];			
			grid.jqGrid('addRowData', i, {item:file});
		}
	}
	
	this.getSelectedFile = function() {
		return $("#downloaded-files-list").getGridParam("selrow");
	}
	
	this.onFileSelectionChanged = function() {
		var fileNr = that.getSelectedFile();
		var selected = (fileNr != null);
		var file = selected ? that.files[fileNr] : null;
				
		$("#downloads-list").jqGrid('clearGridData');
		$("#users-not-downloaded-list").jqGrid('clearGridData');
		
		if (selected) {
			$("#download-details-info").html("<h1>"+file+"</h1>");
			that.refreshDetails(file);
		}
		
		if (!selected) {
			$("#download-details-data").hide();
		} else {
			$("#download-details-data").show();
		}
	}
	
	this.refreshDetails = function(file) {
		var grid = $("#downloads-list");
		var downloaded = [];
		
		for(var i=0;i < that.events.length;i++) {
			var event = that.events[i];
			if (event.item != file) continue;
			downloaded.push(event.user);
			grid.jqGrid('addRowData', event.id, event);
		}
		
		var grid = $("#users-not-downloaded-list");
		for(var i=0;i < that.users.length;i++) {
			var user = that.users[i];
			if (that.inArray(downloaded, user.name)) continue;
			
			grid.jqGrid('addRowData', user.id, user);
		}
	}
}

function getDownloadEvents(rangeStart, rangeEnd, item, success, fail) {
	var data = {}
	if (rangeStart) data["start_time"] = formatInternalTime(rangeStart);
	if (rangeEnd) data["end_time"] = formatInternalTime(rangeEnd);
	if (item) data["item"] = item;
	data["max_rows"] = 5000;
	data['type'] = 'filesystem/download';
	
	request("POST", 'events/query', success, fail, JSON.stringify(data));
}