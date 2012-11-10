(function($){$.extend(true, mollify, {
	plugin : {
		MollifyUploader : function(env) {
			var t = this;
			this.env = env;
			
			this.open = function(folder) {
				var $d = mollify.dom.template("mollify-tmpl-uploader-dlg");
				mollify.ui.views.dialogs.custom({
					element: $d,
					"title-key": 'fileUploadDialogTitle',
					buttons: [
						{ id:0, "title-key": "upload" },
						{ id:1, "title-key": "cancel" }
					],
					"on-button": function(btn, dlg) {
						if (btn.id == 1)
							dlg.close();
						else t.onUpload($d, dlg,folder);
					},
					"on-show": function(dlg) { t.onOpen($d, dlg, folder); }
				});
			};
			
			this.onOpen = function($d, dlg, folder) {
				//var $form = $d.find(".mollify-uploader-form");//.attr("action", );
				var $input = $d.find("input").on('change', function() {
					//if (!this.files || this.files.length == 0) return;
					//if (this.files.length == 1) alert(this.files[0].name);
					//else alert(this.files.length);
				}).fileupload({
					url: mollify.service.url("filesystem/"+folder.id+'/files/'),
					dataType: 'json',
					dropZone: $d.find(".mollify-uploader"),
					progressall: function (e, data) {
						var progress = parseInt(data.loaded / data.total * 100, 10);
						console.log(progress);
				    },
					done: function(e, data) {
						
					}
				});
				/*.html5_upload({
					fieldName: 'uploader-html5[]',
                    url: mollify.service.url("filesystem/"+folder.id+'/files/'),
                    autostart: false,
                    sendBoundary: window.FormData || $.browser.mozilla,
                    onStart: function(event, total) {
                    	alert(total);
                        return true;

                    },

                    genName: function(file, number, total) {
                        console.log("file "+file);
                    },
                    genStatus: function(progress, finished) {
                        console.log("status "+progress);
                    },
                    genProgress: function(loaded, total) {
                        console.log("progress " + loaded);
                    },
                    onFinishOne: function(event, response, name, number, total) {
                        //alert(response);
                        console.log("finished "+name);
                    },
                    onError: function(event, name, error) {
                        alert('error while uploading file ' + name);
                    }
                });*/	
			};
			
			this.onUpload = function($d, dlg, folder) {
				//TODO check if there are files
				//var $form = $d.find(".mollify-uploader-form");
				//$form.submit();
				var $input = $d.find("input");
				if ($input.length == 0) return;
				if (!$input[0].files || !$input[0].files.length == 0) return;
				$input.triggerHandler('html5_upload.start');
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