mollify.registerPlugin(new PluploadPlugin());

function PluploadPlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_plupload" }; }
	
	this.initialize = function(env) {
		that.env = env;
		that.env.addUploader(that.onUpload);
	}
	
	this.onUpload = function(folder, listener) {
		that.env.dialog().showDialog({
			title: that.t('pluploadUploadDialogTitle'),
			html: "<div id='plupload-dialog-content' class='loading' />",
			on_show: function(d) { that.onShowUploadDialog(d, folder, listener); }
		});
	}
	
	this.onShowUploadDialog = function(d, f, l) {
		that.d = d;
		
		mollify.loadContent("plupload-dialog-content", that.url("uploader.html"), function() {
			d.setMinimumSizeToCurrent();
			d.center();
			
			$("#plupload-dialog-content").removeClass("loading");

			var uploader = new plupload.Uploader(that.getSettings(f));

			$('#plupload-cancel-select').click(function(e) {
				d.close();
			});
			$('#plupload-cancel-upload').click(function(e) {
				uploader.stop();
				d.close();
			});
			$('#plupload-upload').click(function(e) {
				e.preventDefault();
				if (uploader.files.length == 0) return;
				
				that.env.service().post("plupload/"+f.id+"/check/", {files: uploader.files}, function(result) {
					if (!result.ok) {
						alert("nope");
						return;
					}
					$('#plupload-select-buttons').hide();
					$('#plupload-upload-buttons').show();
					$('#plupload-files').addClass("uploading");
					
					uploader.start();
				},	function(code, error) {
					alert(error);
				});
			});
						
			uploader.bind('Init', function(up, params) {
				that.initialized = true;
				$('#plupload-content').show();
			});

			uploader.bind('FilesAdded', function(up, files) {
				$("#plupload-file-template").tmpl(files).appendTo("#plupload-files");
        		up.refresh();
    		});

			uploader.bind('UploadFile', function(up, file) {
				$('.plupload-file').removeClass("active");
				$('#'+file.id).addClass("active");
				up.refresh();
			});

			uploader.bind('UploadProgress', function(up, file) {
				$('#'+file.id+'-progress').html('<div id="box"><div id="bar" style="width:'+file.percent+'%;"></div></div>');
				up.refresh();
			});

			uploader.bind('UploadComplete', function(up, files) {
				that.d.close();
				that.env.fileview().refresh();
			});

			uploader.bind('Error', function(up, err) {
				alert("Error: " + err.code + ", Message: " + err.message + (err.file ? ", File: " + err.file.name : ""));
				uploader.stop();
				up.refresh();
    		});

		    uploader.bind('FileUploaded', function(up, file) {
		    	$('#'+file.id+'-progress').html('');
		    	$('#'+file.id).addClass("complete");
			});
			
			uploader.init();
			
			window.setTimeout(function() {
				if (!that.initialized) {
					that.d.hide();
					alert("Invalid Plupload configuration, initialization failed.");
				}
			}, 5000);
		});
	}
	
	this.getSettings = function(f) {
		var settings = mollify.getSettings()["plupload"];
		settings["browse_button"] = "plupload-select";
		settings["container"] = "plupload-container";
		settings["url"] = that.serviceUrl(f.id);
		settings["flash_swf_url"] = that.url('plupload.flash.swf');
        settings["silverlight_xap_url"] = that.url('plupload.silverlight.xap');
		return settings;
	}
	
	this.url = function(p) {
		var url = that.env.service().getPluginUrl("Plupload")+"client/";
		if (!p) return url;
		return url + p;
	}

	this.serviceUrl = function(id) {
		return that.env.service().getUrl("plupload")+"/"+id+"/";
	}
		
	this.t = function(s) {
		return that.env.texts().get(s);
	}
}