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
				    drop: function (e, data) {
				        alert('Dropped: ' + data.files.length);
				    },
					progressall: function (e, data) {
						var progress = parseInt(data.loaded / data.total * 100, 10);
						console.log(progress);
				    },
					done: function(e, data) {
						
					}
				});	
			};
			
			this.initWidget = function($e, folder, l) {
				var $d = mollify.dom.template("mollify-tmpl-uploader-widget");
				$e.append($d);
				var uploadData = [];
				var totalFiles = 0;
				
				var $input = $d.find("input").fileupload({
					url: mollify.service.url("filesystem/"+folder.id+'/files/'),
					dataType: 'json',
					dropZone: $d.find(".mollify-uploader"),
				    /*add: function (e, data) {
				    	$input.attr("disabled", "disabled");
				        //alert('Dropped: ' + data.files.length);
				        uploadData.push(data);
				        totalFiles = totalFiles + data.files.length;
				        console.log(totalFiles);
				    },*/
				    /*send: function(e, data) {
				    	//if (data.files.length == 0) return false;
					    //if (l.start) l.start(data.files);
				    },*/
				    submit: function (e, data) {
					    console.log("submit");
					    var $this = $(this);
					    if (l.start) l.start(data.files, function() {
						    $this.fileupload('send', data);
					    });
        				return false;
        			},
					progressall: function (e, data) {
						var progress = parseInt(data.loaded / data.total * 100, 10);
						if (l.progress) l.progress(progress);
				    },
					done: function(e, data) {
						//console.log("done " + JSON.stringify(data));
						if (l.finished) l.finished();
					}
				});	
			};
			
			return {
				open : function(folder) {
					mollify.templates.load("mollify-uploader", mollify.templates.url("uploader.html"), function() {
						t.open(folder);
					});
				},
				initUploadWidget : function($e, folder, l) {
					mollify.templates.load("mollify-uploader", mollify.templates.url("uploader.html"), function() {
						t.initWidget($e, folder, l);
					});
				}
			};
		}
	}
});})(window.jQuery);