/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

mollify.registerPlugin(new GuestUploadPlugin());

function GuestUploadPlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_guest_upload" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addAction("Send upload link...", that.onSendUploadLink);
	}
	
	this.onSendUploadLink = function() {
		that.env.dialog().showDialog({
			title: "Send Upload Link",
			html: that.getDialogContent(),
			on_show: that.onShowDialog
		});
	}
	
	this.getDialogContent = function() {
		return "<div id='guestupload-dialog-content' style='height:100%'>"+
			"<table cellspacing=0 cellpadding=0 style='height:100%'>"+
			"<tr height='99%'><td align='left' style='vertical-align: top'>"+
			"    <div class='guestupload-title'>To:</div><input id='guestupload-to' style='width:200px'></input><br/>"+
			"    <div class='guestupload-title'>Subject:</div><input id='guestupload-subject' style='width:200px'></input><br/>"+
			"    <div class='guestupload-title'>Message:</div><textarea rows='6' cols='60' id='guestupload-message'></textarea>"+
			"</td></tr>"+
			"<tr height='1%'><td align='right'>"+
			"    <table class='guestupload-buttons'>"+
			"        <tr><td><button id='guestupload-dialog-send' class='gwt-Button mollify-permission-editor-button' type='button' style=''>Send</button></td>"+
			"        <td><button id='guestupload-dialog-close' class='gwt-Button mollify-permission-editor-button' type='button'>Close</button></td></tr>"+
			"    </table>"+
			"</td></tr></table></div>";
	}
	
	this.onShowDialog = function(d) {
		that.dialog = d;
		
		$("#guestupload-dialog-send").click(that.onSend);
		$("#guestupload-dialog-close").click(function(){ d.close(); });
		
		that.env.service().get("guestupload/info/", function(result) {
			that.update(result);
		}, function(code, error) {
			alert("ERROR "+code);
		});
	}
	
	this.update = function(r) {
		$("#guestupload-subject").val(r.subject);
		$("#guestupload-message").val(r.message);
	}
	
	this.onSend = function() {
		var to = $("#guestupload-to").val();
		var subject = $("#guestupload-subject").val();
		var msg = $("#guestupload-message").val();
		if (!to || to.length == 0 || !msg || msg.length == 0) return;
		
		that.env.service().post("guestupload/send/", {to: to, subject: subject, message:msg}, function(result) {
			that.dialog.close();
		}, function(code, error) {
			alert("ERROR "+code);
		});
	}
}