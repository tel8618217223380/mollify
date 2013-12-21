/**
 * plugins.js
 *
 * Copyright 2008- Samuli Järvelä
 * Released under GPL License.
 *
 * License: http://www.mollify.org/license.php
 */
 
!function($, mollify) {

	"use strict"; // jshint ;_;
	
	mollify.plugin = {};
	
	mollify.plugin.Core = function() {
		var that = this;
								
		return {
			id: "plugin-core",
			itemContextHandler : function(item, ctx, data) {
				var root = item.id == item.root_id;
				var writable = !root && (ctx.details.permission.toLowerCase() == "rw" || ctx.details.permission.toLowerCase() == "rwd");
				var deletable = !root && (ctx.details.permission.toLowerCase() == "rwd");
				var parentWritable = !root && (ctx.details.parent_permission.toLowerCase() == "rw" || ctx.details.parent_permission.toLowerCase() == "rwd");

				var actions = [];				
				if (item.is_file ) {
					actions.push({ 'title-key': 'actionDownloadItem', icon: 'download', type:"primary", group:"download", callback: function() { mollify.ui.download(mollify.filesystem.getDownloadUrl(item)); } });
					actions.push({ title: '-' });
				}
				
				actions.push({ 'title-key': 'actionCopyItem', icon: 'copy', callback: function() { return mollify.filesystem.copy(item); }});
				if (parentWritable)
					actions.push({ 'title-key': 'actionCopyItemHere', icon: 'copy', callback: function() { return mollify.filesystem.copyHere(item); } });
				
				if (writable) {	
					actions.push({ 'title-key': 'actionMoveItem', icon: 'mail-forward', callback: function() { return mollify.filesystem.move(item); } });
					actions.push({ 'title-key': 'actionRenameItem', icon: 'pencil', callback: function() { return mollify.filesystem.rename(item); } });
					if (deletable)
						actions.push({ 'title-key': 'actionDeleteItem', icon: 'trash', callback: function() { var df = $.Deferred(); mollify.ui.dialogs.confirmation({
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
						{ 'title-key': 'actionCopyMultiple', icon: 'copy', callback: function() { return mollify.filesystem.copy(items); } },
						{ 'title-key': 'actionMoveMultiple', icon: 'mail-forward', callback: function() { return mollify.filesystem.move(items); } },
						{ 'title-key': 'actionDeleteMultiple', icon: 'trash', callback: function() { return mollify.filesystem.del(items); } }
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
			that.fileSizeFormatter = new mollify.ui.formatters.ByteSize(new mollify.ui.formatters.Number(2, false, mollify.ui.texts.get('decimalSeparator')));
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
			mollify.templates.load("itemdetails-content", mollify.helpers.noncachedUrl(mollify.plugins.url("ItemDetails", "content.html"))).done(function() {
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
			mollify.dom.template("itemdetails-template", {groups: result}).appendTo(o.element);
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
			return mollify.service.post("itemcollections/"+ic.id, {items : window.isArray(items) ? items: [ items ]});
		};
		
		this._removeCollectionItem = function(ic, items) {
			return mollify.service.del("itemcollections/"+ic.id+"/items", {items : window.isArray(items) ? items: [ items ]});
		};
				
		this._showCollection = function(ic) {
			that._fileView.changeToFolder("ic/"+ic.id);
		};
		
		this.editCollection = function(ic, done) {
			mollify.service.get("itemcollections/"+ic.id).done(function(loaded){
				mollify.ui.dialogs.tableView({
					title: mollify.ui.texts.get('pluginItemCollectionsEditDialogTitle', ic.name),
					buttons:[{id:"close", title:mollify.ui.texts.get('dialogClose')},{id:"remove", title:mollify.ui.texts.get("pluginItemCollectionsEditDialogRemove"), type:"secondary", cls:"btn-danger secondary"}],
					onButton: function(btn, h) {
						h.close();
						if (btn.id == 'remove') that.removeCollection(ic);
						done(btn.id == 'remove');
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
			var itemsById = {};
			$.each(list, function(i, ic) {
				itemsById[ic.id] = ic;
				navBarItems.push({title:ic.name, obj: ic, callback:function(){ that._showCollection(ic); }})
			});
			that._collectionsNav.update(navBarItems);
			
			var f = that._fileView.getCurrentFolder();
			if (f.type == 'ic') that._collectionsNav.setActive(itemsById[f.id]);
		}

		this.removeCollection = function(ic) {
			return mollify.service.del("itemcollections/"+ic.id).done(that._updateNavBar);
		};

		this._onShareNavItem = function(ic) {
			if (!mollify.plugins.exists("plugin-share")) return;
			mollify.plugins.get("plugin-share").openShares({ id: "ic_" + ic.id, "name": ic.name, shareTitle: mollify.ui.texts.get("pluginItemCollectionShareTitle") });
		};

		this._getItemActions = function(ic) {
			var items = [
				{"title-key":"pluginItemCollectionsNavEdit", callback: function() {
					that.editCollection(ic, function(removed) {
						var f = that._fileView.getCurrentFolder();
						if (f.type != 'ic' || f.id != ic.id) return;

						if (removed) that._fileView.openInitialFolder();
						else that._fileView.refresh();
					});
				}},
				{"title-key":"pluginItemCollectionsNavRemove", callback: function() { that._fileView.openInitialFolder(); that.removeCollection(ic); }}
			];
			if (mollify.plugins.exists("plugin-share")) items.push({"title-key":"pluginItemCollectionsNavShare", callback: function() { that._onShareNavItem(ic); }});
			return items;
		}
		
		this._onFileViewInit = function(fv) {
			that._fileView = fv;
			that._fileView.addCustomFolderType("ic", {		
				onSelectFolder : function(id) {
					var df = $.Deferred();
					mollify.service.post("itemcollections/"+id+"/data", {rq_data: that._fileView.getDataRequest() }).done(function(r) {
						that._collectionsNav.setActive(r.ic);
						
						var fo = {
							type: "ic",
							id: r.ic.id,
							name: r.ic.name
						};
						var data = {
							items: r.ic.items,
							ic: r.ic,
							data: r.data
						};
						df.resolve(fo, data);
					});
					return df.promise();
				},
				
				onFolderDeselect : function(f) {
					that._collectionsNav.setActive(false);
				},
		
				onRenderFolderView : function(f, data, $h, $tb) {
					mollify.dom.template("mollify-tmpl-fileview-header-custom", { folder: f }).appendTo($h);
		
					var opt = {
						title: function() {
							return this.data.title ? this.data.title : mollify.ui.texts.get(this.data['title-key']);
						}
					};
					var $fa = $("#mollify-fileview-folder-actions");
					var actionsElement = mollify.dom.template("mollify-tmpl-fileview-foldertools-action", { icon: 'icon-cog', dropdown: true }, opt).appendTo($fa);
					mollify.ui.controls.dropdown({
						element: actionsElement,
						items: that._getItemActions(data.ic),
						hideDelay: 0,
						style: 'submenu'
					});
					that._fileView.addCommonFileviewActions($fa);
				}
			});
		};
		
		this._onFileViewActivate = function($e, h) {
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
				onInit: that._onFileViewInit,
				onActivate: that._onFileViewActivate
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
			var items = mollify.helpers.arrayize(i);
			if (items.length == 1) {
				item = i[0];
			}
			
			var df = $.Deferred();
			var doCompress = function(folder) {
				if (item) defaultName = item.name + ".zip";				
	
				mollify.ui.dialogs.input({
					title: mollify.ui.texts.get('pluginArchiverCompressDialogTitle'),
					message: mollify.ui.texts.get('pluginArchiverCompressDialogMessage'),
					defaultValue: defaultName,
					yesTitle: mollify.ui.texts.get('pluginArchiverCompressDialogAction'),
					noTitle: mollify.ui.texts.get('dialogCancel'),
					handler: {
						isAcceptable: function(n) { return (!!n && n.length > 0 && (!item || n != item.name)); },
						onInput: function(n) { $.when(that._onCompress(items, folder, n)).then(df.resolve, df.reject); }
					}
				});
			};
			if (!f) {
				mollify.ui.dialogs.folderSelector({
					title: mollify.ui.texts.get('pluginArchiverCompressDialogTitle'),
					message: mollify.ui.texts.get('pluginArchiverCompressSelectFolderDialogMessage'),
					actionTitle: mollify.ui.texts.get('ok'),
					handler: {
						onSelect: function(folder) { doCompress(folder); },
						canSelect: function(folder) { return true; }
					}
				});
			} else {
				doCompress(f);
			}
			
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
			return mollify.service.post("archiver/compress", { 'items' : items, 'folder': folder, 'name': name}).done(function(r) {
				mollify.events.dispatch('archiver/compress', { items: items, folder: folder, name: name });
				mollify.events.dispatch('filesystem/update', { folder: folder });
			});
		};
		
		this._onExtract = function(a, folder) {
			return mollify.service.post("archiver/extract", { item : a, folder : folder }).done(function(r) {
				mollify.events.dispatch('archiver/extract', { item : a, folder : folder });
				mollify.events.dispatch('filesystem/update', { folder : folder });
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
			getDownloadCompressedUrl : function(i) {
				var single = false;
		
				if (!window.isArray(i)) single = i;
				else if (i.length == 1) single = i[0];
				
				if (single)
					return mollify.service.url("archiver/download?item="+single.id, true);

				return false;	//TODO enable downloading array of items?
			},
			itemContextHandler : function(item, ctx, data) {
				var root = (item.id == item.root_id);

				//TODO check only "w" for writable
				var writable = !root && (ctx.details.permission.toLowerCase() == "rw" || ctx.details.permission.toLowerCase() == "rwd");
				var parentWritable = !root && (ctx.details.parent_permission.toLowerCase() == "rwd" || ctx.details.parent_permission.toLowerCase() == "rwd");
				var folderWritable = !root && ctx.folder_permission && (ctx.folder_permission.toLowerCase() == "rw" || ctx.folder_permission.toLowerCase() == "rwd");

				if (parentWritable && that._isArchive(item)) {
					return {
						actions: [
							{"title-key":"pluginArchiverExtract", callback: function() { return that._onExtract(item) } }
						]
					};
				}
				
				var actions = [
					{"title-key":"pluginArchiverDownloadCompressed", icon: 'archive', type:"primary", group:"download", callback: function() { that.onDownloadCompressed([item]); } }
				];
				if (ctx.folder && folderWritable) actions.push({"title-key":"pluginArchiverCompress", icon: 'archive', callback: function() { return that.onCompress(item, ctx.folder); } });
				return {
					actions: actions
				};
			},
			itemCollectionHandler : function(items, ctx) {
				return {
					actions: [
						{"title-key":"pluginArchiverCompress", icon: 'archive', callback: function() { return that.onCompress(items) } },
						{"title-key":"pluginArchiverDownloadCompressed", icon: 'archive', type:"primary", group:"download", callback: function() { return that.onDownloadCompressed(items) } }
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
						$("#"+data.result.resized_element_id).css({
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
			mollify.templates.load("comments-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Comment", "content.html"))).done(function() {
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
			var bubble = mollify.ui.controls.dynamicBubble({element:e, title: item.name, container: $("#mollify-filelist-main-items")});
			
			mollify.templates.load("comments-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Comment", "content.html"))).done(function() {
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
						"width" : 50,
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
			/*that.permissionOptions = [
				{ title: mollify.ui.texts.get('pluginPermissionsValueRWD'), value: "rwd"},
				{ title: mollify.ui.texts.get('pluginPermissionsValueRW'), value: "rw"},
				{ title: mollify.ui.texts.get('pluginPermissionsValueR'), value: "r"},
				{ title: mollify.ui.texts.get('pluginPermissionsValueN'), value: "n"}
			];
			that.permissionOptionsByKey = mollify.helpers.mapByKey(that.permissionOptions, "value");*/
		};
		
		this.editItemPermissions = function(item) {
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
					mollify.service.put("permissions/list", permissionData).done(d.close).fail(d.close);
				},
				"on-show": function(h, $d) {
					$content = $d.find("#mollify-pluginpermissions-editor-content");
					$("#mollify-pluginpermissions-editor-change-item").click(function(e) {
						e.preventDefault();
						return false;
					});

					h.center();
					
					mollify.service.get("permissions/types?u=1").done(function(r) {
						var users = that.processUserData(r.users);
						var names = mollify.helpers.getKeys(r.types.filesystem);	//param
						var init = names[0];
						var onChange = function(sel) {
							permissionData = {
								"new": [],
								"modified": [],
								"removed": []
							};
							$content.addClass("loading");
							
							that.loadPermissions(item).done(function(p) {
								$content.removeClass("loading");
								that.initEditor(item, sel, r.types.filesystem[sel], p.permissions, users, permissionData);
							}).fail(h.close);
						};
						
						var $permissionName = mollify.ui.controls.select("mollify-pluginpermissions-editor-permission-name", {
							onChange: onChange,
							formatter: function(name) {
								return mollify.ui.texts.get('permission_'+name);
							},
							values: names,
							value: init
						});
						onChange(init);
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
		
		this.loadPermissions = function(item) {
			return mollify.service.get("permissions/list?subject="+item.id);
		};
		
		this.initEditor = function(item, permissionName, permissionValues, permissions, userData, permissionData) {
			var $list;
			
			var isGroup = function(id) { return (id != 0 && userData.usersById[id].is_group != "0"); };
			var onAddOrUpdate = function(user, permissionVal) {
				var userVal = $list.findByKey(user.id);
				if (userVal) {
					if (!userVal.isnew) permissionData.modified.push(userVal);
					userVal.value = permissionVal;
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
					var p = {"user_id": user.id, "subject": item.id, "name" : permissionName, "value": permissionVal, isnew: true };
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
					{
						id: "value",
						title: mollify.ui.texts.get('pluginPermissionsEditColPermission'),
						type: "select",
						options: permissionValues,
						formatter: function(item, k) {
							return mollify.ui.texts.get('permission_'+item.name+'_'+k);
						},
						onChange: function(item, p) {
							item.value = p;
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
				none: mollify.ui.texts.get('pluginPermissionsEditNoUser'),
				title : "name",
				onCreate : function($o, i) { if (isGroup(i.id)) $o.addClass("group"); }
			});
			$newUser.add({ name: mollify.ui.texts.get('pluginPermissionsEditDefaultPermission'), user_id: 0, is_group: 0 });
			$newUser.add(userData.users);
			$newUser.add(userData.groups);
			
			var $newPermission = mollify.ui.controls.select("mollify-pluginpermissions-editor-new-permission", {
				values: permissionValues,
				none: mollify.ui.texts.get('pluginPermissionsEditNoPermission'),
				formatter : function(p) { return mollify.ui.texts.get('permission_'+permissionName+'_'+p); }
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
				
				onAddOrUpdate(selectedUser, selectedPermission);
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
		
		this.onActivateConfigView = function($c, cv) {
			mollify.service.get("permissions/types?u=1").done(function(r) {
				var users = that.processUserData(r.users);
				
				var allTypeKeys = mollify.helpers.getKeys(r.types.generic).concat(mollify.helpers.getKeys(r.types.filesystem));
				var allTypes = $.extend({}, r.types.generic, r.types.filesystem);
				var values = mollify.helpers.mapByKey(r.permissions, "name", "value");

				var getQueryParams = function(i) {
					/*var start = $optionStart.get();
					var end = $optionEnd.get();
					var tp = $optionType.get();
					if (tp == "custom") tp = $("#eventlogging-event-type-custom").val();
					if (!tp || tp.length === 0) tp = null;
					var user = $optionUser.get();*/
					
					var params = {
						
					};
					/*if (start) params.start_time = mollify.helpers.formatInternalTime(start);
					if (end) params.end_time = mollify.helpers.formatInternalTime(end);
					if (user) params.user = user.name;
					if (tp) params.type = tp;*/
					
					return params;
				};
				
				var refresh = function() {
					cv.showLoading(true);
					listView.table.refresh().done(function(){ cv.showLoading(false); });
				};
	
				var listView = new mollify.view.ConfigListView($c, {
					actions: [
						{ id: "action-refresh", content:'<i class="icon-refresh"></i>', callback: refresh }
					],
					table: {
						id: "config-permissions-list",
						key: "id",
						narrow: true,
						hilight: true,
						remote: {
							path : "permissions/query",
							paging: { max: 50 },
							queryParams: getQueryParams,
							onLoad: function(pr) { $c.addClass("loading"); pr.done(function() { $c.removeClass("loading"); }); }
						},
						defaultSort: { id: "time", asc: false },
						columns: [
							{ id: "name", title: mollify.ui.texts.get('pluginPermissionsPermissionName'), sortable: true, formatter: function(item, name) {
								if (r.types.filesystem[name])
									return mollify.ui.texts.get('permission_default_'+name);
								return mollify.ui.texts.get('permission_'+name);
							} },
							{ id: "value", title: mollify.ui.texts.get('pluginPermissionsPermissionValue'), sortable: true, formatter: function(item, k) {
								var itemValues = allTypes[item.name];
								if (itemValues) return mollify.ui.texts.get('permission_'+item.name+'_'+k);
								return mollify.ui.texts.get('permission_'+k);
							} },
							{ id: "user_id", title: mollify.ui.texts.get('pluginPermissionsPermissionUser'), sortable: true },
							{ id: "subject", title: mollify.ui.texts.get('pluginPermissionsPermissionSubject'), formatter: function(v) { return "..."; } }
						]
					}
				});
				var $options = $c.find(".mollify-configlistview-options");
				mollify.dom.template("mollify-tmpl-permission-admin-options").appendTo($options);
				mollify.ui.process($options, ["localize"]);
				
				/*$optionType = mollify.ui.controls.select("eventlogging-event-type", {
					values: that._types.concat(["custom"]),
					formatter: function(v) {
						if (v == "custom") return mollify.ui.texts.get('pluginEventLoggingAdminEventTypeCustom');
						return that._typeTexts[v] + " ("+v+")";
					},
					none: mollify.ui.texts.get('pluginEventLoggingAdminAny'),
					onChange: function(t) {
						if (t == "custom")
							$("#eventlogging-event-type-custom").show().val("").focus();
						else
							$("#eventlogging-event-type-custom").hide();
					}
				});
				$optionUser = mollify.ui.controls.select("eventlogging-user", {
					values: users,
					formatter: function(u) { return u.name; },
					none: mollify.ui.texts.get('pluginEventLoggingAdminAny')
				});
				$optionStart = mollify.ui.controls.datepicker("eventlogging-start", {
					format: mollify.ui.texts.get('shortDateTimeFormat'),
					time: true
				});
				$optionEnd = mollify.ui.controls.datepicker("eventlogging-end", {
					format: mollify.ui.texts.get('shortDateTimeFormat'),
					time: true
				});*/
				refresh();
			});
		};
		
		this.editUserGenericPermissions = function(user) {
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
				content: mollify.dom.template("mollify-tmpl-permission-generic-editor", {user: user}),
				buttons: [
					{ id: "yes", "title": mollify.ui.texts.get('dialogSave') },
					{ id: "no", "title": mollify.ui.texts.get('dialogCancel') }
				],
				"on-button": function(btn, d) {
					if (btn.id == 'no') {
						d.close();
						return;
					}
					/*if (permissionData["new"].length === 0 && permissionData.modified.length === 0 && permissionData.removed.length === 0)
						return;
					
					$content.addClass("loading");
					mollify.service.put("permissions/list", permissionData).done(d.close).fail(d.close);*/
				},
				"on-show": function(h, $d) {
					$content = $d.find("#mollify-pluginpermissions-editor-generic-content");
					h.center();
					var $list = false;
					
					mollify.service.get("permissions/user/"+user.id+"/generic/?t=1").done(function(r) {
						$content.removeClass("loading");
						
						var allTypeKeys = mollify.helpers.getKeys(r.types.generic).concat(mollify.helpers.getKeys(r.types.filesystem));
						var allTypes = $.extend({}, r.types.generic, r.types.filesystem);
						var values = mollify.helpers.mapByKey(r.permissions, "name", "value");
												
						var permissions = [];
						
						$.each(allTypeKeys, function(i, t) {
							permissions.push({ name: t, value: values[t] });
						});
						
						$list = mollify.ui.controls.table("mollify-pluginpermissions-editor-generic-permission-list", {
							key: "name",
							columns: [
								{ id: "name", title: mollify.ui.texts.get('pluginPermissionsPermissionName'), formatter: function(item, name) {
									if (r.types.filesystem[name])
										return mollify.ui.texts.get('permission_default_'+name);
									return mollify.ui.texts.get('permission_'+name);
								} },
								{
									id: "value",
									title: mollify.ui.texts.get('pluginPermissionsPermissionValue'),
									type: "select",
									options: function(item) {
										var itemValues = allTypes[item.name];
										if (itemValues) return itemValues;
										return ["yes", "no"];
									},
									none: function(item) {
										var itemValues = allTypes[item.name];
										var fallback = "";
										if (itemValues) {
											fallback = mollify.ui.texts.get('permission_'+item.name+'_'+itemValues[0]);
										} else {
											fallback = mollify.ui.texts.get('permission_no');
										}
										var text = mollify.ui.texts.get('permission_value_undefined', fallback);
										return text;
									},
									formatter: function(item, k) {
										var itemValues = allTypes[item.name];
										if (itemValues) return mollify.ui.texts.get('permission_'+item.name+'_'+k);
										return mollify.ui.texts.get('permission_'+k);
									},
									//onChange: function(item, p) {
									//	item.value = p.value;
									//	onEdit(item);
									//},
								}
							]
						});
						$list.add(permissions);						
					}).fail(h.close);
				}
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
						{ id: 'pluginPermissions', 'title-key': 'pluginPermissionsAction', callback: function() { that.editItemPermissions(item); } }
					]
				};
			},
			configViewHandler : {
				views : function() {
					return [{
						viewId: "permissions",
						admin: true,
						title: mollify.ui.texts.get("pluginPermissionsConfigViewNavTitle"),
						onActivate: that.onActivateConfigView
					}];
				}
			},
			editUserGenericPermissions: that.editUserGenericPermissions
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
		that.itemsByKey = {};
		
		this.initialize = function() {
			that.itemContext = new mollify.ui.itemContext();
			mollify.events.addEventHandler(function(e) {
				if (e.type == 'filesystem/delete') that.onRemoveItems(mollify.helpers.extractValue(e.payload.items, "id"));
				//TODO else if (e.type == 'filesystem/rename') that.updateItems(mollify.helpers.extractValue(e.payload.items));
			});
		};
		
		this.onFileViewActivate = function($container) {
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
				var dnd = {
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
				};
				mollify.ui.draganddrop.enableDrop($("#mollify-dropbox-list"), dnd);
				mollify.ui.draganddrop.enableDrop($("#mollify-dropbox-handle"), dnd);
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
			that._updateButton();
			that.openDropbox(false);
		};
		
		this.onFileViewDeactivate = function() {
			$("#mollify-dropbox").remove();
		};
		
		this.getActions = function(cb) {				
			if (that.items.length === 0) {
				cb([]);
				return;
			}
			var actions = mollify.helpers.getPluginActions(mollify.plugins.getItemCollectionPlugins(that.items, {src: "dropbox"}));
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
			that.itemsByKey = {};
			that.refreshList();
		};
				
		this.onAddItem = function(i) {
			that.openDropbox(true);
			var list = i;
			if (!window.isArray(i))
				list = [i];
			$.each(list, function(ind, item) {
				if (that.items.indexOf(item) >= 0) return;
				that.items.push(item);
				that.itemsByKey[item.id] = item;
			});
			that.refreshList();
			that._updateButton();
		};
		
		this.onRemoveItem = function(item) {
			that.items.remove(item);
			delete that.itemsByKey[item.id];
			that.refreshList();
			that._updateButton();
		};

		this.onRemoveItems = function(ids) {
			var count = 0;
			$.each(ids, function(i, id) {
				var item = that.itemsByKey[id];
				if (!item) return;
				
				that.items.remove(item);
				delete that.itemsByKey[id];
				count++;
			});
			if (count > 0) {
				that.refreshList();
				that._updateButton();
			}
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
					container: mollify.App.getElement(),
					viewport: mollify.App.getElement()
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
				mollify.ui.hideActivePopup();
				var $t = $(this);
				that.onRemoveItem($t.tmplItem().data);
			});
		};
		
		this._updateButton = function() {
			var $btn = $("#mollify-dropbox-actions > button");
			if (that.items.length > 0)
				$btn.removeClass("disabled");
			else
				$btn.addClass("disabled");
		};
					
		return {
			id: "plugin-dropbox",
			initialize: that.initialize,
			fileViewHandler : {
				onActivate: that.onFileViewActivate,
				onDeactivate: that.onFileViewDeactivate
			},
			itemContextHandler : function(item, ctx, data) {
				return {
					actions: [
						{ id: 'pluginDropbox', 'title-key': 'pluginDropboxAddTo', callback: function() { that.onAddItem(item); that.openDropbox(true); } }
					]
				};
			},
			itemCollectionHandler : function(items, ctx) {
				if (ctx && ctx.src == 'dropbox') return false;
				return {
					actions: [
						{ 'title-key': 'pluginDropboxAddTo', callback: function() { return that.onAddItem(items); } }
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
			
			mollify.App.registerView("share", {
				getView : function(rqParts, urlParams) {					
					if (rqParts.length != 2) return false;
					var df = $.Deferred();
					
					var shareId = rqParts[1];
					mollify.service.get("public/"+shareId+"/info/").done(function(result) {
						if (!result || !result.type || (["download", "upload", "prepared_download"].indexOf(result.type) < 0)) {
							df.resolve(new mollify.ui.FullErrorView(mollify.ui.texts.get('shareViewInvalidRequest')));
							return;
						}
						
						if (result.restriction == "private") {
							if (!mollify.session || !mollify.session.authenticated) {
								df.resolve(false);
								return;
							}
						} else if (result.restriction == "pw" && !result.auth) {
							df.resolve(new that.ShareAccessPasswordView(shareId, result));
							return;
						}
						
						df.resolve(that._getShareView(shareId, result));
					}).fail(function() {
						df.resolve(new mollify.ui.FullErrorView(mollify.ui.texts.get('shareViewInvalidRequest')));
					});
					return df.promise();
				}
			});
		};
		
		this._getShareView = function(id, info) {
			var serviceUrl = mollify.service.url("public/"+id, true);			
			var urlProvider = {
				get : function(path, param) {
					var url = serviceUrl;
					if (path) url = url + path;
					if (param) url = mollify.helpers.urlWithParam(url, param);
					return mollify.helpers.noncachedUrl(url);
				}
			}
			
			if (info.type == "download") {
				return new that.ShareDownloadView(id, urlProvider, info.name);
			} else if (info.type == "prepared_download") {
				return new that.SharePreparedDownloadView(id, urlProvider, info.name);
			} else {
				return new that.ShareUploadView(id, urlProvider, info.name);
			}
			return new mollify.ui.FullErrorView(mollify.ui.texts.get('shareViewInvalidRequest'));
		};

		this.ShareAccessPasswordView = function(shareId, info) {
			var vt = this;
			
			this.init = function($c) {
				vt._$c = $c;
				
				mollify.dom.loadContentInto($c, mollify.plugins.url("Share", "public_share_access_password.html"), function() {
					$("#mollify-share-access-button").click(vt._onAccess);
					$("#mollify-share-access-password").focus();
					$("#mollify-share-access-password").bind('keypress', function(e) {
						if ((e.keyCode || e.which) == 13) vt._onAccess();
					});
				}, ['localize']);
			};
			
			this._onAccess = function() {
				var pw = $("#mollify-share-access-password").val();
				if (!pw || pw.length === 0) return;
				var key = window.Base64.encode(pw);
				
				mollify.service.post("public/"+shareId+"/key/", { key: key }).done(function(r) {
					if (!r.result) {
						mollify.ui.dialogs.notification({
							message: mollify.ui.texts.get('shareAccessPasswordFailed')
						});
						$("#mollify-share-access-password").focus();
						return;
					}
					//proceed to original view
					that._getShareView(shareId, info, key).init(vt._$c);
				});				
			};
		};
		
		this.ShareDownloadView = function(shareId, u, shareName) {
			var vt = this;
			
			this.init = function($c) {
				mollify.dom.loadContentInto($c, mollify.plugins.url("Share", "public_share_download.html"), function() {
					$("#mollify-share-title").text(mollify.ui.texts.get("shareViewDownloadTitle", shareName));
					
					setTimeout(function() { mollify.ui.download(u.get()); }, 1000);
				}, ['localize']);
			};
		};

		this.SharePreparedDownloadView = function(shareId, u, shareName) {
			var vt = this;
			
			this.init = function($c) {
				mollify.dom.loadContentInto($c, mollify.plugins.url("Share", "public_share_prepared_download.html"), function() {
					$("#mollify-share-download-prepare").text(mollify.ui.texts.get("shareViewPreparedDownloadPreparingTitle", shareName));
					$("#mollify-share-download").text(mollify.ui.texts.get("shareViewPreparedDownloadDownloadingTitle", shareName));
					$("#mollify-share-download-error").text(mollify.ui.texts.get("shareViewPreparedDownloadErrorTitle", shareName));
					
					mollify.service.get(u.get("/prepare")).done(function(r) {
						$("#mollify-share-download-prepare").hide();
						$("#mollify-share-download").show();
						mollify.ui.download(u.get(false, "key="+r.key));
					}).fail(function() {
						this.handled = true;
						$("#mollify-share-download-prepare").hide();
						$("#mollify-share-download-error").show();
					});
				}, ['localize']);
			};
		};
						
		this.ShareUploadView = function(shareId, u, shareName) {
			var vt = this;
			
			this.init = function($c) {
				var uploadSpeedFormatter = new mollify.ui.formatters.Number(1, mollify.ui.texts.get('dataRateKbps'), mollify.ui.texts.get('decimalSeparator'));
				
				mollify.dom.loadContentInto($c, mollify.plugins.url("Share", "public_share_upload.html"), function() {
					$("#mollify-share-title").text(mollify.ui.texts.get("shareViewUploadTitle", shareName));
					vt._uploadProgress = new that.PublicUploaderProgress($("#mollify-share-public-upload-progress"));
					
					mollify.ui.uploader.initUploadWidget($("#mollify-share-public-uploader"), {
						url: u.get(false, "format=binary"),
						dropElement: $("#mollify-share-public"),
						handler: {
							start: function(files, ready) {							
								vt._uploadProgress.start(mollify.ui.texts.get(files.length > 1 ? "mainviewUploadProgressManyMessage" : "mainviewUploadProgressOneMessage", files.length));
								ready();
							},
							progress: function(pr, br) {
								var speed = "";
								if (br) speed = uploadSpeedFormatter.format(br/1024);
								vt._uploadProgress.update(pr, speed);
							},
							finished: function() {
								setTimeout(function() { vt._uploadProgress.success(mollify.ui.texts.get('mainviewFileUploadComplete')); }, 1000);
							},
							failed: function(e) {
								if (e && e.code == 216) {
									vt._uploadProgress.failure(mollify.ui.texts.get('mainviewFileUploadNotAllowed'));
								} else {
									vt._uploadProgress.failure(mollify.ui.texts.get('mainviewFileUploadFailed'));
								}
							}
						}
					});
				}, ['localize']);
			};
		};
		
		this.PublicUploaderProgress = function($e) {
			var t = this;
			t._$title = $e.find(".title");
			t._$speed = $e.find(".speed");
			t._$bar = $e.find(".bar");
			
			return {
				start : function(title) {
					$e.removeClass("success failure");
					t._$title.text(title ? title : "");
					t._$speed.text("");
					t._$bar.css("width", "0%");
				},
				update : function(progress, speed) {
					t._$bar.css("width", progress+"%");
					t._$speed.text(speed ? speed : "");
				},
				success : function(text) {
					$e.addClass("success");
					t._$bar.css("width", "0%");
					t._$title.text(text);
					t._$speed.text("");
				},
				failure : function(text) {
					$e.addClass("failure");
					t._$title.text(text);
					t._$speed.text("");
					t._$bar.css("width", "0%");
				}
			}
		};
		
		this.renderItemContextDetails = function(el, item, $content, data) {
			$content.addClass("loading");
			mollify.templates.load("shares-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Share", "content.html"))).done(function() {
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
			that._context = mollify.ui.controls.slidePanel($("#share-list"), { relative: true });
			
			that.updateShareList(item);
		};
		
		this.getShareLink = function(share) {
			return mollify.App.getPageUrl("share/"+share.id);
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
			/*var $c = $("#share-context").empty();*/
			var $c = that._context.getContentElement().empty();
			
			mollify.dom.template(contentTemplateId, tmplData).appendTo($c);
			mollify.ui.process($c, ["localize"]);
			mollify.ui.controls.datepicker("share-validity-expirationdate-value", {
				format: mollify.ui.texts.get('shortDateTimeFormat'),
				time: true
			});
			that._context.show(false, 280);
			/*$("#share-context-container").animate({
				"top" : "18px"
			}, 500);*/
		}
		
		this.closeAddEdit = function() {
			that._context.hide();
			/*$("#share-context-container").animate({
				"top" : "300px"
			}, 500);*/
		}
		
		this.onAddShare = function(item) {
			that.openContextContent('add-share-title', 'share-context-addedit-template');
			$("#share-general-name").val('');
			$('#share-general-active').attr('checked', true);
			$("#share-access-norestriction").attr('checked', true);
			$("#share-access-public-password-value").attr("placeholder", mollify.ui.texts.get("shareDialogShareAccessEnterPwTitle"));
			
			$("#share-addedit-btn-ok").click(function() {
				$("#share-access-public-password-value").removeClass("error");
				
				var name = $("#share-general-name").val();
				var active = $("#share-general-active").is(":checked");
				var expiration = $("#share-validity-expirationdate-value").data("mollify-datepicker").get();
				
				var restriction = false;
				if ($("#share-access-private-loggedin").is(":checked")) restriction = { type: "private" };
				else if ($("#share-access-public-password").is(":checked")) {
					var value = $("#share-access-public-password-value").val();
					if (!value || value.length === 0) {
						$("#share-access-public-password-value").addClass("error");
						return;
					}
					restriction = { type: "pw", value : value };
				}
				
				$("#share-items").empty().append('<div class="loading"/>');
				that.closeAddEdit();
				that.addShare(item, name || '', expiration, active, restriction);
			});
			
			$("#share-addedit-btn-cancel").click(function() {
				that.closeAddEdit();
			});
		};
		
		this.onEditShare = function(item, share) {
			that.openContextContent('edit-share-title', 'share-context-addedit-template', { edit: true });
			
			$("#share-general-name").val(share.name);
			$("#share-general-active").attr("checked", share.active);

			var oldRestrictionPw = (share.restriction == 'pw');
			if (share.restriction == 'pw')
				$("#share-access-public-password").attr('checked', true);
			else if (share.restriction == 'private')
				$("#share-access-private-loggedin").attr('checked', true);
			else
				$("#share-access-norestriction").attr('checked', true);
			
			if (share.expiration)
				$("#share-validity-expirationdate-value").data("mollify-datepicker").set(mollify.helpers.parseInternalTime(share.expiration));
			
			if (oldRestrictionPw) $("#share-access-public-password-value").attr("placeholder", mollify.ui.texts.get("shareDialogShareAccessChangePwTitle"));
			else $("#share-access-public-password-value").attr("placeholder", mollify.ui.texts.get("shareDialogShareAccessEnterPwTitle"));
						
			$("#share-addedit-btn-ok").click(function() {
				var name = $("#share-general-name").val();
				var active = $("#share-general-active").is(":checked");
				var expiration = $("#share-validity-expirationdate-value").data("mollify-datepicker").get();
				
				var restriction = false;
				if ($("#share-access-private-loggedin").is(":checked")) restriction = { type: "private" };
				else if ($("#share-access-public-password").is(":checked")) {
					var value = $("#share-access-public-password-value").val();
					if (!oldRestrictionPw && (!value || value.length === 0)) {
						$("#share-access-public-password-value").addClass("error");
						return;
					}
					restriction = { type: "pw", value : value };
				}
				
				$("#share-items").empty().append('<div class="loading"/>')
				that.closeAddEdit();
				that.editShare(item, share.id, name || '', expiration, active, restriction);
			});
			
			$("#share-addedit-btn-cancel").click(function() {
				that.closeAddEdit();
			});
		}
		
		this.onOpenShares = function(item) {
			mollify.templates.load("shares-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Share", "content.html"))).done(function() {
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
		
		this.addShare = function(item, name, expiration, active, restriction) {
			return mollify.service.post("share/", { item: item.id, name: name, expiration: mollify.helpers.formatInternalTime(expiration), active: active, restriction: restriction }).done(function(result) {
				that.refreshShares(result);
				that.updateShareList(item);
			}).fail(that.d.close);
		}
	
		this.editShare = function(item, id, name, expiration, active, restriction) {
			return mollify.service.put("share/"+id, { id: id, name: name, expiration: mollify.helpers.formatInternalTime(expiration), active: active, restriction: restriction }).done(function(result) {
				var share = that.getShare(id);
				share.name = name;
				share.active = active;
				share.expiration = mollify.helpers.formatInternalTime(expiration);
				share.restriction = restriction ? restriction.type : false;
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
			that.d = mollify.ui.controls.dynamicBubble({element:cell, title: item.name, container: $("#mollify-filelist-main-items")});
			
			mollify.templates.load("shares-content", mollify.helpers.noncachedUrl(mollify.plugins.url("Share", "content.html"))).done(function() {
				that.d.content(mollify.dom.template("mollify-tmpl-shares", {item: item, bubble: true}));
				that.loadShares(item).done(function(shares) {
					that.initContent(item, shares, that.d.element());
					that.d.position();
				});
			});
		};

		this.onActivateConfigView = function($c, cv) {
			var shares = false;
			var items = false;
			var invalid = [];
			var listView = false;

			var updateShares = function() {
				cv.showLoading(true);
				
				that.loadShares().done(function(l) {
					shares = l.shares[mollify.session.user_id];
					items = l.items;
					invalid = l.invalid;
					listView.table.set(items);
					
					cv.showLoading(false);
				});
			};
			var isValid = function(i) {
				if (invalid.length === 0) return true;
				return (invalid.indexOf(i.id) < 0);
			};

			listView = new mollify.view.ConfigListView($c, {
				table: {
					key: "id",
					columns: [
						{ id: "icon", title:"", valueMapper: function(item) {
							return isValid(item) ? '<i class="icon-file"></i>' : '<i class="icon-exclamation"></i>';
						} },
						{ id: "name", title: mollify.ui.texts.get('fileListColumnTitleName') },
						{ id: "count", title: mollify.ui.texts.get('pluginShareConfigViewCountTitle'), valueMapper: function(item) {
							return shares[item.id].length;
						} },
						{ id: "edit", title: "", type: "action", valueMapper: function(item) {
							return isValid(item) ? '<i class="icon-edit"></i>' : '';
						} },
						{ id: "remove", title: "", type: "action", content: '<i class="icon-trash"></i>' }
					],
					onRow: function($r, item) {
						if (!isValid(item)) $r.addClass("error");	
					},
					onRowAction: function(id, item) {
						if (id == "edit") {
							that.onOpenShares(item);
						} else if (id == "remove") {
							that.removeAllItemShares(item).done(updateShares);
						}
					}
				}
			});
			updateShares();
		}
		
		return {
			id: "plugin-share",
			backendPluginId: "Share",
			resources: {
				css: true
			},
			initialize: that.initialize,

			configViewHandler : {
				views : function() {
					return [{
						viewId: "shares",
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
						{ id: 'pluginShare', 'title-key': 'itemContextShareMenuTitle', icon: 'external-link', callback: function() { that.onOpenShares(item); } }
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
	
	/**
	*	Send via email -plugin
	**/
	mollify.plugin.SendViaEmailPlugin = function() {
		var that = this;
		
		this.initialize = function() {};
		
		return {
			id: "plugin-sendviaemail",
			initialize: that.initialize,

			itemContextHandler : function(item, ctx, data) {
				if (!item.is_file) return false;
				return {
					actions: [
						{ 'title-key': 'actionSendViaEmailSingle', callback: function() { } }
					]
				};
			},
			itemCollectionHandler : function(items) {
				var folder = false;
				$.each(items, function(i, itm){ if (!itm.is_file) { folder = true; return false; } });				
				if (folder) return false;
				
				return {
					actions: [
						{ 'title-key': 'actionSendViaEmailMultiple', callback: function() { } }
					]
				};
			}
		};
	}
	
	/**
	*	Registration -plugin
	**/
	mollify.plugin.RegistrationPlugin = function() {
		var that = this;
		
		this.initialize = function() {
			mollify.App.registerView("registration", {
				getView : function(rqParts, urlParams) {
					if (rqParts.length != 2) return false;
					
					if (rqParts[1] == "new") {
						return new that.NewRegistrationView(urlParams);
					} else if (rqParts[1] == "confirm") {
						return new that.ConfirmRegistrationView(urlParams);
					}
					return false;
				}
			});
		};
		
		this.NewRegistrationView = function() {
			var vt = this;
			
			this.init = function($c) {
				mollify.dom.loadContentInto($c, mollify.plugins.url("Registration", "registration_create.html"), function() {
					$("#register-new-button").click(vt.onRegister);
					$("#registration-new-name").focus();
				}, ['localize']);
			};
			
			this.onRegister = function() {
				$(".control-group").removeClass("error");
				
				var name = $("#registration-new-name").val();
				var pw = $("#registration-new-pw").val();
				var confirmPw = $("#registration-new-pw-confirm").val();
				var email = $("#registration-new-email").val();
				
				var proceed = true;
				if (!name || name.length === 0) {
					$("#registration-new-name").closest(".control-group").addClass("error");
					proceed = false;
				}
				if (!pw || pw.length === 0) {
					$("#registration-new-pw").closest(".control-group").addClass("error");
					proceed = false;
				}
				if (!confirmPw || confirmPw.length === 0) {
					$("#registration-new-pw-confirm").closest(".control-group").addClass("error");
					proceed = false;
				}
				if (!email || email.length === 0) {
					$("#registration-new-email").closest(".control-group").addClass("error");
					proceed = false;
				}
				if (!proceed) return;
				
				if (pw != confirmPw) {
					$("#registration-new-pw").closest(".control-group").addClass("error");
					$("#registration-new-pw-confirm").closest(".control-group").addClass("error");
					return;
				}
				
				mollify.service.post("registration/create", {name:name, password:window.Base64.encode(pw), email:email, data: null}).done(function() {
					$("#mollify-registration-form").hide();
					$("#mollify-registration-main").addClass("wide");
					$("#mollify-registration-success").show();
				}).fail(function() {
					this.handled = true;
					mollify.ui.dialogs.error({message: mollify.ui.texts.get('registrationFailed')});
				});
			}
		};
		
		this.ConfirmRegistrationView = function(urlParams) {
			var vt = this;
			
			this.init = function($c) {				
				mollify.dom.loadContentInto($c, mollify.plugins.url("Registration", "registration_confirm.html"), function() {
					if (!urlParams.email || urlParams.email.length === 0) {
							$("#mollify-registration-main").addClass("complete").empty().append(mollify.dom.template("mollify-tmpl-registration-errormessage", {message: mollify.ui.texts.get('registrationInvalidConfirm')}));
						return;
					}
					vt._email = urlParams.email;

					if (urlParams.key && urlParams.key.length > 0) {
						vt._confirm(vt._email, urlParams.key);
					} else {
						$("#mollify-registration-confirm-form").show();
						$("#registration-confirm-email").val(vt._email);
						$("#register-confirm-button").click(vt.onConfirm);
						$("#registration-confirm-key").focus();
					}
				}, ['localize']);
			};
			
			this.onConfirm = function() {
				$(".control-group").removeClass("error");		
				var key = $("#registration-confirm-key").val();
				
				var proceed = true;
				if (!key || key.length === 0) {
					$("#registration-confirm-key").closest(".control-group").addClass("error");
					proceed = false;
				}
				if (!proceed) return;

				vt._confirm(vt._email, key, true);
			};
			
			this._confirm = function(email, key, fromForm) {
				$("#mollify-registration-main").addClass("loading");
				mollify.service.post("registration/confirm", {email:email, key:key}).done(function(r) {
					$("#mollify-registration-confirm-form").hide();
					$("#mollify-registration-main").removeClass("loading").addClass("wide");
					
					if (!r.require_approval)
						$("#mollify-registration-confirm-success").show();
					else
						$("#mollify-registration-confirm-success-wait-approval").show();
				}).fail(function(error) {
					$("#mollify-registration-main").removeClass("loading");
					this.handled = true;
					if (fromForm)
						mollify.ui.dialogs.error({message: mollify.ui.texts.get('registrationConfirmFailed')});
					else {
						$("#mollify-registration-main").addClass("wide").empty().append(mollify.dom.template("mollify-tmpl-registration-errormessage", {message: mollify.ui.texts.get('registrationConfirmFailed')}));
					}
				});
			};
		};
		
		return {
			id: "plugin-registration",
			initialize: that.initialize,

			show : function() {
				mollify.App.openPage('registration/new');
			}
		};
	}
}(window.jQuery, window.mollify);
