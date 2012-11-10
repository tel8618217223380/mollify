(function($){$.extend(true, mollify, {
	plugin : {
		MollifyUploader : function() {
			var t = this;
			
			this.open = function(folder) {
				var $d = mollify.dom.template("mollify-tmpl-uploader-dlg");
				mollify.ui.views.dialogs.custom({
					element: $d,
					buttons: [
						{ id:0, "title-key": "upload" },
						{ id:1, "title-key": "cancel" }
					],
					"on-button": function(btn, d) {
						if (btn.id == 1)
							d.close();
					},
					"on-show": function(dlg) { t.onOpen($d, dlg, folder); }
				});
			};
			
			this.onOpen = function($d, dlg, folder) {
				var $form = $d.find(".mollify-uploader-form");
				$form.append(mollify.dom.template("mollify-tmpl-uploader-file"));
			};
			
			return {
				open : function(folder) {
					mollify.templates.load("mollify-uploader", mollify.templates.url("uploader.html"), function() {
						t.open(folder);
					});
				}
			};
		}
	}
});})(window.jQuery);