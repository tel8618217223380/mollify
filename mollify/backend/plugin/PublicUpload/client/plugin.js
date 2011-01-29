/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

mollify.registerPlugin(new PublicUploadPlugin());

function PublicUploadPlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_public_upload" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addItemContextProvider(that.getItemContext);
	}
	
	this.getItemContext = function(item, details) {
		if (item["is_file"] || details["permission"].toLowerCase() != 'rw') return null;
		
		return {
			actions : {
				secondary: [
					{
						title: "-"
					},
					{
						title: "Get public upload link...",
						callback: that.onGetPublicUploadLink
					}
				]
			}
		};
	}
	
	this.onGetPublicUploadLink = function(item) {
		that.env.dialog().showDialog({
			title: "Public Upload Link",
			html: that.getUploadDialogContent(),
			on_show: function(d) { that.onShowUploadDialog(d, item); }
		});
	}
	
	this.getUploadDialogContent = function() {
		return "<div id='public-upload-dialog-content'><div id='public-upload-content'>"+
			"<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'>"+
			"<tr height='99%'><td align='left' style='vertical-align: top'>"+
			"    <div class='public-upload-title'>Upload to:</div>"+
			"    <div class='public-upload-value' id='public-upload-value-to'><input id='public-upload-to' readonly='readonly'></input></div>"+
			"    <div class='public-upload-title'>Link:</div><div id='public-upload-value-link' class='public-upload-value'><input id='public-upload-link' readonly='readonly'></input></div>"+
			"</td></tr>"+
			"<tr height='1%'><td align='right'>"+
			"    <table class='public-upload-buttons' style='width:100%'>"+
			"        <tr><td align='right'><button id='public-upload-dialog-close' class='gwt-Button mollify-public-upload-button' type='button'>Close</button></td>"+
			"    </table>"+
			"</td></tr></table></div></div>";
	}
	
	this.onShowUploadDialog = function(d, i) {
		that.dialog = d;
		
		$("#public-upload-dialog-close").click(function(){ d.close(); });		
		$("#public-upload-to").val(i.name);
		$("#public-upload-link").val(that.env.service().getPluginUrl("PublicUpload") + "?id=" + i.id);
	}
}