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
		if (!that.env.session().isAdminOrStaff() || item["is_protected"]) return null;
		
		return {
			components : [
				{
					type: "custom",
					html: "<div class='share-plugin'><a id='share-link'>Share</a></div>",
					on_init: that.onInit
				}
			]
		};
	}
	
	this.onInit = function(id, item, details) {
		$("#share-link").click(function() { that.onShare(item); });
	}
	
	this.onShare = function(item) {
		that.env.dialog().showDialog({
			title: "Share",
			html: that.getDialogContent(item),
			on_show: that.onShowDialog
		});
	}
	
	this.getDialogContent = function(item) {
		return "<div id='share-dialog-content'>"+
			"<a id='share-dialog-close'>Close</a>"+
			"</div>";
	}
	
	this.onShowDialog = function(d) {
		$("#share-dialog-close").click(function(){ d.close() });
	}
}