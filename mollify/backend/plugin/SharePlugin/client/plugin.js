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
					html: "",
					on_init: that.onInit
				}
			]
		};
	}
	
	this.onInit = function(id, item, details) {
		$("#"+id).html("<div class='share-plugin'><a id='share-link'>Share</a></div>");
		$("#share-link").click(function() { that.onShare(item); });
	}
	
	this.onShare = function(item) {
		alert("todo");
	}
}