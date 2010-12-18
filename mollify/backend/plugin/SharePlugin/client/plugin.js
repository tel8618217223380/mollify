/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

mollify.registerPlugin(new SharePlugin());

function SharePlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_share" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addItemContextProvider(that.getItemContext);
	}
	
	this.getItemContext = function(item, details) {
		if (!that.env.session().isAdminOrStaff() || item["is_protected"] || item.path == '') return null;
		
		return {
			components : [
				{
					type: "custom",
					html: "<div id='share-link' class='mollify-actionlink'>Share with...</div>",
					on_init: that.onInit
				}
			]
		};
	}
	
	this.onInit = function(id, container, item, details) {
		$("#share-link").click(function() {
			container.close();
			that.onShare(item);
		});
	}
	
	this.onShare = function(item) {
		that.env.dialog().showDialog({
			title: "Share With",
			html: that.getDialogContent(item),
			on_show: function(d) {
				that.onShowDialog(d, item);
			}
		});
	}
	
	this.getDialogContent = function(item) {
		return "<div id='share-dialog-content' style='height:100%'>"+
			"<table cellspacing=0 cellpadding=0 style='height:100%'>"+
			"<tr height='99%'><td align='left' style='vertical-align: top'>"+
			"    <div class='mollify-permission-editor-item-title'>Name:</div>" +
			"    <div id='share-name-value' class='mollify-permission-editor-item-name mollify-permission-editor-item-name-fixed'></div>" +
			"    <div class='mollify-permission-editor-item-title'>Users:</div>" +
			"    <div id='share-users'></div>" +
			"    <div class='mollify-permission-editor-item-title'>Permission:</div>" +
			"    <div class='share-permission'>" +
			"    <input type='radio' name='permission' value='rw'> Read and Write</input>" +
			"    <input type='radio' name='permission' value='ro'> Read Only</input></div>" +
			"</td></tr>"+
			"<tr height='1%'><td align='right'>"+
			"    <table class='share-buttons'>"+
			"        <tr><td><button id='share-dialog-share' class='gwt-Button mollify-permission-editor-button' type='button' style=''>Share</button></td>"+
			"        <td><button id='share-dialog-close' class='gwt-Button mollify-permission-editor-button' type='button'>Close</button></td></tr>"+
			"    </table>"+
			"</td></tr></table></div>";
	}
	
	this.onShowDialog = function(d, item) {
		that.dialog = d;
		that.item = item;
		
		$("#share-name-value").html(item.name);
		$("#share-dialog-share").click(that.onShareItem);
		$("#share-dialog-close").click(function(){ d.close() });
		
		that.env.service().get("configuration/users?t=no", function(result) {
			that.updateUsers(result);
		}, function(code, error) {
			alert("ERROR "+code);
		});
	}
	
	this.updateUsers = function(users) {
		that.users = users;
		
		var html = '<table>';
		for (var i=0; i<users.length; i++) {
			var u = users[i];
			html = html + '<tr><td><label><input type="checkbox" name="share-user" value="'+u.id+'"/> '+u.name+'</label></td></tr>';
		}
		html = html + '</table>';
		$("#share-users").html(html);
	}
	
	this.onShareItem = function() {
		var p = $("input:radio[@name=permission]:checked").val();
		if (!p) return;
		
		var selected = [];
		$("input:checkbox[@name=share-user]:checked").map(function() { selected.push($(this).val()); });
		if (selected.length == 0) return;
		
		that.env.service().post("share/"+that.item.id, {users:selected, permission:p}, function(result) {
			that.dialog.close();
		}, function(code, error) {
			alert("ERROR "+code);
		});
	}
}