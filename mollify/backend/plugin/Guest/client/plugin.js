/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

mollify.registerPlugin(new GuestPlugin());

function GuestPlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_guest" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addAction("Send upload link...", that.onSendUploadLink);
		that.env.addItemContextProvider(that.getItemContext);
	}
	
	this.getItemContext = function(item, details) {
		if (!item["is_file"]) return null;
		return {
			actions : {
				secondary: [
					{
						title: "-"
					},
					{
						title: "Send link...",
						callback: that.onSendDownloadLink
					}
				]
			}
		};
	}
	
	this.onSendUploadLink = function() {
		that.env.dialog().showDialog({
			title: "Send Upload Link",
			html: that.getUploadDialogContent(),
			on_show: that.onShowUploadDialog
		});
	}

	this.onSendDownloadLink = function(i) {
		that.env.dialog().showDialog({
			title: "Send Download Link",
			html: that.getDownloadDialogContent(i),
			on_show: function(d) {
				that.onShowDownloadDialog(d, i);
			}
		});
	}
		
	// UPLOAD
	
	this.getUploadDialogContent = function() {
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
	
	this.onShowUploadDialog = function(d) {
		that.dialog = d;
		
		$("#guestupload-dialog-send").click(that.onSendUpload);
		$("#guestupload-dialog-close").click(function(){ d.close(); });
		
		that.env.service().get("guest/upload/info/", function(result) {
			$("#guestupload-subject").val(result.subject);
			$("#guestupload-message").val(result.message);
		}, function(code, error) {
			alert("ERROR "+code);
		});
	}
	
	this.onSendUpload = function() {
		var to = $("#guestupload-to").val();
		var subject = $("#guestupload-subject").val();
		var msg = $("#guestupload-message").val();
		if (!to || to.length == 0 || !msg || msg.length == 0) return;
		
		that.env.service().post("guest/upload/send/", {to: to, subject: subject, message:msg}, function(result) {
			that.dialog.close();
		}, function(code, error) {
			alert("ERROR "+code);
		});
	}
	
	// DOWNLOAD
	
	this.getDownloadDialogContent = function(i) {
		return "<div id='guestdownload-dialog-content' style='height:100%'>"+
			"<table cellspacing=0 cellpadding=0 style='height:100%'>"+
			"<tr height='99%'><td align='left' style='vertical-align: top'>"+
			"    <div class='guestdownload-title'>To:</div><input id='guestdownload-to' style='width:200px'></input><br/>"+
			"    <div class='guestdownload-title'>Subject:</div><input id='guestdownload-subject' style='width:200px'></input><br/>"+
			"    <div class='guestdownload-title'>Message:</div><textarea rows='6' cols='60' id='guestdownload-message'></textarea>"+
			"</td></tr>"+
			"<tr height='1%'><td align='right'>"+
			"    <table class='guestdownload-buttons'>"+
			"        <tr><td><button id='guestdownload-dialog-send' class='gwt-Button mollify-permission-editor-button' type='button' style=''>Send</button></td>"+
			"        <td><button id='guestdownload-dialog-close' class='gwt-Button mollify-permission-editor-button' type='button'>Close</button></td></tr>"+
			"    </table>"+
			"</td></tr></table></div>";
	}
	
	this.onShowDownloadDialog = function(d, i) {
		that.dialog = d;
		
		$("#guestdownload-dialog-send").click(function() { that.onSendDownload(i); });
		$("#guestdownload-dialog-close").click(function() { d.close(); });
		
		that.env.service().get("guest/download/info/", function(result) {
			var msg = result.message;
			msg = msg.replace(/\%name\%/g, i.name);
			
			$("#guestdownload-subject").val(result.subject);
			$("#guestdownload-message").val(msg);
		}, function(code, error) {
			alert("ERROR "+code);
		});
	}

	this.onSendDownload = function(i) {
		var to = $("#guestdownload-to").val();
		var subject = $("#guestdownload-subject").val();
		var msg = $("#guestdownload-message").val();
		if (!to || to.length == 0 || !msg || msg.length == 0) return;
		
		that.env.service().post("guest/download/send/", {to: to, subject: subject, message:msg, id: i.id}, function(result) {
			that.dialog.close();
		}, function(code, error) {
			alert("ERROR "+code);
		});
	}
}