!function($, mollify) {

	"use strict"; // jshint ;_;
	
	mollify.plugin = {};
	
	mollify.plugin.Core = function() {
		var that = this;
								
		return {
			id: "plugin-core",
			itemContextHandler : function(item, ctx, data) {
				var root = item.id == item.root_id;
				var writable = !root && ctx.details.permission == "RW";
//								boolean root = !item.isFile()
//										&& ((JsFolder) item.cast()).isRoot();
//								boolean writable = !root
//										&& details.getFilePermission()
//												.canWrite();
				var actions = [];
				
				if (item.is_file ) {
					actions.push({ 'title-key': 'actionDownloadItem', callback: function() { mollify.ui.download(mollify.filesystem.getDownloadUrl(item)); } });
					actions.push({ title: '-' });
				}
				
				actions.push({ 'title-key': 'actionCopyItem', callback: function() { mollify.filesystem.copy(item); }});
				
				if (writable) {
					actions.push({ 'title-key': 'actionCopyItemHere', callback: function() { mollify.filesystem.copyHere(item); } });
					actions.push({ 'title-key': 'actionMoveItem', callback: function() { mollify.filesystem.move(item); } });
					actions.push({ 'title-key': 'actionRenameItem', callback: function() { mollify.filesystem.rename(item); } });
					actions.push({ 'title-key': 'actionDeleteItem', callback: function() { mollify.ui.dialogs.confirmation({
						title: item.is_file ? mollify.ui.texts.get("deleteFileConfirmationDialogTitle") : mollify.ui.texts.get("deleteFolderConfirmationDialogTitle"),
						message: mollify.ui.texts.get(item.is_file ? "confirmFileDeleteMessage" : "confirmFolderDeleteMessage", [item.name]),
						callback: function() { mollify.filesystem.del(item); }
					});}});
				}
				return {
					actions: actions
				};
			},
			itemCollectionHandler : function(items) {
				return {
					actions: [
						{ 'title-key': 'actionCopyMultiple', callback: function() { mollify.filesystem.copy(items); } },
						{ 'title-key': 'actionMoveMultiple', callback: function() { mollify.filesystem.move(items); } },
						{ 'title-key': 'actionDeleteMultiple', callback: function() { mollify.filesystem.del(items); } }
					]
				};
			}
		};
	}

	/**
	/* Item details plugin
	/**/
	mollify.plugin.ItemDetailsPlugin = function(conf, sp) {
		var that = this;
		that.formatters = {};
		that.typeConfs = false;
		
		this.initialize = function() {
			that.fileSizeFormatter = new mollify.ui.formatters.ByteSize(new mollify.ui.formatters.Number(2, mollify.ui.texts.get('decimalSeparator')));
			that.timestampFormatter = new mollify.ui.formatters.Timestamp(mollify.ui.texts.get('shortDateTimeFormat'));
			/*if (sp) {
				for (var i=0; i<sp.length;i++)
					that.addDetailsSpec(sp[i]);
			}*/
			if (conf) {
				that.typeConfs = {};
				
				for (var t in conf) {
					var parts = t.split(",");
					var c = conf[t];
					for (var i=0; i < parts.length; i++) {
						var p = parts[i].trim();
						if (p.length > 0)
							that.typeConfs[p] = c;
					}
				}
			}
		};
		
		/*this.addDetailsSpec = function(s) {
			if (!s || !s.key) return;
			that.specs[s.key] = s;
		}*/
		
		this.getApplicableSpec = function(item) {
			var ext = (item.is_file && item.extension) ? item.extension.toLowerCase().trim() : "";
			if (ext.length === 0 || !that.typeConfs[ext]) {
				ext = item.is_file ? "[file]" : "[folder]";
				if (!that.typeConfs[ext])
					return that.typeConfs["*"];
			}
			return that.typeConfs[ext];
		}
		
		this.renderItemContextDetails = function(el, item, $content, data) {
			$content.addClass("loading");
			mollify.templates.load("itemdetails-content", mollify.helpers.noncachedUrl(mollify.plugins.url("ItemDetails", "content.html")), function() {
				$content.removeClass("loading");
				that.renderItemDetails(el, item, {element: $content.empty(), data: data});
			});
		};
		
		this.renderItemDetails = function(el, item, o) {
			var s = that.getApplicableSpec(item);
			var groups = that.getGroups(s, o.data);
			
			var result = [];
			for (var i=0,j=groups.length; i<j; i++) {
				var g = groups[i];
				result.push({
					key: g,
					title: that.getGroupTitle(g),
					rows: that.getGroupRows(g, s, o.data)
				});
			}
			
			/*var data = [];
			for (var k in s) {
				var rowSpec = s[k];
				var rowData = o.data[k];
				if (!rowData) continue;
				
				data.push({key:k, title:that.getTitle(k, rowSpec), value: that.formatData(k, rowData)});
			}*/
			$("#itemdetails-template").tmpl({groups: result}).appendTo(o.element);
		};
		
		this.getGroups = function(s, d) {
			var groups = [];
			for (var k in s) {
				var spec = s[k];
				var data = d[k];
				if (!data) continue;
				
				var g = 'file';
				if (k == 'exif' || that.formatters[k]) g = k;
				
				if (groups.indexOf(g) < 0)
					groups.push(g);
			}
			return groups;
		};
		
		this.getGroupTitle = function(g) {				
			if (that.formatters[g]) {
				var f = that.formatters[g];
				if (f.groupTitle) return f.groupTitle;
				if (f["group-title-key"]) return mollify.ui.texts.get(f["group-title-key"]);
			}
			if (g == 'file') return mollify.ui.texts.get('fileItemDetailsGroupFile');
			if (g == 'exif') return mollify.ui.texts.get('fileItemDetailsGroupExif');
			return '';
		};
		
		this.getGroupRows = function(g, s, d) {
			if (that.formatters[g])
				return that.formatters[g].getGroupRows(s[g], d[g]);
			if (g == 'exif') return that.getExifRows(s[g], d[g]);
			
			// file group rows
			var rows = [];
			for (var k in s) {
				if (k == 'exif' || that.formatters[k]) continue;
				var spec = s[k];

				var rowData = d[k];
				if (!rowData) continue;
				
				rows.push({
					title: that.getFileRowTitle(k, s[k]),
					value: that.formatFileData(k, rowData)
				});
			}
			return rows;
		};
		
		this.getFileRowTitle = function(dataKey, rowSpec) {
			if (rowSpec.title) return rowSpec.title;
			if (rowSpec["title-key"]) return mollify.ui.texts.get(rowSpec["title-key"]);
	
			if (dataKey == 'name') return mollify.ui.texts.get('fileItemContextDataName');
			if (dataKey == 'size') return mollify.ui.texts.get('fileItemContextDataSize');
			if (dataKey == 'path') return mollify.ui.texts.get('fileItemContextDataPath');
			if (dataKey == 'extension') return mollify.ui.texts.get('fileItemContextDataExtension');
			if (dataKey == 'last-modified') return mollify.ui.texts.get('fileItemContextDataLastModified');
			if (dataKey == 'image-size') return mollify.ui.texts.get('fileItemContextDataImageSize');
			
			/*if (that.specs[dataKey]) {
				var spec = that.specs[dataKey];
				if (spec.title) return spec.title;
				if (spec["title-key"]) return mollify.ui.texts.get(spec["title-key"]);
			}*/
			return dataKey;
		};
		
		this.formatFileData = function(key, data) {
			if (key == 'size') return that.fileSizeFormatter.format(data);
			if (key == 'last-modified') return that.timestampFormatter.format(mollify.helpers.parseInternalTime(data));
			if (key == 'image-size') return mollify.ui.texts.get('fileItemContextDataImageSizePixels', [data]);
			
			if (that.specs[key]) {
				var spec = that.specs[key];
				if (spec.formatter) return spec.formatter(data);
			}
	
			return data;
		};
		
		this.getExifRows = function(spec, data) {
			var rows = [];
			for (var section in data) {				
				var html = '';
				var first = true;
				var count = 0;
				for (var key in data[section]) {
					var v = that.formatExifValue(section, key, data[section][key]);
					if (!v) continue;
					
					html += '<tr id="exif-row-'+section+'-'+key+'" class="'+(first?'exif-row-section-first':'exif-row')+'"><td class="exif-key">'+key+'</td><td class="exif-value">'+v+'</td></tr>';
					first = false;
					count++;
				}
				
				if (count > 0)
					rows.push({title: section, value: '<table class="exif-section-'+section+'">'+html+"</table>"});
			}
			return rows;
		};
		
		this.formatExifValue = function(section, key, value) {
			if (section == 'FILE' && key == 'SectionsFound') return false;
			//TODO format values?
			return value;
		};

		return {
			id: "plugin-itemdetails",
			initialize: that.initialize,
			itemContextRequestData : function(item) {
				if (!that.typeConfs) return false;
				var spec = that.getApplicableSpec(item);
				if (!spec) return false;
				
				var data = [];
				for (var k in spec)
					data.push(k);
				return data;
			},
			itemContextHandler : function(item, ctx, data) {
				if (!data || !that.typeConfs) return false;
				var spec = that.getApplicableSpec(item);
				if (!spec) return false;
				
				return {
					details: {
						"title-key": "pluginItemDetailsContextTitle",
						"on-render": function(el, $content) {
							that.renderItemContextDetails(el, item, $content, data);
						}
					}
				};
			}
		};
	}
	
	/**
	*	Item collection plugin
	**/
	mollify.plugin.ItemCollectionPlugin = function() {
		var that = this;
		
		this.initialize = function() {
		};
								
		return {
			id: "plugin-itemcollection",
			initialize: that.initialize,
			itemCollectionHandler : function(items) {
				return {
					actions: [
					]
				};
			}
		};
	}
	
	/**
	*	Archiver plugin
	**/
	mollify.plugin.ArchiverPlugin = function() {
		var that = this;
		
		this.initialize = function() {
		};
		
		this.onCompress = function(i, f) {
			if (!i) return;
			
			var defaultName = '';
			var item = false;
			var items = [];
			if (!window.isArray(i)) {
				item = i;
				items.push(item);
			} else if (i.length == 1) {
				item = i[0];
				items = i;
			}
			
			if (item) defaultName = item.name + ".zip";
			
			mollify.ui.dialogs.input({
				title: mollify.ui.texts.get('pluginArchiverCompressDialogTitle'),
				message: mollify.ui.texts.get('pluginArchiverCompressDialogMessage'),
				defaultValue: defaultName,
				yesTitle: mollify.ui.texts.get('pluginArchiverCompressDialogAction'),
				noTitle: mollify.ui.texts.get('dialogCancel'),
				handler: {
					isAcceptable: function(n) { return (!!n && n.length > 0 && (!item || n != item.name)); },
					onInput: function(n) { that._onCompress(items, f, n); }
				}
			});	
		};
		
		this._onCompress = function(items, folder, name) {
			mollify.service.post("archiver/compress", {items : items, folder: folder, name:name}, function(r) {
				mollify.events.dispatch('archiver/compress', { items: items, folder: folder, name: name });
				mollify.events.dispatch('filesystem/update', { folder: folder });
			});
		};
								
		return {
			id: "plugin-archiver",
			initialize: that.initialize,
			itemContextHandler : function(item, ctx, data) {
				var root = (item.id == item.root_id);
				if (root) return false;
				
				var actions = [
					{"title-key":"pluginArchiverDownloadCompressed", callback: function() { that.onDownloadCompressed(item) } }
				];
				if (ctx.folder)	actions.push({"title-key":"pluginArchiverCompress", callback: function() { that.onCompress(item, ctx.folder); } });
				return {
					actions: actions
				};
			},
			itemCollectionHandler : function(items, ctx) {
				return {
					actions: [
						{"title-key":"pluginArchiverCompress", callback: function() { that.onCompress(items) } },
						{"title-key":"pluginArchiverDownloadCompressed", callback: function() { that.onDownloadCompressed(items) } }
					]
				};
			}
		};
	}
	
	/**
	/* File viewer editor plugin
	/**/
	mollify.plugin.FileViewerEditorPlugin = function() {
		var that = this;
		
		this.initialize = function() {
		};
		
		this.onEdit = function(item, spec) {
			mollify.ui.dialogs.custom({
				resizable: true,
				initSize: [600, 400],
				title: mollify.ui.texts.get('fileViewerEditorViewEditDialogTitle'),
				content: '<div class="fileviewereditor-editor-content"></div>',
				buttons: [
					{ id: "yes", "title": mollify.ui.texts.get('dialogSave') },
					{ id: "no", "title": mollify.ui.texts.get('dialogCancel') }
				],
				"on-button": function(btn, d) {
					if (btn.id == 'no') {
						d.close();
						return;
					}
					document.getElementById('editor-frame').contentWindow.onEditorSave(function() {
						d.close();
						//TODO dispatch changed event
					}, function(c, er) {
						d.close();
						alert("error "+c+" " + er);							
					});
				},
				"on-show": function(h, $d) {						
					var $content = $d.find(".fileviewereditor-editor-content");
					var $frm = $('<iframe id="editor-frame" width=\"100%\" height:\"100%\" style=\"width:100%;height:100%;border: none;overflow: none;\" />').attr('src', spec.embedded);
					$content.removeClass("loading").append($frm);
					h.center();
				}
			});
		};
			
		this.onView = function(item, all, spec) {
			var loaded = {};
			var list = [{
				embedded: spec.view.embedded,
				full: spec.view.full,
				edit: !!spec.edit,
				item: item
			}];
			var init = list[0];
			var visible = false;
			init.init = true;
			var activeItem = false;
			
			var $lb;
			var $lbc;
			var $i = false;
			var maxW;
			var maxH;
			var resize = function() {
				maxW = ($(window).width()-100);
				maxH = ($(window).height()-100);
				$lbc.css({
					"max-width": maxW+"px",
					"max-height": maxH+"px"
				});
				if ($i) {
					$i.css({
						"max-width": maxW+"px",
						"max-height": maxH+"px"
					});
				}
				$lb.lightbox('center');
			};
			$(window).resize(resize);
			var load = function(itm) {
				var id = itm.item.id;
				activeItem = itm;
				
				if (loaded[id]) return;
				$.ajax({
					type: 'GET',
					url: itm.embedded
				}).done(function(data) {
					loaded[id] = true;
					
					$i = $("#mollify-fileviewereditor-viewer-item-"+id);
					var $ic = $i.find(".mollify-fileviewereditor-viewer-item-content");
					$ic.removeClass("loading").html(data.result.html);
					if (data.result.size) {
						var sp = data.result.size.split(';');
						$("#"+data.result["resized-element-id"]).css({
							"width": sp[0]+"px",
							"height": sp[1]+"px"
						});
					}
					
					// if img, wait until it is loaded
					var $img = $ic.find('img:first');
					if ($img.length > 0) {
						$img.one('load', function() {
							var w = $img.width();
							if (!data.result.size && w > 0)
								$img.css({
									"width": w+"px",
									"height": $img.height()+"px"
								});
							resize();
						});
					} else {
						resize();
					}
					
					if (!visible) {
						$lb.lightbox('show');
						visible = true;
					}
				});
			};
			
			var $v = mollify.dom.template("mollify-tmpl-fileviewereditor-popup", {
				items : list
			}, {
				content: function(i) {
					return i.content;
				}
			}).appendTo($("body"));
			
			var onHide = function() {
				$v.remove();
			};
			
			$lb = $v.lightbox({backdrop: true, resizeToFit: false, show: false, onHide: onHide});
			mollify.ui.process($lb, ["localize"]);
			
			$lb.find("button.close").click(function(){
				$lb.lightbox('hide');
			});
			$lbc = $lb.find(".carousel-inner");
			
			var $c = $v.find(".carousel").carousel({interval: false}).on('slid', function() {
				alert("slid");
				var $active = $v.find(".mollify-fileviewereditor-viewer-item.active");
				load($active.tmplItem().data);
			});
			$c.find(".carousel-control").click(function() {
				if ($(this).hasClass("left")) $c.carousel('prev');
				else $c.carousel('next');
			});
			var $tools = $c.find(".mollify-fileviewereditor-viewer-tools");
			$tools.find(".mollify-fileviewereditor-viewer-item-viewinnewwindow").click(function(){
				$lb.lightbox('hide');
				mollify.ui.window.open(activeItem.full);
			});
			$tools.find(".mollify-fileviewereditor-viewer-item-edit").click(function(){
				$lb.lightbox('hide');
				that.onEdit(item, spec.edit);	//TODO activeItem
			});
			load(init);
		};
					
		return {
			id: "plugin-fileviewereditor",
			initialize: that.initialize,
			itemContextHandler : function(item, ctx, data) {
				if (!data) return false;
				
				var previewerAvailable = !!data.preview;
				var viewerAvailable = !!data.view;
				var editorAvailable = !!data.edit;
				
				var result = {
					details : false,
					actions: []
				};
				if (previewerAvailable) {
					result.details = {
						"title-key": "pluginFileViewerEditorPreview",
						"on-render": function(el, $content) {
							$content.empty().addClass("loading");
							
							$.ajax({
								type: 'GET',
								url: data.preview
							}).done(function(r) {
								$content.removeClass("loading").html(r.result.html);
							});
						}
					};
				}

				if (viewerAvailable) {
					result.actions.push(
						{ id: 'pluginFileViewerEditorView', "title-key": 'pluginFileViewerEditorView', type:"primary", callback: function() {
							that.onView(item, [], data);
						}}
					);
				}
				if (editorAvailable) {
					result.actions.push(
						{ id: 'pluginFileViewerEditorView', "title-key": 'pluginFileViewerEditorEdit', type:"primary", callback: function() {
							that.onEdit(item, data.edit);
						}}
					);
				}
				return result;
			}
		};
	};
	
	/**
	*	Comment plugin
	**/
	mollify.plugin.CommentPlugin = function() {
		var that = this;
		
		this.initialize = function() {
			that._timestampFormatter = new mollify.ui.formatters.Timestamp(mollify.ui.texts.get('shortDateTimeFormat'));
			mollify.dom.importCss(mollify.plugins.url("Comment", "style.css"));
			mollify.dom.importScript(mollify.plugins.url("Comment", "texts_" + mollify.ui.texts.locale + ".js"));
		};
		
		this.getListCellContent = function(item, data) {
			if (!item.id || item.id.length === 0 || !data || !data["plugin-comment-count"]) return "";
			var counts = data["plugin-comment-count"];
	
			if (!counts[item.id])
				return "<div id='item-comment-count-"+item.id+"' class='filelist-item-comment-count-none'></div>";
			
			return "<div id='item-comment-count-"+item.id+"' class='filelist-item-comment-count'>"+counts[item.id]+"</div>";
		};
		
		this.renderItemContextDetails = function(el, item, $content, data) {
			$content.addClass("loading");
			mollify.templates.load("comments-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Comment", "content.html")), function() {
				$content.removeClass("loading");
				if (data.count === 0) {
					that.renderItemContextComments(el, item, [], {element: $content.empty(), contentTemplate: 'comments-template'});
				} else {
					that.loadComments(item, function(item, comments) {
						that.renderItemContextComments(el, item, comments, {element: $content.empty(), contentTemplate: 'comments-template'});
					});
				}
			});
		};
		
		this.renderItemContextComments = function(el, item, comments, o) {
			mollify.dom.template(o.contentTemplate, item).appendTo(o.element);
			
			$("#comments-dialog-add").click(function() {
				var comment = $("#comments-dialog-add-text").val();
				if (!comment || comment.length === 0) return;
				that.onAddComment(item, comment, el.close);
			} );
			
			that.updateComments($("#comments-list"), item, comments);
		};
		
		this.showCommentsBubble = function(item, e) {
			var bubble = mollify.ui.controls.dynamicBubble({element:e, title: item.name});
			
			mollify.templates.load("comments-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Comment", "content.html")), function() {
				bubble.content(mollify.dom.template("comments-template", item));
		
				$("#comments-dialog-add").click(function() { 
					var comment = $("#comments-dialog-add-text").val();
					if (!comment || comment.length === 0) return;
					that.onAddComment(item, comment, bubble.close);
				});
				that.loadComments(item, function(item, comments) {
					that.updateComments($("#comments-list"), item, comments);
				});
			});
		};
		
		this.loadComments = function(item, cb) {
			mollify.service.get("comment/"+item.id, function(comments) {
				cb(item, that.processComments(comments));
			}, function(code, error) {
				alert(error);
			});
		};
		
		this.processComments = function(comments) {
			var userId = mollify.session.user_id;
			var isAdmin = mollify.session.admin;
			
			for (var i=0,j=comments.length; i<j; i++) {
				comments[i].time = that._timestampFormatter.format(mollify.helpers.parseInternalTime(comments[i].time));
				comments[i].comment = comments[i].comment.replace(new RegExp('\n', 'g'), '<br/>');
				comments[i].remove = isAdmin || (userId == comments[i].user_id);
			}
			return comments;
		};
		
		this.onAddComment = function(item, comment, cb) {
			mollify.service.post("comment/"+item.id, { comment: comment }, function(result) {
				that.updateCommentCount(item, result.count);
				if (cb) cb();
			},	function(code, error) {
				alert(error);
			});
		};
		
		this.onRemoveComment = function($list, item, id) {		
			mollify.service.del("comment/"+item.id+"/"+id, function(result) {
				that.updateCommentCount(item, result.length);
				that.updateComments($list, item, that.processComments(result));
			},	function(code, error) {
				alert(error);
			});
		};
		
		this.updateCommentCount = function(item, count) {
			var e = document.getElementById("item-comment-count-"+item.id);
			if (!e) return;
			
			if (count < 1) {
				e.innerHTML = '';
				e.setAttribute('class', 'filelist-item-comment-count-none');
			} else {
				e.innerHTML = count;
				e.setAttribute('class', 'filelist-item-comment-count');
			}
		};
		
		this.updateComments = function($list, item, comments) {
			$list.removeClass("loading");
			
			if (comments.length === 0) {
				$list.html("<span class='message'>"+mollify.ui.texts.get("commentsDialogNoComments")+"</span>");
				return;
			}
	
			mollify.dom.template("comment-template", comments).appendTo($list.empty());
			$list.find(".comment-content").hover(
				function () { $(this).addClass("hover"); }, 
				function () { $(this).removeClass("hover"); }
			);
			$list.find(".comment-remove-action").click(function(e) {
				e.preventDefault();
				var comment = $(this).tmplItem().data;
				that.onRemoveComment($list, item, comment.id);
			});
		};
		
		return {
			id: "plugin-comment",
			initialize: that.initialize,
			mainViewHandler : {
				filelistColumns : function() {
					return [{
						"id": "comment-count",
						"request-id": "plugin-comment-count",
						"title-key": "",
						"sort": function(i1, i2, sort, data) {
							if (!i1.is_file && !i2.is_file) return 0;
							if (!data || !data["core-file-modified"]) return 0;
							
							var ts1 = data["core-file-modified"][i1.id] ? data["core-file-modified"][i1.id] * 1 : 0;
							var ts2 = data["core-file-modified"][i2.id] ? data["core-file-modified"][i2.id] * 1 : 0;
							return ((ts1 > ts2) ? 1 : -1) * sort;
						},
						"content": that.getListCellContent,
						"request": function(parent) { return {}; },
						"on-click": function(item) {
							that.showCommentsBubble(item, $("#item-comment-count-"+item.id));
						}
					}];
				}
			},
			itemContextHandler : function(item, ctx, data) {
				return {
					details: {
						"title-key": "pluginCommentContextTitle",
						"on-render": function(el, $content) { that.renderItemContextDetails(el, item, $content, data); }
					}
				};
			}
		};
	}

	/**
	*	Permission plugin
	**/
	mollify.plugin.PermissionsPlugin = function() {
		var that = this;
		
		this.initialize = function() {
			that.permissionOptions = [
				{ title: mollify.ui.texts.get('pluginPermissionsValueRW'), value: "rw"},
				{ title: mollify.ui.texts.get('pluginPermissionsValueRO'), value: "ro"},
				{ title: mollify.ui.texts.get('pluginPermissionsValueN'), value: "n"}
			];
			that.permissionOptionsByKey = {};
			for (var i=0,j=that.permissionOptions.length; i<j; i++) { var p = that.permissionOptions[i]; that.permissionOptionsByKey[p.value] = p; }
		
			//mollify.dom.importCss(mollify.plugins.url("Comment", "style.css"));
			//mollify.dom.importScript(mollify.plugins.url("Comment", "texts_" + mollify.ui.texts.locale + ".js"));
		};
		
		this.renderItemContextDetails = function(el, item, $content, data) {
			$content.addClass("loading");
			mollify.templates.load("comments-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Comment", "content.html")), function() {
				$content.removeClass("loading");
				if (data.count === 0) {
					that.renderItemContextComments(el, item, [], {element: $content.empty(), contentTemplate: 'comments-template'});
				} else {
					that.loadComments(item, function(item, comments) {
						that.renderItemContextComments(el, item, comments, {element: $content.empty(), contentTemplate: 'comments-template'});
					});
				}
			});
		};
		
		that.onOpenPermissions = function(item) {
			var permissionData = {
				"new": [],
				"modified": [],
				"removed": []
			};
			var $content = false;
			
			mollify.ui.dialogs.custom({
				resizable: true,
				initSize: [600, 400],
				title: mollify.ui.texts.get('pluginPermissionsEditDialogTitle'),
				content: mollify.dom.template("mollify-tmpl-permission-editor", {item: item}),
				buttons: [
					{ id: "yes", "title": mollify.ui.texts.get('dialogSave') },
					{ id: "no", "title": mollify.ui.texts.get('dialogCancel') }
				],
				"on-button": function(btn, d) {
					if (btn.id == 'no') {
						d.close();
						return;
					}
					if (permissionData["new"].length === 0 && permissionData.modified.length === 0 && permissionData.removed.length === 0)
						return;
					
					$content.addClass("loading");
					mollify.service.put("filesystem/permissions", permissionData, function(r) {
						d.close();
					}, function(code, error) {
						d.close();
						mollify.ui.dialogs.error({
							message: code + " " + error
						});
					});
				},
				"on-show": function(h, $d) {
					$content = $d.find("#mollify-pluginpermissions-editor-content");
					$("#mollify-pluginpermissions-editor-change-item").click(function(e) {
						e.preventDefault();
						return false;
					});

					h.center();
					
					that.loadPermissions(item, function(permissions, userData) {
						$content.removeClass("loading");
						that.initEditor(item, permissions, userData, permissionData);
					}, function(c, e) {
						$d.close();
						mollify.ui.dialogs.error({
							message: c + " " + e
						});
					});
				}
			});
		};
		
		this.processUserData = function(l) {
			var userData = {
				users : [],
				groups : [],
				usersById : {}
			};
			for (var i=0,j=l.length; i<j; i++) {
				var u = l[i];
				if (u.is_group == "0") {
					userData.users.push(u);
					userData.usersById[u.id] = u;
				} else {
					userData.groups.push(u);
					userData.usersById[u.id] = u;
				}
			}
			return userData;
		};
		
		this.loadPermissions = function(item, cb, err) {
			mollify.service.get("filesystem/"+item.id+"/permissions?u=1", function(r) {
				cb(r.permissions, that.processUserData(r.users));
			}, function(code, error) {
				err(code, error);
			});
		};
		
		this.initEditor = function(item, permissions, userData, permissionData) {
			var $list;
			
			var isGroup = function(id) { return (userData.usersById[id].is_group != "0"); };
			var onAddOrUpdate = function(user, permissionVal) {
				var userVal = $list.findByKey(user.id);
				if (userVal) {
					if (!userVal.isnew) permissionData.modified.push(userVal);
					userVal.permission = permissionVal;
					$list.update(userVal);
				} else {
					// if previously deleted, move it to modified
					for (var i=0,j=permissionData.removed.length; i<j; i++) {
						var d = permissionData.removed[i];
						if (d.user_id == user.id) {
							permissionData.removed.remove(i);
							permissionData.modified.push(d);
							d.permission = permissionVal;
							$list.add(d);
							return;
						}
					}

					// not modified or deleted => create new
					var p = {"user_id": user.id, "item_id": item.id, permission: permissionVal, isnew: true };
					permissionData["new"].push(p);
					$list.add(p);
				}					
			};
			var onRemove = function(permission) {
				if (!permission.isnew) permissionData.removed.push(permission);
			};
			var onEdit = function(permission) {
				if (!permission.isnew) permissionData.modified.push(permission);
			};
			
			$list = mollify.ui.controls.table("mollify-pluginpermissions-editor-permission-list", {
				key: "user_id",
				onRow: function($r, i) { if (isGroup(i.user_id)) $r.addClass("group"); },
				columns: [
					{ id: "user_id", title: mollify.ui.texts.get('pluginPermissionsEditColUser'), renderer: function(i, v, $c){ $c.html(userData.usersById[v].name).addClass("user"); } },
					{ id: "permission", title: mollify.ui.texts.get('pluginPermissionsEditColPermission'), renderer: function(i, v, $c){
						if (!$c[0].ctrl) {
							var $s = mollify.ui.controls.select($("<select></select>").appendTo($c.addClass("permission")), {
								values: that.permissionOptions,
								title : "title",
								onChange: function(v) {
									i.permission = v.value;
									onEdit(i);
								}
							});
							$c[0].ctrl = $s;
						}
						$c[0].ctrl.select(that.permissionOptionsByKey[v]);
					}},
					{ id: "remove", title: "", renderer: function(i, v, $c){ $c.append(mollify.dom.template("mollify-tmpl-permission-editor-listremove")); }}
				]
			});
			$("#mollify-pluginpermissions-editor-permission-list").delegate("a.remove-link", "click", function() {
				var permission = $(this).parent().parent()[0].data;
				onRemove(permission);
				$list.remove(permission);
			});
			
			$list.add(permissions);
			var $newUser = mollify.ui.controls.select("mollify-pluginpermissions-editor-new-user", {
				none: {title: mollify.ui.texts.get('pluginPermissionsEditNoUser')},
				title : "name",
				onCreate : function($o, i) { if (isGroup(i.id)) $o.addClass("group"); }
			});
			$newUser.add(userData.users);
			$newUser.add(userData.groups);
			
			var $newPermission = mollify.ui.controls.select("mollify-pluginpermissions-editor-new-permission", {
				values: that.permissionOptions,
				none: {title: mollify.ui.texts.get('pluginPermissionsEditNoPermission')},
				title : "title"
			});
			
			var resetNew = function() {
				$newUser.select(false);
				$newPermission.select(false);
			};
			resetNew();
			
			$("#mollify-pluginpermissions-editor-new-add").click(function() {
				var selectedUser = $newUser.selected();
				if (!selectedUser) return;
				var selectedPermission = $newPermission.selected();
				if (!selectedPermission) return;
				
				onAddOrUpdate(selectedUser, selectedPermission.value);
				resetNew();
			});
		};
		
		this.renderItemContextDetails = function(el, item, $content) {
			mollify.dom.template("mollify-tmpl-permission-context").appendTo($content);
			mollify.ui.process($content, ["localize"]);
			
			that.loadPermissions(item, function(permissions, userData) {
				$("#mollify-pluginpermissions-context-content").removeClass("loading");
				
				var $list = mollify.ui.controls.table("mollify-pluginpermissions-context-permission-list", {
					key: "user_id",
					columns: [
						{ id: "user_id", title: mollify.ui.texts.get('pluginPermissionsEditColUser'), renderer: function(i, v, $c){ $c.html(userData.usersById[v].name).addClass("user"); } },
						{ id: "permission", title: mollify.ui.texts.get('pluginPermissionsEditColPermission'), renderer: function(i, v, $c){
							$c.html(that.permissionOptionsByKey[v].title);
						}}
					]
				});
				$list.add(permissions);
				$("#mollify-pluginpermissions-context-edit").click(function(){
					el.close();
					that.onOpenPermissions(item);
				});
			}, function(c, e) {
				el.close();
				mollify.ui.dialogs.error({
					message: c + " " + e
				});
			});
		};
					
		return {
			id: "plugin-permissions",
			initialize: that.initialize,
			itemContextHandler : function(item, ctx, data) {
				if (!mollify.session.admin) return false;
				
				return {
					details: {
						"title-key": "pluginPermissionsContextTitle",
						"on-render": function(el, $content) {
							that.renderItemContextDetails(el, item, $content);
						}
					},
					actions: [
						{ id: 'pluginPermissions', 'title-key': 'pluginPermissionsAction', callback: function() { that.onOpenPermissions(item); } }
					]
				};
			}
		};
	}

	/**
	*	Dropbox plugin
	**/
	mollify.plugin.DropboxPlugin = function() {
		var that = this;
		that.w = 0;
		that.$dbE = false;
		that.items = [];
		
		this.initialize = function() {
			that.itemContext = new mollify.ui.itemContext({ onDescription: null });
		};
		
		this.onMainViewRender = function($container) {
			mollify.dom.template("mollify-tmpl-mainview-dropbox").appendTo($container);
			$("#mollify-dropbox-handle").click(function() {
				that.openDropbox();
			});
			
			that.$dbE = $("#mollify-dropbox");
			that.w = $("#mollify-dropbox-content").outerWidth();
			
			var onResize = function() {
				var y = $("#mollify-mainview-header").height();
				that.$dbE.css("top", y+"px").height($(window).height()-y);
			};
			$(window).resize(onResize);
			onResize();
			
			if (mollify.ui.draganddrop) {
				mollify.ui.draganddrop.enableDrop($("#mollify-dropbox-list"), {
					canDrop : function($e, e, obj) {
						if (!obj || obj.type != 'filesystemitem') return false;
						var item = obj.payload;
						return (that.items.indexOf(item) < 0);
					},
					dropType : function($e, e, obj) {
						if (!obj || obj.type != 'filesystemitem') return false;
						return "copy";
					},
					onDrop : function($e, e, obj) {
						if (!obj || obj.type != 'filesystemitem') return;
						var item = obj.payload;
						that.onAddItem(item);
					}
				});
			}
			
			var ab = mollify.ui.controls.dropdown({
				element: $("#mollify-dropbox-actions"),
				container: $("body"),
				hideDelay: 0,
				dynamic: true,
				onShow: function(drp, items) {			
					that.getActions(function(a) {
						if (!a) {
							drp.hide();
							return;
						}
						drp.items(a);
					});
				},
				onItem: function() {
					that.emptyDropbox();
				},
				onBlur: function(dd) {
					
				}
			});
			that.openDropbox(false);
		};
		
		this.getActions = function(cb) {				
			if (that.items.length === 0) {
				cb([]);
				return;
			}
			var plugins = mollify.plugins.getItemCollectionPlugins(that.items);
			
			var actions = mollify.helpers.getPluginActions(plugins);
			actions.push({title:"-"});
			actions.push({"title-key":"dropboxEmpty"});
			cb(mollify.helpers.cleanupActions(actions));
		};
		
		this.openDropbox = function(o) {
			var open = that.$dbE.hasClass("opened");
			if (window.def(o)) {
				if (o == open) return;
			} else {
				o = !open;
			}
			
			if (!o) that.$dbE.removeClass("opened").addClass("closed").animate({"width": "0"}, 300);
			else that.$dbE.addClass("opened").removeClass("closed").animate({"width": that.w+""}, 300);
		};
		
		this.emptyDropbox = function() {
			that.items = [];
			that.refreshList();
		};
		
		this.onAddItem = function(item) {
			if (that.items.indexOf(item) >= 0) return;
			that.items.push(item);
			that.refreshList();
		};
		
		this.refreshList = function() {
			$("#mollify-dropbox-list").empty().append(mollify.dom.template("mollify-tmpl-mainview-dropbox-item", that.items));
			$("#mollify-dropbox-list .mollify-dropbox-list-item").click(function(e) {
				e.preventDefault();
				e.stopPropagation();
				var $i = $(this);
				var item = $i.tmplItem().data;
				$i.tooltip('hide');
				that.itemContext.open({
					item: item,
					element: $i,
					container: $("#mollify"),
					viewport: $("#mollify")
				});
				return false;
			}).each(function() {
				var $i = $(this);
				var item = $i.tmplItem().data;
				$i.tooltip({
					placement: "bottom",
					html: true,
					title: mollify.filesystem.rootsById[item.root_id].name + (item.path.length > 0 ? ":&nbsp;" + item.path : ""),
					trigger: "hover"
				});
			});
			$("#mollify-dropbox-list .mollify-dropbox-list-item > a.item-remove").click(function() {
				var $t = $(this);
				that.items.remove($t.tmplItem().data);
				that.refreshList();
			});
		};
					
		return {
			id: "plugin-dropbox",
			initialize: that.initialize,
			mainViewHandler : {
				onMainViewRender: that.onMainViewRender
			},
			itemContextHandler : function(item, ctx, data) {
				return {
					actions: [
						{ id: 'pluginDropbox', 'title-key': 'pluginDropboxAddTo', callback: function() { that.onAddItem(item); that.openDropbox(true); } }
					]
				};
			}
		};
	}

}(window.jQuery, window.mollify);