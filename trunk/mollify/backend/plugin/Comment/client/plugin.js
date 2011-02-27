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
	}
	
	this.getItemContext = function(item, details) {
		return {
			components : [
				{
					type: "custom",
//					title: that.t("details_comments_title"),
					html: "<b>Testi</b>",
					on_init: that.onInit
				}
			]
		};
	}
	
	this.onInit = function(id, item, details) {
		if (!details.comments) return;
		
		var html = "<div class='details-comments'>"+that.t("details_comments_title")+details.comments.count+"</div>";
		$("#"+id).html(html);
	}
	
	this.t = function(s) {
		return that.env.texts().get(s);
	}
}