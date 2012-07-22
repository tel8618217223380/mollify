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
	};
	
	this.getItemContext = function(item, details) {
		if (!details["plugin-archiver"]) return null;
		var archiver = details["plugin-archiver"];
		if (!archiver["action_extract"] && !archiver["action_compress"]) return null;
		
		var actions = [{ title: "-" }];
		if (archiver["action_extract"]) {
			actions.push({
				title: that.env.texts().get("plugin_archiver_extractAction"),
				callback: function(item) { that.onExtract(archiver["action_extract"], false); }
			});
		}
		if (archiver["action_compress"]) {
			actions.push({
				title: that.env.texts().get("plugin_archiver_compressAction"),
				callback: function(item) { that.onCompress(archiver["action_compress"], false); }
			});
		}		
		
		return {
			actions : {
				secondary: actions
			}
		}
	};
	
	this.onExtract = function(url, allowOverwrite) {
		var wd = that.env.dialog().showWait(that.env.texts().get("pleaseWait"));
		var params = { overwrite: allowOverwrite };
		
		that.env.service().post(url, params,
			function(result) {
				wd.close();
				that.env.fileview().refresh();
			},
			function(code, error) {
				wd.close();
				if (code == 205) {
					that.env.dialog().showConfirmation({
						title: that.env.texts().get("plugin_archiver_extractFolderAlreadyExistsTitle"),
						message: that.env.texts().get("plugin_archiver_extractFolderAlreadyExistsMessage"),
						on_confirm: function() { that.onExtract(url, true); }
					});
					return;
				}
				alert("Extract error: "+code+"/"+error);
			}
		);
	};
	
	this.onCompress = function(url, allowOverwrite) {
		var wd = that.env.dialog().showWait(that.env.texts().get("pleaseWait"));
		var params = { overwrite: allowOverwrite };
		
		that.env.service().post(url, params,
			function(result) {
				wd.close();
				that.env.fileview().refresh();
			},
			function(code, error) {
				wd.close();
				if (code == 204) {
					that.env.dialog().showConfirmation({
						title: that.env.texts().get("plugin_archiver_compressFileAlreadyExistsTitle"),
						message: that.env.texts().get("plugin_archiver_compressFileAlreadyExistsMessage"),
						on_confirm: function() { that.onCompress(url, true); }
					});
					return;
				}
				alert("Compress error: "+code+"/"+error);
			}
		);
	}
}