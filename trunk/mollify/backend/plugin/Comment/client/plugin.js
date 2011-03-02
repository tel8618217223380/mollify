/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

mollify.registerPlugin(new CommentPlugin());

function CommentPlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_comment" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addItemContextProvider(that.getItemContext);
		
		importCss(that.env.service().getPluginUrl("comment")+"client/style.css");
	}
	
	this.getItemContext = function(item, details) {
		return {
			components : [{
				type: "custom",
				html: "",
				on_init: that.onInit
			}]
		};
	}
	
	this.onInit = function(id, c, item, details) {
		if (!details.comments) return;
		
		$("#"+id).html("<div class='details-comments'><div id='details-comments-content'><div class='details-comments-icon'/><div id='details-comment-count'>"+details.comments.count+"</div></div></div>");
		$("#details-comments-content").hover(function () {
			$(this).addClass("hover");
		}, 
		function () {
			$(this).removeClass("hover");
		});
		$("#details-comments-content").click(function() { c.close(); that.openComments(item); });
	}
	
	this.openComments = function(item) {
		that.env.dialog().showDialog({
			title: that.t("commentsDialogTitle"),
			html: that.getCommentsDialogContent(item),
			on_show: function(d) { that.onShowCommentsDialog(d, item); }
		});
	}
	
	this.getCommentsDialogContent = function(item) {
		return "<div id='comments-dialog-content' style='width:100%; height:100%'>"+
			"<table cellspacing=0 cellpadding=0 style='width:100%; height:100%'>"+
			"<tr height='99%'><td align='left' style='vertical-align: top'>"+
			"<div id='comments-list'></div>"+
			"<div id='new-comment'><input type='text' id='comments-dialog-add-text' /></div>"+
			"</td></tr>"+
			"<tr height='1%'><td align='right'>"+
			"    <table class='comments-dialog-buttons' style='width:100%'>"+
			"        <tr><td align='right'><button id='comments-dialog-add-btn' class='gwt-Button comments-dialog-button' type='button'>"+that.t("commentsDialogAddButton")+"</button><button id='comments-dialog-close' class='gwt-Button comments-dialog-button' type='button'>"+that.t("dialogCloseButton")+"</button></td>"+
			"    </table>"+
			"</td></tr></table></div>";
	}

	this.onShowCommentsDialog = function(d, item) {
		$("#comments-dialog-close").click(function(){ d.close(); });
		
		that.env.service().get("comments/"+item.id(), function(result) {
			that.onShowComments(item, result);
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.onShowComments = function(item, comments) {
		var list = $("#comments-list");
		for (var i=0; i<comments.length; i++) {
			var c = comments[i];
			list.append(c.comment);
		}
	}
	
	this.t = function(s) {
		return that.env.texts().get(s);
	}
}