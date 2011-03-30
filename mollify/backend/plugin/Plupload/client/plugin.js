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
		mollify.loadContent("plupload-dialog-content", that.url("uploader.html"), function() {
			d.setMinimumSizeToCurrent();
			d.center();
			
			$("#plupload-dialog-content").removeClass("loading");

			var uploader = new plupload.Uploader(that.getSettings());

			$('#plupload-upload').click(function(e) {
				uploader.start();
				e.preventDefault();
			});
						
			uploader.bind('Init', function(up, params) {
				alert("init");
				$('#plupload-files').html("<div>Current runtime: " + params.runtime + "</div>");
			});

			uploader.bind('FilesAdded', function(up, files) {
				$.each(files, function(i, file) {
				$('#plupload-files').append(
					'<div id="' + file.id + '">' + file.name + ' (' + plupload.formatSize(file.size) + ') <b></b></div>');
				});
        		up.refresh();
    		});

			uploader.bind('UploadProgress', function(up, file) {
				$('#' + file.id + " b").html(file.percent + "%");
			});

			uploader.bind('Error', function(up, err) {
				$('#filelist').append("<div>Error: " + err.code + ", Message: " + err.message + (err.file ? ", File: " + err.file.name : "") + "</div>");
	      	  up.refresh();
    		});

		    uploader.bind('FileUploaded', function(up, file) {
				$('#' + file.id + " b").html("100%");
			});
			
			uploader.init();
		});
	}
	
	this.getSettings = function() {
		var settings = mollify.getSettings()["plupload"];
		settings["browse_button"] = "plupload-select";
		settings["container"] = "plupload-container";
		settings["url"] = that.url();
		settings["flash_swf_url"] = that.url('plupload.flash.swf');
        settings["silverlight_xap_url"] = that.url('plupload.silverlight.xap');
		return settings;
	}
	
	this.url = function(p) {
		var url = that.env.service().getPluginUrl("Plupload")+"client/";
		if (!p) return url;
		return url + p;
	}
	
	this.t = function(s) {
		return that.env.texts().get(s);
	}
}