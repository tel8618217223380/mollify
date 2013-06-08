!function($, mollify) {

	"use strict"; // jshint ;_;
	
	mollify.plugin = {};
	
	mollify.plugin.Core = function() {
		var that = this;
								
		return {
			id: "plugin-core",
			itemContextHandler : function(item, ctx, data) {
				var root = item.id == item.root_id;
				var writable = !root && ctx.details.permission.toUpperCase() == "RW";
				var parentWritable = !root && ctx.details.parent_permission.toUpperCase() == "RW";

				var actions = [];				
				if (item.is_file ) {
					actions.push({ 'title-key': 'actionDownloadItem', type:"primary", group:"download", callback: function() { mollify.ui.download(mollify.filesystem.getDownloadUrl(item)); } });
					actions.push({ title: '-' });
				}
				
				actions.push({ 'title-key': 'actionCopyItem', callback: function() { return mollify.filesystem.copy(item); }});
				if (parentWritable)
					actions.push({ 'title-key': 'actionCopyItemHere', callback: function() { return mollify.filesystem.copyHere(item); } });
				
				if (writable) {	
					actions.push({ 'title-key': 'actionMoveItem', callback: function() { return mollify.filesystem.move(item); } });
					actions.push({ 'title-key': 'actionRenameItem', callback: function() { return mollify.filesystem.rename(item); } });
					actions.push({ 'title-key': 'actionDeleteItem', callback: function() { var df = $.Deferred(); mollify.ui.dialogs.confirmation({
						title: item.is_file ? mollify.ui.texts.get("deleteFileConfirmationDialogTitle") : mollify.ui.texts.get("deleteFolderConfirmationDialogTitle"),
						message: mollify.ui.texts.get(item.is_file ? "confirmFileDeleteMessage" : "confirmFolderDeleteMessage", [item.name]),
						callback: function() { $.when(mollify.filesystem.del(item)).then(df.resolve, df.reject); }
					});
					return df.promise(); }});
				}
				return {
					actions: actions
				};
			},
			itemCollectionHandler : function(items) {
				return {
					actions: [
						{ 'title-key': 'actionCopyMultiple', callback: function() { return mollify.filesystem.copy(items); } },
						{ 'title-key': 'actionMoveMultiple', callback: function() { return mollify.filesystem.move(items); } },
						{ 'title-key': 'actionDeleteMultiple', callback: function() { return mollify.filesystem.del(items); } }
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
		
		this.onStore = function(items) {
			var df = $.Deferred();
			mollify.ui.dialogs.input({
				title: mollify.ui.texts.get('pluginItemCollectionStoreDialogTitle'),
				message: mollify.ui.texts.get('pluginItemCollectionStoreDialogMessage'),
				defaultValue: "",
				yesTitle: mollify.ui.texts.get('pluginItemCollectionStoreDialogAction'),
				noTitle: mollify.ui.texts.get('dialogCancel'),
				handler: {
					isAcceptable: function(n) { return (!!n && n.length > 0); },
					onInput: function(n) { $.when(that._onStore(items, n)).then(df.resolve, df.reject); }
				}
			});
			return df.promise();
		};
		
		this._onStore = function(items, name) {
			return mollify.service.post("itemcollections", {items : items, name:name}).done(function(list) {
				//TODO show message
				that._updateNavBar(list);
			});
		};
		
		this.onAddItems = function(ic, items) {
			return mollify.service.post("itemcollections/"+ic.id, {items : isArray(items) ? items: [ items ]});
		};
		
		this._removeCollectionItem = function(ic, items) {
			return mollify.service.del("itemcollections/"+ic.id+"/items", {items : isArray(items) ? items: [ items ]});
		};
		
		this._showCollection = function(ic) {
			that._fileView.changeToFolder({
				type: "ic",
				name: ic.name,
				ic: ic
			});
			that._collectionsNav.setActive(ic);
		};

		this.onFolderDeselect = function(f) {
			that._collectionsNav.setActive(false);
		};

		this.getFolderInfo = function(f) {
			var df = $.Deferred();
			df.resolve({items: f.ic.items});	//TODO get data for items in the collection "service.post(filesystem/info, {items: items})
			return df.promise();
		};

		this.onRenderFolderView = function(f, $h, $tb) {
			mollify.dom.template("mollify-tmpl-fileview-header", {folder: f}).appendTo($h);
			$("#mollify-folder-description").remove();

			var opt = {
				title: function() {
					return this.data.title ? this.data.title : mollify.ui.texts.get(this.data['title-key']);
				}
			};
			var actionsElement = mollify.dom.template("mollify-tmpl-fileview-foldertools-action", { icon: 'icon-cog', dropdown: true }, opt).appendTo($("#mollify-fileview-folder-actions"));
			mollify.ui.controls.dropdown({
				element: actionsElement.find("li"),
				items: that._getItemActions(f.ic),
				hideDelay: 0,
				style: 'submenu'
			});
		};

		this.editCollection = function(ic) {
			mollify.service.get("itemcollections/"+ic.id).done(function(loaded){
				mollify.ui.dialogs.tableView({
					title: mollify.ui.texts.get('pluginItemCollectionsEditDialogTitle', ic.name),
					buttons:[{id:"close", title:mollify.ui.texts.get('dialogClose')},{id:"remove", title:mollify.ui.texts.get("pluginItemCollectionsEditDialogRemove"), type:"secondary", cls:"btn-danger secondary"}],
					onButton: function(btn, h) {
						h.close();
						if (btn.id == 'remove') that.removeCollection(ic);
					},
					table: {
						key: "item_id",
						columns: [
							{ id: "icon", title:"", renderer: function(i, v, $c) {
								$c.html(i.is_file ? '<i class="icon-file"></i>' : '<i class="icon-folder-close-alt"></i>');
							} },
							{ id: "name", title: mollify.ui.texts.get('fileListColumnTitleName') },
							{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
						]
					},
					onTableRowAction: function(d, table, id, item) {
						if (id == "remove") {
							that._removeCollectionItem(ic, item).done(function() {
								table.remove(item);
							});
						}
					},
					onRender: function(d, $c, table) {
						table.set(loaded.items);
						$c.removeClass("loading");
					}
				});
			});
		};

		this._updateNavBar = function(list) {
			that._list = list;
			var navBarItems = [];
			$.each(list, function(i, ic) {
				navBarItems.push({title:ic.name, obj: ic, callback:function(){ that._showCollection(ic); }})
			});
			that._collectionsNav.update(navBarItems);
		}

		this.removeCollection = function(ic) {
			return mollify.service.del("itemcollections/"+ic.id).done(that._updateNavBar);
		};

		this._onShareNavItem = function(ic) {
			if (!mollify.plugins.exists("plugin-share")) return;
			mollify.plugins.get("plugin-share").openShares({ id: "ic_" + ic.name, "name": ic.name, shareTitle: mollify.ui.texts.get("pluginItemCollectionShareTitle") });
		};

		this._getItemActions = function(ic) {
			var items = [
				{"title-key":"pluginItemCollectionsNavEdit", callback: function() { that.editCollection(ic); }},
				{"title-key":"pluginItemCollectionsNavRemove", callback: function() { that.removeCollection(ic); }}
			];
			if (mollify.plugins.exists("plugin-share")) items.push({"title-key":"pluginItemCollectionsNavShare", callback: function() { that._onShareNavItem(ic); }});
			return items;
		}
		
		this._onFileViewRender = function($e, h) {
			that._fileView = h.fileview;
			that._fileView.addCustomFolderType("ic", that);

			that._collectionsNav = h.addNavBar({
				title: mollify.ui.texts.get("pluginItemCollectionsNavTitle"),
				classes: "ic-navbar-item",
				items: [],
				dropdown: {
					items: that._getItemActions
				},
				onRender: mollify.ui.draganddrop ? function($nb, $items, objs) {
					mollify.ui.draganddrop.enableDrop($items, {
						canDrop : function($e, e, obj) {
							if (!obj || obj.type != 'filesystemitem') return false;
							return true;
						},
						dropType : function($e, e, obj) {
							if (!obj || obj.type != 'filesystemitem') return false;
							return "copy";
						},
						onDrop : function($e, e, obj) {
							if (!obj || obj.type != 'filesystemitem') return;
							var item = obj.payload;
							var ic = objs($e);							
							that.onAddItems(ic, item);
						}
					});
				} : false
			});
			mollify.service.get("itemcollections").done(that._updateNavBar);
		};

		return {
			id: "plugin-itemcollection",
			initialize: that.initialize,
			itemCollectionHandler : function(items) {
				return {
					actions: [{
						"title-key": "pluginItemCollectionStore",
						callback: function() { return that.onStore(items); }
					}]
				};
			},
			fileViewHandler : {
				onFileViewRender: that._onFileViewRender
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
			
			var df = $.Deferred();
			mollify.ui.dialogs.input({
				title: mollify.ui.texts.get('pluginArchiverCompressDialogTitle'),
				message: mollify.ui.texts.get('pluginArchiverCompressDialogMessage'),
				defaultValue: defaultName,
				yesTitle: mollify.ui.texts.get('pluginArchiverCompressDialogAction'),
				noTitle: mollify.ui.texts.get('dialogCancel'),
				handler: {
					isAcceptable: function(n) { return (!!n && n.length > 0 && (!item || n != item.name)); },
					onInput: function(n) { $.when(that._onCompress(items, f, n)).then(df.resolve, df.reject); }
				}
			});
			return df.promise();
		};
		
		this.onDownloadCompressed = function(items) {
			//TODO show progress
			return mollify.service.post("archiver/download", {items : items}).done(function(r) {
				//TODO remove progress
				mollify.ui.download(mollify.service.url('archiver/download/'+r.id, true));
			});
		};
		
		this._onCompress = function(items, folder, name) {
			return mollify.service.post("archiver/compress", {items : items, folder: folder, name:name}).done(function(r) {
				mollify.events.dispatch('archiver/compress', { items: items, folder: folder, name: name });
				mollify.events.dispatch('filesystem/update', { folder: folder });
			});
		};
		
		this._onExtract = function(a, folder) {
			return mollify.service.post("archiver/extract", {item : a, folder: folder}).done(function(r) {
				mollify.events.dispatch('archiver/extract', { item: a, folder: folder });
				mollify.events.dispatch('filesystem/update', { folder: folder });
			});
		};
		
		this._isArchive = function(item) {
			if (!item.is_file) return false;
			
			var ext = item.extension.toLowerCase();
			return ext == 'zip';	//TODO get supported extensions from backend
		};
								
		return {
			id: "plugin-archiver",
			initialize: that.initialize,
			getDownloadCompressedUrl : function(item) { return mollify.service.url("archiver/download?item="+item.id, true); },
			itemContextHandler : function(item, ctx, data) {
				var root = (item.id == item.root_id);
				if (root) return false;
				var writable = !root && ctx.details.permission.toUpperCase() == "RW";
				var parentWritable = !root && ctx.details.parent_permission.toUpperCase() == "RW";
				var folderWritable = !root && ctx.folder_permission.toUpperCase() == "RW";

				if (that._isArchive(item)) {
					return {
						actions: [
							{"title-key":"pluginArchiverExtract", callback: function() { return that._onExtract(item) } }
						]
					};
				}
				
				var actions = [
					{"title-key":"pluginArchiverDownloadCompressed", type:"primary", group:"download", callback: function() { that.onDownloadCompressed([item]); } }
				];
				if (ctx.folder && folderWritable) actions.push({"title-key":"pluginArchiverCompress", callback: function() { return that.onCompress(item, ctx.folder); } });
				return {
					actions: actions
				};
			},
			itemCollectionHandler : function(items, ctx) {
				return {
					actions: [
						{"title-key":"pluginArchiverCompress", callback: function() { return that.onCompress(items) } },
						{"title-key":"pluginArchiverDownloadCompressed", type:"primary", group:"download", callback: function() { return that.onDownloadCompressed(items) } }
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
						return true;
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
			mollify.service.get("comment/"+item.id).done(function(comments) {
				cb(item, that.processComments(comments));
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
			mollify.service.post("comment/"+item.id, { comment: comment }).done(function(result) {
				that.updateCommentCount(item, result.count);
				if (cb) cb();
			});
		};
		
		this.onRemoveComment = function($list, item, id) {		
			mollify.service.del("comment/"+item.id+"/"+id).done(function(result) {
				that.updateCommentCount(item, result.length);
				that.updateComments($list, item, that.processComments(result));
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
			fileViewHandler : {
				filelistColumns : function() {
					return [{
						"id": "comment-count",
						"request-id": "plugin-comment-count",
						"title-key": "",
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
			that.permissionOptionsByKey = mollify.helpers.mapByKey(that.permissionOptions, "value");
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
					mollify.service.put("filesystem/permissions", permissionData).done(d.close).fail(d.close);
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
					}).fail(h.close);
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
		
		this.loadPermissions = function(item, cb) {
			return mollify.service.get("filesystem/"+item.id+"/permissions?u=1").done(function(r) {
				cb(r.permissions, that.processUserData(r.users));
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
					{ id: "permission", title: mollify.ui.texts.get('pluginPermissionsEditColPermission'), type: "select", options: that.permissionOptions, valueMapper: function(item, k) { return that.permissionOptionsByKey[k]; }, onChange: function(item, p) {
						item.permission = p.value;
						onEdit(item);
					}, cellClass: "permission" },
					{ id: "remove", title: "", type:"action", content: mollify.dom.template("mollify-tmpl-permission-editor-listremove").html() }
				],
				onRowAction: function(id, permission) { onRemove(permission); $list.remove(permission); }
			});
			/*$("#mollify-pluginpermissions-editor-permission-list").delegate("a.remove-link", "click", function() {
				var permission = $(this).parent().parent()[0].data;
				onRemove(permission);
				$list.remove(permission);
			});*/
			
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
			}).fail(function(e) {
				el.close();
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
		
		this.onFileViewRender = function($container) {
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
				onItem: function(i, cbr) {
					if (cbr) cbr.done(that.emptyDropbox);
					else that.emptyDropbox();
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
			var $items = $("#mollify-dropbox-list .mollify-dropbox-list-item");
			$items.click(function(e) {
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
			if (mollify.ui.draganddrop) {
				mollify.ui.draganddrop.enableDrag($items, {
					onDragStart : function($e, e) {
						var item = $e.tmplItem().data;
						return {type:'filesystemitem', payload: item};
					}
				});
			}
			$("#mollify-dropbox-list .mollify-dropbox-list-item > a.item-remove").click(function() {
				var $t = $(this);
				that.items.remove($t.tmplItem().data);
				that.refreshList();
			});
		};
					
		return {
			id: "plugin-dropbox",
			initialize: that.initialize,
			fileViewHandler : {
				onFileViewRender: that.onFileViewRender
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

	/**
	*	Share plugin
	**/
	mollify.plugin.SharePlugin = function() {
		var that = this;
		
		this.initialize = function() {
			that._timestampFormatter = new mollify.ui.formatters.Timestamp(mollify.ui.texts.get('shortDateTimeFormat'));
			mollify.dom.importCss(mollify.plugins.url("Share", "style.css"));
		};
		
		this.renderItemContextDetails = function(el, item, $content, data) {
			$content.addClass("loading");
			mollify.templates.load("shares-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Share", "content.html")), function() {
				$content.removeClass("loading");
				mollify.dom.template("mollify-tmpl-shares", {item: item}).appendTo($content);
				that.loadShares(item).done(function(shares) {
					that.initContent(item, shares, $content);
				});
			});
		};
		
		this.loadShares = function(item) {
			if (!item) return mollify.service.get("share/all/");
			return mollify.service.get("share/items/"+item.id).done(function(result) {
				that.refreshShares(result);
			});
		};
		
		this.refreshShares = function(shares) {
			that.shares = shares;
			that.shareIds = [];
			
			for (var i=0, j=that.shares.length; i<j; i++)
				that.shareIds.push(shares[i].id);			
		};
		
		this.getShare = function(id) {
			return that.shares[that.shareIds.indexOf(id)];
		}
		
		this.initContent = function(item, shares, $c) {
			var title = item.shareTitle ? item.shareTitle : mollify.ui.texts.get(item.is_file ? 'shareDialogShareFileTitle' : 'shareDialogShareFolderTitle');
			$("#share-item-title").html(title);
			$("#share-item-name").html(item.name);
			$("#share-dialog-content").removeClass("loading");
			$("#share-new").click(function() { that.onAddShare(item); } );
			
			that.updateShareList(item);
		};
		
		this.getShareLink = function(share) {
			return mollify.service.url("public/"+share.id, true);
		};
		
		this.updateShareList = function(item) {
			$("#share-items").empty();
			
			if (that.shares.length === 0) {
				$("#share-items").html('<div class="no-share-items">'+mollify.ui.texts.get("shareDialogNoShares")+'</div>');
				return;
			}
			
			var opt = {
				itemClass : function() {
					var c = "item-share";
					if (!this.data.active)
						c = c + " inactive";
					if (!this.data.name || this.data.name.length === 0)
						c = c + " unnamed";
					return c;
				},
				link : function() {
					return that.getShareLink(this.data);
				}
			};
			
			mollify.dom.template("share-template", that.shares, opt).appendTo("#share-items");
			mollify.ui.process($("#share-list"), ["localize"]);
			if (!mollify.ui.clipboard) {
				$(".share-link-copy").hide();
			} else {
				var h = {
					onMouseOver: function($e, clip) { clip.setHandCursor(true); $e.addClass("hover"); },
					onMouseOut: function($e) { $e.removeClass("hover"); }
				}
				$.each($(".share-link-copy"), function(i, e) {
					var share = $(e).tmplItem().data;
					mollify.ui.clipboard.enableCopy($(e), that.getShareLink(share), h);
				});
			}
	
			$(".share-link-toggle").click(function() {
				var share = $(this).tmplItem().data;
				if (!share.active) return;

				var $link = $(this).parent();				
				var $c = $link.parent().siblings(".share-link-content");
				var $share = $c.parent();

				$(".share-link-content").not($c).hide();
				$(".item-share").not($share).removeClass("active");
				
				$share.toggleClass("active");
				$c.slideToggle();
				return false;
			});
			$(".item-share").hover(function() {
					$(".item-share").removeClass("hover");
					$(this).addClass("hover");
				},
				function() {
			});
			$(".share-edit").click(function(e) {
				var share = $(this).tmplItem().data;
				that.onEditShare(item, share);
			});
			$(".share-remove").click(function(e) {
				var share = $(this).tmplItem().data;
				that.removeShare(item, share);
			});
		}

		this.openContextContent = function(toolbarId, contentTemplateId, tmplData) {
			var $c = $("#share-context").empty();
			mollify.dom.template(contentTemplateId, tmplData).appendTo($c);
			mollify.ui.process($c, ["localize"]);
			mollify.ui.controls.datepicker("share-validity-expirationdate-value", {
				format: mollify.ui.texts.get('shortDateTimeFormat'),
				time: true
			})
			$("#share-context-container").animate({
				"top" : "18px"
			}, 500);
		}
		
		this.closeAddEdit = function() {
			$("#share-context-container").animate({
				"top" : "300px"
			}, 500);
		}
		
		this.onAddShare = function(item) {
			that.openContextContent('add-share-title', 'share-context-addedit-template');
			$("#share-general-name").val('');
			$('#share-general-active').attr('checked', true);
	
			$("#share-addedit-btn-ok").click(function() {
				var name = $("#share-general-name").val();
				var active = $("#share-general-active").is(":checked");
				var expiration = $("#share-validity-expirationdate-value").data("mollify-datepicker").get();
				
				$("#share-items").empty().append('<div class="loading"/>');
				that.closeAddEdit();
				that.addShare(item, name || '', expiration, active);
			});
			
			$("#share-addedit-btn-cancel").click(function() {
				that.closeAddEdit();
			});
		};
		
		this.onEditShare = function(item, share) {
			that.openContextContent('edit-share-title', 'share-context-addedit-template', { edit: true });
			
			$("#share-general-name").val(share.name);
			$("#share-general-active").attr("checked", share.active);
			
			$("#share-addedit-btn-ok").click(function() {
				var name = $("#share-general-name").val();
				var active = $("#share-general-active").is(":checked");
				var expiration = $("#share-validity-expirationdate-value").data("mollify-datepicker").get();
				
				$("#share-items").empty().append('<div class="loading"/>')
				that.closeAddEdit();
				that.editShare(item, share.id, name || '', expiration, active);
			});
			
			$("#share-addedit-btn-cancel").click(function() {
				that.closeAddEdit();
			});
		}
		
		this.onOpenShares = function(item) {
			mollify.templates.load("shares-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Share", "content.html")), function() {
				mollify.ui.dialogs.custom({
					resizable: true,
					initSize: [600, 470],
					title: item.shareTitle ? item.shareTitle : mollify.ui.texts.get(item.is_file ? 'shareDialogShareFileTitle' : 'shareDialogShareFolderTitle'),
					content: mollify.dom.template("mollify-tmpl-shares", {item: item, bubble: false}),
					buttons: [
						{ id: "no", "title": mollify.ui.texts.get('dialogClose') }
					],
					"on-button": function(btn, d) {
						d.close();
						that.d = false;
					},
					"on-show": function(h, $d) {
						that.d = h;
						that.loadShares(item).done(function(shares) { that.initContent(item, shares, $d); });
					}
				});
			});
		};
		
		this.addShare = function(item, name, expiration, active) {
			return mollify.service.post("share/", { item: item.id, name: name, expiration: mollify.helpers.formatInternalTime(expiration), active: active }).done(function(result) {
				that.refreshShares(result);
				that.updateShareList(item);
			}).fail(that.d.close);
		}
	
		this.editShare = function(item, id, name, expiration, active) {
			return mollify.service.put("share/"+id, { id: id, name: name, expiration: mollify.helpers.formatInternalTime(expiration), active: active }).done(function(result) {
				var share = that.getShare(id);
				share.name = name;
				share.active = active;
				share.expiration = expiration;
				that.updateShareList(item);
			}).fail(that.d.close);
		}
		
		this.removeShare = function(item, share) {
			return mollify.service.del("share/"+share.id).done(function(result) {
				var i = that.shareIds.indexOf(share.id);
				that.shareIds.splice(i, 1);
				that.shares.splice(i, 1);
				that.updateShareList(item);
			}).fail(that.d.close);
		}

		this.removeAllItemShares = function(item) {
			return mollify.service.del("share/items/"+item.id);
		}
		
		this.getActionValidationMessages = function(action, items, validationData) {
			var messages = [];
			$.each(items, function(i, itm) {
				var msg;
				if (itm.reason == 'item_shared') msg = mollify.ui.texts.get("pluginShareActionValidationDeleteShared", itm.item.name);
				else if (itm.reason == 'item_shared_others') msg = mollify.ui.texts.get("pluginShareActionValidationDeleteSharedOthers", itm.item.name);
				else return;

				messages.push({
					message: msg,
					acceptable: itm.acceptable,
					acceptKey: itm.acceptKey
				});
			});
			return messages;
		}
		
		this.getListCellContent = function(item, data) {
			if (!item.id || item.id.length === 0 || !data || !data["plugin-share-info"]) return "";
			var itemData = data["plugin-share-info"][item.id];
			if (!itemData) return "<div id='item-share-info-"+item.id+"' class='filelist-item-share-info empty'></div>";
			if (itemData.own > 0)
				return "<div id='item-share-info-"+item.id+"' class='filelist-item-share-info'><i class='icon-external-link'></i>&nbsp;"+itemData.own+"</div>";
			return "<div id='item-share-info-"+item.id+"' class='filelist-item-share-info others' title='"+mollify.ui.texts.get("pluginShareFilelistColOtherShared")+"'><i class='icon-external-link'></i></div>";
		};

		this._updateListCellContent = function(item, data) {
		};
		
		this.showShareBubble = function(item, cell) {
			that.d = mollify.ui.controls.dynamicBubble({element:cell, title: item.name, container: $("body")});
			
			mollify.templates.load("shares-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Share", "content.html")), function() {
				that.d.content(mollify.dom.template("mollify-tmpl-shares", {item: item, bubble: true}));
				that.loadShares(item).done(function(shares) { that.initContent(item, shares, that.d.element()); });
			});
		};

		this.onActivateConfigView = function($c) {
			var shares = false;
			var items = false;
			var listView = false;

			var updateShares = function() {
				$c.addClass("loading");
				that.loadShares().done(function(l) {
					$c.removeClass("loading");
					shares = l.shares[mollify.session.user_id];
					items = l.items;
					listView.table.set(items);
				});
			}

			listView = new mollify.view.ConfigListView($c, {
				table: {
					key: "id",
					columns: [
						{ id: "icon", title:"", type:"static", content: '<i class="icon-file"></i>' },
						{ id: "name", title: mollify.ui.texts.get('fileListColumnTitleName') },
						{ id: "count", title: mollify.ui.texts.get('pluginShareConfigViewCountTitle'), valueMapper: function(item) {
							return shares[item.id].length;
						} },
						{ id: "edit", title: "", type: "action", content: '<i class="icon-edit"></i>' },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					]
				},
				onTableRowAction: function(table, id, item) {
					if (id == "edit") {
						that.onOpenShares(item);
					} else if (id == "remove") {
						that.removeAllItemShares(item).done(updateShares);
					}
				}
			});
			updateShares();
		}
		
		return {
			id: "plugin-share",
			initialize: that.initialize,

			configViewHandler : {
				views : function() {
					return [{
						title: mollify.ui.texts.get("pluginShareConfigViewNavTitle"),
						onActivate: that.onActivateConfigView
					}];
				}
			},
			fileViewHandler : {
				filelistColumns : function() {
					return [{
						"id": "share-info",
						"request-id": "plugin-share-info",
						"title-key": "",
						"content": that.getListCellContent,
						"request": function(parent) { return {}; },
						"on-click": function(item, data) {
							if (!item.id || item.id.length === 0 || !data || !data["plugin-share-info"]) return;
							var itemData = data["plugin-share-info"][item.id];
							if (!itemData) return;
							
							if (itemData.own > 0)
								that.showShareBubble(item, $("#item-share-info-"+item.id));
						}
					}];
				}
			},
			itemContextHandler : function(item, ctx, data) {
				return {
					actions: [
						{ id: 'pluginShare', 'title-key': 'itemContextShareMenuTitle', callback: function() { that.onOpenShares(item); } }
					]
				};
			},
			
			actionValidationHandler : function() {
				return {
					getValidationMessages : that.getActionValidationMessages
				}
			},

			openShares : that.onOpenShares
		};
	}
}(window.jQuery, window.mollify);
