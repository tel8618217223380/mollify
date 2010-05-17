/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

function NotificatorListView() {
	var that = this;
	this.pageUrl = "notificator.html";
	this.list = null;
	
	this.onLoadView = function onLoadView() {
		$("#button-add-notification").click(that.openAddNotification);
		$("#button-refresh").click(that.onRefresh);

		$("#notifications-list").jqGrid({        
			datatype: "local",
			multiselect: false,
			autowidth: true,
			height: '100%',
		   	colNames:['ID', 'Name'],
		   	colModel:[
			   	{name:'id',index:'id', width:60, sortable:true, sorttype:"int"},
			   	{name:'name',index:'name',width:150, sortable:true},
		   	],
		   	sortname:'id',
		   	sortorder:'desc',
			onSelectRow: function(id){
				that.onNotificationSelectionChanged();
			}
		});
		
		that.onRefresh();
	}
	
	this.onRefresh = function() {
		getNotifications(that.refreshList, onServerError);
	}
	
	this.refreshList = function(list) {
		that.list = list;
		that.notificationsById = {}
		
		var grid = $("#notifications-list");
		grid.jqGrid('clearGridData');
		
		for (var i=0; i < list.length; i++) {
			var r = list[i];
//			r.time = parseInternalTime(r.time);
			
			that.notificationsById[r.id] = r;
			grid.jqGrid('addRowData', r.id, r);
		}
		that.onNotificationSelectionChanged();
	}
	
	this.getSelectedNotification = function() {
		return $("#notifications-list").getGridParam("selrow");
	}

	this.getNotification = function(id) {
		return that.list[id];
	}
	
	this.onNotificationSelectionChanged = function() {
		var n = that.getSelectedNotification();
		var selected = (n != null);
		if (selected) notification = that.getNotification(n);
		
//		enableButton("button-remove-notification", selected);
//		enableButton("button-edit-notification", selected);
	}
	
	function timeFormatter(time, options, obj) {
		return formatDateTime(time);
	}
	
	function notNullFormatter(o, options, obj) {
		if (o == null) return '';
		return o;
	}
	
	this.openAddNotification = function() {
		if (!that.addNotificationDialogInit) {
			that.addNotificationDialogInit = true;

			$("#add-notification-dialog").dialog({
				autoOpen: false,
				bgiframe: true,
				height: 'auto',
				width: 270,
				modal: true,
				resizable: false,
				title: "Add Notification",
				buttons: {
					Cancel: function() {
						$(this).dialog('close');
					},
					Add: function() {
					}
				}
			});
		}
		
		$("#name").val("");
		$("#add-notification-dialog").dialog('open');
	}
}

function getNotifications(success, fail) {
	request("GET", 'notificator/list/', success, fail);
}