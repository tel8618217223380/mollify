/**
 * Copyright (c) 2008- Samuli Järvelä
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */

mollify.registerPlugin(new GuestUploadPlugin());

function GuestUploadPlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_guest_upload" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addAction("Send upload link...", that.onSendUploadLink);
	}
	
	this.onSendUploadLink = function() {
		that.env.dialog().showDialog({
			title: "Send Upload Link",
			html: "Test",
			on_show: function(d) {
				//that.onShowDialog(d, item);
			}
		});
	}
}