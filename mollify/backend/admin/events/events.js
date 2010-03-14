/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

function MollifyEventsView() {
	var that = this;
	this.pageUrl = "events/events.html";
	this.onLoadView = onLoadView;
	
	function onLoadView() {		
		$("#button-search").click(that.onSearch);
		$("#event-range-start").datepicker();
		$("#event-range-end").datepicker();
		
		$("#events-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['ID', 'Time', 'User', 'Type', 'Item', 'Description'],
		   	colModel:[
			   	{name:'id',index:'id', width:20, sortable:true, sorttype:"int"},
		   		{name:'time',index:'time', width:100, sortable:true, formatter:timeFormatter},
				{name:'user',index:'user',width:100, sortable:true},
				{name:'type',index:'type',width:100, sortable:true, formatter:typeFormatter},
				{name:'item',index:'item',width:150, sortable:true},
				{name:'description',index:'description',width:150, sortable:true},
		   	],
		   	sortname:'time',
		   	sortorder:'asc',
		});
	}
	
	function timeFormatter(time, options, obj) {
		return formatDateTime(time);
	}

	function typeFormatter(type, options, obj) {
		if (type == 'filesystem/download') return "Download";
		return "Unknown ("+type+")";
	}
		
	this.onSearch = function() {
		var start = $("#event-range-start").val();
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
		
		var end = $("#event-range-end").val();
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

		getEvents(start, end, that.onRefreshEvents, onServerError);
	}
	
	this.onRefreshEvents = function(events) {
		that.events = {};

		var grid = $("#events-list");
		grid.jqGrid('clearGridData');
		
		for(var i=0;i < events.length;i++) {
			var event = events[i];			
			event.time = parseInternalTime(event.time);
			
			that.events[event.id] = event;
			grid.jqGrid('addRowData', event.id, event);
		}
	}
}

function getEvents(rangeStart, rangeEnd, success, fail) {
	var data = {}
	if (rangeStart) data["start"] = formatInternalTime(rangeStart);
	if (rangeEnd) data["end"] = formatInternalTime(rangeEnd);
	request("POST", 'events/query', success, fail, JSON.stringify(data));
}