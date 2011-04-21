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
			html: "<div id='plupload-dialog-content'><div id='plupload-content-loading' /></div>",
			on_show: function(d) { that.onShowUploadDialog(d, folder, listener); }
		});
	}
	
	this.onShowUploadDialog = function(d, f, l) {
		that.d = d;
		
		mollify.loadContent("plupload-dialog-content", that.url("uploader.html"), function() {
			d.setMinimumSizeToCurrent();
			d.center();

			var uploader = new plupload.Uploader(that.getSettings(f));

			$('#plupload-cancel-select-button').click(function(e) {
				uploader.destroy();
				d.close();
			});
			$('#plupload-cancel-upload-button').click(function(e) {
				uploader.stop();
				uploader.destroy();
				d.close();
			});
			$('#plupload-upload-button').click(function(e) {
				e.preventDefault();
				if (uploader.files.length == 0) return;

				//that.fakeUpload();
				//return;
				
				that.env.service().post("plupload/"+f.id+"/check/", {files: uploader.files}, function(result) {
					if (!result.ok) {
						var list = result.existing[0];
						for(var i=1; i < result.existing.length; i++) list = list + ", " + result.existing[i];
						alert(that.t("pluploadFilesAlreadyExist") + list);
						return;
					}
					
					$('#plupload-header-text').html(that.t("pluploadHeaderUploading"));
					$('#plupload-select-button').remove();
					
					$('#plupload-footer-select').hide();
					$('#plupload-footer-upload').show();
					$('#plupload-files').addClass("uploading");
					$(".plupload-file-remove").remove();
					
					uploader.start();
				},	function(code, error) {
					alert(error);
				});
			});
						
			uploader.bind('Init', function(up, params) {
				that.initialized = true;
				$('#plupload-content').show();
				that.addNoFilesLabel();
			});

			uploader.bind('FilesAdded', function(up, files) {
				if (that.uploader.files.length == 0) $("#plupload-files").html('');
				
				$("#plupload-file-template").tmpl(files, {formatSize: that.formatFileSize}).appendTo("#plupload-files");
				
				$(".plupload-file").hover(function(){
					$(this).addClass("plupload-file-over");
				}, function(){
					$(this).removeClass("plupload-file-over");
				});
				
				$(".plupload-file-remove").hover(function(){
					$(this).addClass("plupload-file-remove-over");
				}, function(){
					$(this).removeClass("plupload-file-remove-over");
				}).click(function(){
					var id = $(this).parent().attr('id');
					that.removeFile(id);
				});
        		up.refresh();
    		});

			uploader.bind('UploadFile', function(up, file) {
				$('.plupload-file').removeClass("active");
				$('#'+file.id).addClass("active");
				$('#'+file.id+'-progress').show();
				
				that.onFileProgress({id: file.id, percent:0});
				up.refresh();
			});

			uploader.bind('UploadProgress', function(up, file) {
				that.onFileProgress(file);
				up.refresh();
			});

		    uploader.bind('FileUploaded', function(up, file) {
		    	$('#'+file.id+'-progress').html('');
		    	$('#'+file.id).removeClass("active");
		    	$('#'+file.id).addClass("complete");
			});

			uploader.bind('UploadComplete', function(up, files) {
				that.uploader.destroy();
				that.d.close();
				that.env.fileview().refresh();
			});

			uploader.bind('Error', function(up, err) {
				if (err.code == -600) {
					that.removeFile(err.file.id);
					alert(that.t("pluploadErrorFileTooBig") + " (" + that.formatFileSize(that.uploader.settings["max_file_size"]) + ")");
					return;
				}
				alert("Error: " + err.code + ", Message: " + err.message + (err.file ? ", File: " + err.file.name : ""));
				uploader.stop();
				up.refresh();
    		});
			
			that.uploader = uploader;
			uploader.init();
			
			window.setTimeout(function() {
				if (!that.initialized) {
					that.d.hide();
					alert("Invalid Plupload configuration, initialization failed.");
				}
			}, 5000);
		});
	}
	
	this.addNoFilesLabel = function() {
		$("#plupload-files-empty").tmpl({}).appendTo("#plupload-files");
		mollify.localize("plupload-files");
	}
	
	this.removeFile = function(id) {
		var f = that.uploader.getFile(id);
		if (f) {
			that.uploader.removeFile(f);
			that.uploader.refresh();
		}
		$('#'+id).remove();
		if (that.uploader.files.length == 0) that.addNoFilesLabel();
	}
	
	this.formatFileSize = function(s) {
		return that.env.texts().formatSize(s);
	}
	
	this.fakeUpload = function() {
		$('#plupload-footer-select').hide();
		$('#plupload-footer-upload').show();
		$('#plupload-files').addClass("uploading");
		$(".plupload-file-remove").remove();
		
		var first = that.uploader.files[0];
		$('#'+first.id).addClass("active");
		$('#'+first.id+'-progress').show();
		that.onFileProgress({id: first.id, percent:40});
		
		var second = that.uploader.files[1];
    	$('#'+second.id+'-progress').html('');
    	$('#'+second.id).addClass("complete");
	}
	
	this.onFileProgress = function(file) {
		$('#'+file.id+'-progress').html(file.percent+"%"); //'<div class="plupload-file-progress-bar" style="width:'+file.percent+'%;"></div>');
	}
	
	this.getSettings = function(f) {
		var settings = mollify.getSettings()["plupload"];
		settings["browse_button"] = "plupload-select-button";
		settings["drop_element"] = "plupload-files";
		settings["container"] = "plupload-container";
		settings["url"] = that.serviceUrl(f.id);
		settings["flash_swf_url"] = that.url('plupload.flash.swf');
        settings["silverlight_xap_url"] = that.url('plupload.silverlight.xap');
        settings["multipart_params"] = {};
        settings["headers"] = {};
        settings["unique_names"] = false;
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