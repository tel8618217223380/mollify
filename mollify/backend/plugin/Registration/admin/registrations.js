/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

function PendingRegistrationsView() {
	var that = this;
	this.pageUrl = "registrations.html";
	this.list = null;
	
	this.onLoadView = function onLoadView() {
		$("#button-remove-registration").click(that.onRemove);
		$("#button-confirm-registration").click(that.onConfirm);
		$("#button-refresh").click(that.onRefresh);

		$("#registrations-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['ID', 'User', 'Email', 'Key', 'Time'],
		   	colModel:[
			   	{name:'id',index:'id', width:60, sortable:true, sorttype:"int"},
			   	{name:'name',index:'name',width:150, sortable:true},
			   	{name:'email',index:'email',width:150, sortable:true},
			   	{name:'key',index:'key',width:150, sortable:true},
		   		{name:'time',index:'time', width:150, sortable:true, formatter:timeFormatter},
		   	],
		   	sortname:'id',
		   	sortorder:'desc',
			onSelectRow: function(id){
				that.onRegistrationSelectionChanged();
			}
		});
		
		that.onRefresh();
	}
	
	this.onRefresh = function() {
		getRegistrations(that.refreshList, onServerError);
	}
	
	this.refreshList = function(list) {
		that.list = list;
		that.registrationsById = {}
		
		var grid = $("#registrations-list");
		grid.jqGrid('clearGridData');
		
		for (var i=0; i < list.length; i++) {
			var r = list[i];
			r.time = parseInternalTime(r.time);
			
			that.registrationsById[r.id] = r;
			grid.jqGrid('addRowData', r.id, r);
		}
		that.onRegistrationSelectionChanged();
	}
	
	this.getSelectedRegistration = function() {
		return $("#registrations-list").getGridParam("selrow");
	}

	this.getRegistration = function(id) {
		return that.list[id];
	}
	
	this.onRegistrationSelectionChanged = function() {
		var r = that.getSelectedRegistration();
		var selected = (r != null);
		if (selected) registration = that.getRegistration(r);
		
		enableButton("button-remove-registration", selected);
		enableButton("button-confirm-registration", selected);
	}
	
	this.onRemove = function() {
		var id = that.getSelectedRegistration();
		if (id == null) return;
		removeRegistration(id, that.refresh, onServerError);
	}

	this.onConfirm = function() {
		var id = that.getSelectedRegistration();
		if (id == null) return;
		registration = that.getRegistration(id);
		
		confirmRegistration(registration.email, registration.key, that.refresh, onServerError);
	}
	
	function timeFormatter(time, options, obj) {
		return formatDateTime(time);
	}
	
	function notNullFormatter(o, options, obj) {
		if (o == null) return '';
		return o;
	}
}

function getRegistrations(success, fail) {
	request("GET", 'registration/list/', success, fail);
}

function removeRegistration(id, success, fail) {
	request("DELETE", 'registration/list/'+id, success, fail);
}

function confirmRegistrations(email, key, success, fail) {
	request("POST", 'registration/confirm/', success, fail);
}