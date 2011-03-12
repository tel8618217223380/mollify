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
		mollify.importCss(that.url("style.css"));
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
		
		$("#details-comments-content").hover(
			function () { $(this).addClass("hover"); }, 
			function () { $(this).removeClass("hover"); }
		);
		$("#details-comments-content").click(function() {
			c.close();
			that.openComments(item);
		});
	}
	
	this.openComments = function(item) {
		that.env.dialog().showDialog({
			title: that.t("commentsDialogTitle"),
			html: "<div id='comments-dialog-content' />",
			on_show: function(d) { that.onShowCommentsDialog(d, item); }
		});
	}

	this.onShowCommentsDialog = function(d, item) {
		mollify.loadContent("comments-dialog-content", that.url("content.html"), that.t, function() {
			$("#comments-dialog-add").click(function(){ that.onAddComment(d, item); });
			$("#comments-dialog-close").click(function(){ d.close(); });
			
			that.env.service().get("comment/"+item.id, function(result) {
				that.onShowComments(item, result);
			},	function(code, error) {
				alert(error);
			});
		});
	}
	
	this.onAddComment = function(d, item) {
		var comment = $("#comments-dialog-add-text").val();
		if (!comment || comment.length == 0) return;
		
		that.env.service().post("comment/"+item.id, { comment: comment }, function(result) {
			d.close();
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.onShowComments = function(item, comments) {
		for (var i=0; i<comments.length; i++)
			comments[i].time = that.env.texts().formatInternalTime(comments[i].time);

		$("#comment-template").tmpl(comments).appendTo("#comments-list");
	}
	
	this.url = function(p) {
		return that.env.service().getPluginUrl("comment")+"client/"+p;
	}
	
	this.t = function(s) {
		return that.env.texts().get(s);
	}
}