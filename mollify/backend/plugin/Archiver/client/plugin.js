/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

mollify.registerPlugin(new ArchiverPlugin());

function ArchiverPlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_archiver" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addItemContextProvider(that.getItemContext);
		$.getScript(that.env.pluginUrl("Archiver") + "client/texts_" + that.env.texts().locale + ".js");
	}
	
	this.getItemContext = function(item, details) {
		if (!details["plugin_archiver"] || !details["plugin_archiver"]["action_extract"]) return null;
		
		var action = details["plugin_archiver"]["action_extract"];
		
		return {
			components : [],
			actions : {
				primary : [],
				secondary: [
					{ title: "-" },
					{
						title: that.env.texts().get("plugin_archiver_extract_action"),
						callback: function(item) { that.onAction(action); }
					}
				]
			}
		}
	}
	
	this.onAction = function(action) {
		that.env.service().post(action,
			function(result) {},
			function(code, error) {
				alert("Extract error: "+code+"/"+error);
			}
		);
	}
}