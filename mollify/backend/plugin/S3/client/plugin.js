/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

mollify.registerPlugin(new S3Plugin());

function S3Plugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_s3" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addUploader(that.onUpload);
	}
	
	this.onUpload = function(folder, listener) {
		that.env.dialog().showDialog({
			title: that.env.texts().get('fileUploadDialogTitle'),
			html: that.getUploadDialogContent(),
			on_show: function(d) { that.onShowUploadDialog(d); }
		});
	}
	
	/**"<form action='http://johnsmith.s3.amazonaws.com/' method='post' enctype='multipart/form-data'>"+
			"<input type='hidden' name="success_action_redirect" value="http://johnsmith.s3.amazonaws.com/successful_upload.html" />"+
			"<input type='hidden' name='AWSAccessKeyId' value='15B4D3461F177624206A' />"+
			"<input type='file' name="file" />"+
			"<input type='submit' name='submit' value="Upload to Amazon S3" />"+
			"</form>
	*/
	
	this.getUploadDialogContent = function() {
		return "<div id='s3-upload-dialog-content'><div id='s3-upload-content'>"+
			"<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'>"+
			"<tr height='99%'><td align='left' style='vertical-align: top'><div id='s3-upload-form' /></td></tr>"+
			"<tr height='1%'><td align='right'>"+
			"    <table class='s3-upload-buttons' style='width:100%'>"+
			"        <tr><td align='right'><button id='s3-upload-dialog-close' class='gwt-Button mollify-s3-upload-button' type='button'>"+that.env.texts().get('dialogCloseButton')+"</button></td>"+
			"    </table>"+
			"</td></tr></table></div></div>";
	}
	
	this.onShowUploadDialog = function(d) {
		that.dialog = d;
		
		$("#s3-upload-dialog-close").click(function(){ d.close(); });
		$("#s3-upload-form").load(that.env.service().get("s3/upload");
	}
}