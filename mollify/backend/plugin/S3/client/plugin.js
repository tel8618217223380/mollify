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
			title: that.t('fileUploadDialogTitle'),
			html: that.getUploadDialogContent(),
			on_show: function(d) { that.onShowUploadDialog(d, folder, listener); }
		});
	}
	
	this.getUploadDialogContent = function() {
		return "<div id='s3-upload-dialog-content' style='width:100%; height:100%'>"+
			"<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'>"+
			"<tr height='99%'><td align='left' style='vertical-align: top'>"+
			"    <form id='s3-upload-form' method='post' enctype='multipart/form-data' target='s3-upload-frame'>"+
			"        <input type='file' name='file' />"+
			"    </form>"+
			"    <div id='s3-upload-progress' style='display:none'>"+that.t("fileUploadProgressPleaseWait")+"</div>"+
			"</td></tr>"+
			"<tr height='1%'><td align='right'>"+
			"    <table class='s3-upload-buttons' style='width:100%'>"+
			"        <tr><td align='right'>"+
			"             <button id='s3-upload-dialog-upload' class='gwt-Button mollify-s3-upload-button' type='button'>"+that.t('fileUploadDialogUploadButton')+"</button>"+
			"             <button id='s3-upload-dialog-close' class='gwt-Button mollify-s3-upload-button' type='button'>"+that.t('dialogCloseButton')+"</button>"+
			"        </td></tr>"+
			"    </table>"+
			"</td></tr></table></div>";
	}
	
	this.onShowUploadDialog = function(d, f, l) {
		if (!$('#s3-upload-frame').length)
			$('body').append('<iframe name="s3-upload-frame" id="s3-upload-frame" style="display:none"></iframe>');
		$('#s3-upload-frame').contents().find('body').html('');
		$("#s3-upload-dialog-close").click(function() { d.close(); });
		$("#s3-upload-dialog-upload").click(function() {
			$('#s3-upload-frame').one('load', function () {
				var response;
				try {
					response = $('#s3-upload-frame').contents().find('body').html();
				} catch (e) {
					d.close();
					l.fail("Error:"+e);
					return;
				}
				if (response == 'ok') {
					d.close();
					l.success();
					return;
				}
				d.close();
				l.fail("Invalid response:"+response);
			});
			$("#s3-upload-form").submit();
		});
		
		that.env.service().get("s3/upload?id="+f.id, function(result) {
			var form = $("#s3-upload-form");
			form.attr("action", result.url);
			
			var keys = result.keys;
			for (k in keys)
				form.prepend("<input type='hidden' name='"+k+"' value='"+keys[k]+"'></input>");
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.t = function(s) {
		return that.env.texts().get(s);
	}
}