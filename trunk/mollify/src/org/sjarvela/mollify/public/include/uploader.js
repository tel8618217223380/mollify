$.extend(true, mollify, {
	plugin : {
		MollifyUploader : function() {
			var t = this;
			
			this.open = function(folder) {
				mollify.ui.views.dialogs.custom({
					content: $("#mollify-uploader"),
					buttons: [
						{ id:0, "title-key": "upload" }
					],
					"on-button": function(btn, d) {
						d.close();
					}
				});
			};
			
			return {
				open : function(folder) {
					mollify.dom.loadContent("mollify-uploader", mollify.templates.url("uploader.html"), function() {
						t.open(folder);
					});
				}
			};
		}
	}
});
mollify.ui.uploader = new mollify.plugin.MollifyUploader();