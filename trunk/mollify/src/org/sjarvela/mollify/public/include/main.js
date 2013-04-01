/**
/* Main view
/**/
(function($){$.extend(true, mollify, {
	view : {
		MainView : function () {
			var that = this;
			this.currentFolder = false;
			this.viewStyle = 0;
			
			this.init = function(p) {
				that.listener = p.listener;				
				
				that.itemContext = new mollify.ui.itemContext({ getItemDetails : that.listener.getItemDetails, onDescription: that.onDescription });
			}
			
			this.changePassword = function() {	
				var $dlg = false;
				var $old = false;
				var $new1 = false;
				var $new2 = false;
				var errorTextMissing = mollify.ui.texts.get('mainviewChangePasswordErrorValueMissing');
				var errorConfirm = mollify.ui.texts.get('mainviewChangePasswordErrorConfirm');
				
				mollify.ui.views.dialogs.custom({
					title: mollify.ui.texts.get('mainviewChangePasswordTitle'),
					content: $("#mollify-tmpl-main-changepassword").tmpl({message: mollify.ui.texts.get('mainviewChangePasswordMessage')}),
					buttons: [
						{ id: "yes", "title": mollify.ui.texts.get('mainviewChangePasswordAction') },
						{ id: "no", "title": mollify.ui.texts.get('dialogCancel') }
					],
					"on-button": function(btn, d) {
						if (btn.id === 'yes') {
							$dlg.find(".control-group").removeClass("error");
							$dlg.find(".help-inline").text("");
							
							// check
							var old = $old.find("input").val();
							var new1 = $new1.find("input").val();
							var new2 = $new2.find("input").val();
							
							if (!old) {
								$old.addClass("error");
								$old.find(".help-inline").text(errorTextMissing);
							}
							if (!new1) {
								$new1.addClass("error");
								$new1.find(".help-inline").text(errorTextMissing);
							}
							if (!new2) {
								$new2.addClass("error");
								$new2.find(".help-inline").text(errorTextMissing);
							}
							if (new1 && new2 && new1 != new2) {
								$new1.addClass("error");
								$new2.addClass("error");
								$new1.find(".help-inline").text(errorConfirm);
								$new2.find(".help-inline").text(errorConfirm);
							}
							if (!old || !new1 || !new2 || new1 != new2) return;
						}
						d.close();
						if (btn.id === 'yes') that.listener.onChangePassword(old, new1, function() {
							mollify.ui.views.dialogs.notification({message:mollify.ui.texts.get('mainviewChangePasswordSuccess')});
						});
					},
					"on-show": function(h, $d) {
						$dlg = $d;
						$old = $("#mollify-mainview-changepassword-old");
						$new1 = $("#mollify-mainview-changepassword-new1");
						$new2 = $("#mollify-mainview-changepassword-new2");
						
						$old.find("input").focus();
					}
				});
			}

			this.openAdminUtil = function(url) {
				mollify.ui.window.open(url);
			}
			
			this.getDataRequest = function(folder) {
				return $.extend({'core-parent-description': {}}, that.itemWidget.getDataRequest ? that.itemWidget.getDataRequest(folder) : {});
			}
			
			this.render = function(id) {
				mollify.dom.loadContentInto($("#"+id), mollify.templates.url("mainview.html"), that, ['localize', 'radio']);
			}
			
			this.onLoad = function() {
				$(window).resize(that.onResize);
				that.onResize();
		
				// TODO default view mode
				// TODO expose file urls
		
				mollify.dom.template("mollify-tmpl-main-username", mollify.session).appendTo("#mollify-mainview-user");
				if (mollify.session.authenticated) {
					mollify.ui.controls.dropdown({
						element: $('#mollify-username-dropdown'),
						items: false,
						onShow: function(drp, items) {
							if (items) return;
							
							that.listener.getSessionActions(function(a) {
								if (!a) {
									drp.hide();
									return;
								}
								drp.items(a);
							});
						}
					});
					//mollify.ui.controls.dropdown({element: $('#mollify-username-dropdown'), items: that.listener.sessionActions()});
				}
				var onSearch = function() {
					var val = $("#mollify-mainview-search-input").val();
					if (!val || val.length == 0) return;
					that.onSearch(val);
				};
				$("#mollify-mainview-search-input").keyup(function(e){
					if (e.which == 13) onSearch();
				});
				$("#mollify-mainview-search > button").click(onSearch);
				
				mollify.dom.template("mollify-tmpl-main-root-list", mollify.session.folders).appendTo("#mollify-mainview-rootlist");
				var $ri = $("#mollify-mainview-rootlist").find(".mollify-mainview-rootlist-item").click(function() {
					var root = $(this).tmplItem().data;
					that.onFolderSelected(root);
				});
				
				if (mollify.ui.draganddrop) {
					mollify.ui.draganddrop.enableDrop($ri.find("a"), {
						canDrop : function($e, e, obj) {
							if (!obj || obj.type != 'filesystemitem') return false;
							var item = obj.payload;
							var me = $e.tmplItem().data;
							return that.canDragAndDrop(me, item);
						},
						dropType : function($e, e, obj) {
							if (!obj || obj.type != 'filesystemitem') return false;
							var item = obj.payload;
							var me = $e.tmplItem().data;
							return that.dropType(me, item);
						},
						onDrop : function($e, e, obj) {
							if (!obj || obj.type != 'filesystemitem') return;
							var item = obj.payload;
							var me = $e.tmplItem().data;							
							that.onDragAndDrop(me, item);
						}
					});
				}
				
				that.controls["mollify-mainview-viewstyle-options"].set(that.viewStyle);
				that.initList();
				
				mollify.env.addEventHandler(that.onEvent);
				that.uploadProgress = new mollify.view.main.UploadProgress($("#mollify-mainview-progress"));
				
				if (mollify.ui.uploader && mollify.ui.uploader.initMainViewUploader) {
					//if (that._canWrite) mollify.ui.uploader.setMainViewUploadFolder(that._currentFolder);
					//else mollify.ui.uploader.setMainViewUploadFolder(false);
					if (mollify.ui.uploader && mollify.ui.uploader.initMainViewUploader) mollify.ui.uploader.initMainViewUploader({
						folder: that._currentFolder,
						container: $("#mollify"),
						dropElement: $("#mollify-folderview"),
				 		start: function(files, ready) {
					 		that.uploadProgress.show(ready);
				 		},
				 		progress: function(pr) {
					 		that.uploadProgress.set(pr);
				 		},
				 		finished: function() {
					 		that.uploadProgress.hide();
					 		that.listener.onRefresh();
				 		}
					});
				}
				
				$.each(mollify.plugins.getMainViewPlugins(), function(i, p) {
					p.onMainViewRender($("#mollify"));
				});
				
				that.listener.onViewLoaded();
			}
			
			this.onEvent = function(e) {
				if (e.type.substring(0, 11) != 'FILESYSTEM_') return;
				var files = e.payload;
				//TODO check if affects this view
				that.listener.onRefresh();
			};
			
			this.unload = function() {
				mollify.ui.hideActivePopup();
			};
			
			this.onSearch = function(s) {
				that.listener.onSearch(s, function(r) {
					$("#mollify-mainview-search-input").val("");
					$(".mollify-mainview-rootlist-item").removeClass("active");
					var $h = $("#mollify-folderview-header").empty();
					
					mollify.dom.template("mollify-tmpl-main-searchresults").appendTo($h);
					$("#mollify-searchresults-title-text").text(mollify.ui.texts.get('mainViewSearchResultsTitle', [""+r.count]));
					$("#mollify-searchresults-desc-text").text(mollify.ui.texts.get('mainViewSearchResultsDesc', [s]));
					
					//mollify.ui.process($h, ['localize']);
					
					var items = [];
					for (var id in r.matches) {
						items.push(r.matches[id].item);
					}
					
					that.viewType = 'search';
					that.searchResults = r;
					that.initList();
					that.updateItems(items, {});
				});
			};
			
			this.initSearchResultTooltip = function(items) {
				var prefix = mollify.ui.texts.get('mainViewSearchResultTooltipMatches');
				$(".mollify-filelist-item").each(function() {
		           $i = $(this);
		           var item = $i.tmplItem().data;
		           
		           var matchList = function(l) {
			           var r = "";
			           var first = true;
			           $.each(l, function(i, li) {
				           if (!first) r = r + ", ";
				           r = r + li.type;
				           first = false;
			           });
			           return r;
		           }
		           
		           $i.tooltip({
			           placement: "bottom",
			           title: prefix + matchList(that.searchResults.matches[item.id].matches),
			           trigger: "hover"
		           });
	            });	
			};
			
			this.onRadioChanged = function(groupId, valueId, i) {
				if (groupId == "mollify-mainview-viewstyle-options") that.onViewStyleChanged(valueId, i);
			};
			
			this.onViewStyleChanged = function(id, i) {
				that.viewStyle = i;
				that.initList();
				that.data(that.p);
			};
			
			this.onResize = function() {
				$("#mollify-folderview").height($(window).height()-$("#mollify-mainview-header").height());
			};
			
			this.showAllRoots = function() {
				that.folder();
				that.data({ items: mollify.session.folders });
			};
		
			this.showNoRoots = function() {
				console.log("showNoRoots");
			};
				
			this.showProgress = function() {
				$("#mollify-folderview-items").addClass("loading");
			};
		
			this.hideProgress = function() {
				$("#mollify-folderview-items").removeClass("loading");
			};
		
			this.onFolderSelected = function(f) {
				mollify.ui.hideActivePopup();
				that.listener.onFolderSelected(f);
			};
			
			this.onRetrieveUrl = function(url) {
				if (!that._currentFolder) return;
				
				that.showProgress();
				mollify.service.post("filesystem/"+that._currentFolder.id+"/retrieve", {url:url}, function(r) {
					that.hideProgress();
					that.listener.onRefresh();
				}, function(code, error) {
					that.hideProgress();
					//301 resource not found
					if (code == 301) {
						mollify.ui.views.dialogs.error({
							message: mollify.ui.texts.get('mainviewRetrieveFileResourceNotFound', [url])
						});
					} else {
						mollify.ui.views.dialogs.error({
							message: code + " " + error
						});
					}
				});
			};

			this.dropType = function(to, item) {
				var copy = (to.root_id != item.root_id);
				return copy ? "copy" : "move";
			};
						
			this.canDragAndDrop = function(to, item) {
				if (to.is_file) return false;
				//if (item.id == item.root_id) return false;
				if (item.id == to.id) return false;
				if (item.parent_id == to.id) return false;
				return true;
			};
			
			this.onDragAndDrop = function(to, item) {
				var copy = (that.dropType(to, item) == 'copy');
				console.log((copy ? "copy " : "move ") +item.name+" to "+to.name);
				
				if (copy) mollify.filesystem.copy(item, to);
				else mollify.filesystem.move(item, to);
			};
			
			this.folder = function(p) {
				that._currentFolder = p ? p.hierarchy[p.hierarchy.length-1] : false;
				that._canWrite = p ? p.canWrite : false;
				var currentRoot = p ? p.hierarchy[0] : false;
				
				$(".mollify-mainview-rootlist-item").removeClass("active");
				if (currentRoot) $("#mollify-mainview-rootlist-item-"+currentRoot.id).addClass("active");
				
				var $h = $("#mollify-folderview-header").empty();
				if (p) {					
					mollify.dom.template("mollify-tmpl-main-folder", {canWrite: p.canWrite, folder: that._currentFolder}).appendTo($h);
					that.setupHierarchy(p.hierarchy);
					
					var opt = {
						title: function() {
							return this.data.title ? this.data.title : mollify.ui.texts.get(this.data['title-key']);
						}
					};
					$t = $("#mollify-folder-tools");
					if (p.canWrite) {
						mollify.dom.template("mollify-tmpl-main-foldertools-action", { icon: 'icon-folder-close' }, opt).appendTo($t).click(function() {
							 mollify.ui.controls.dynamicBubble({element: $(this), content: mollify.dom.template("mollify-tmpl-main-createfolder-bubble"), handler: {
							 	onRenderBubble: function(b) {
							 		var $i = $("#mollify-mainview-createfolder-name-input");
							 		$("#mollify-mainview-createfolder-button").click(function(){
								 		var name = $i.val();
								 		if (!name) return;
								 		
								 		b.hide();
								 		that.listener.onCreateFolder(name);
							 		});
							 		$i.focus();
								}
							}});
							return false;
						});
						if (mollify.ui.uploader) mollify.dom.template("mollify-tmpl-main-foldertools-action", { icon: 'icon-download-alt' }, opt).appendTo($t).click(function() {
							mollify.ui.controls.dynamicBubble({element: $(this), content: mollify.dom.template("mollify-tmpl-main-addfile-bubble"), handler: {
							 	onRenderBubble: function(b) {
							 		mollify.ui.uploader.initUploadWidget($("#mollify-mainview-addfile-upload"), that._currentFolder, {
								 		start: function(files, ready) {
									 		b.hide(true);
									 		that.uploadProgress.show(ready);
								 		},
								 		progress: function(pr) {
									 		that.uploadProgress.set(pr);
								 		},
								 		finished: function() {
								 			b.hide();
									 		that.uploadProgress.hide();
									 		that.listener.onRefresh();
								 		}
							 		});
							 		if (!mollify.hasFeature('retrieve_url')) {
								 		$("#mollify-mainview-addfile-retrieve").remove();
							 		}
							 		var onRetrieve = function() {
								 		var val = $("#mollify-mainview-addfile-retrieve-url-input").val();
								 		if (!val || val.length < 1 || val.substring(0,4).toLowerCase().localeCompare('http') != 0) return false;
								 		b.close();
								 		that.onRetrieveUrl(val);
							 		};
							 		$("#mollify-mainview-addfile-retrieve-url-input").bind('keypress', function(e) {
										if ((e.keyCode || e.which) == 13) onRetrieve();
									});
							 		$("#mollify-mainview-addfile-retrieve-button").click(onRetrieve);
								}
							}});
							return false;
						});
						
						var actionsElement = mollify.dom.template("mollify-tmpl-main-foldertools-action", { icon: 'icon-cog', dropdown: true }, opt).appendTo($t);
						mollify.ui.controls.dropdown({
							element: actionsElement.find("li"),
							items: false,
							hideDelay: 0,
							style: 'submenu',
							onShow: function(drp, items) {
								if (items) return;
								
								that.getItemActions(that._currentFolder, function(a) {
									if (!a) {
										drp.hide();
										return;
									}
									drp.items(a);
								});
							}
						});
					}
					mollify.dom.template("mollify-tmpl-main-foldertools-action", { icon: 'icon-refresh' }, opt).appendTo($t).click(that.listener.onRefresh);
					$("#mollify-folderview-items").addClass("loading");
				} else {
					mollify.dom.template("mollify-tmpl-main-rootfolders").appendTo($h);
				}
				$("#mollify-folderview-items").css("top", $h.outerHeight()+"px");
				mollify.ui.process($h, ['localize']);
				
				if (mollify.ui.uploader && mollify.ui.uploader.setMainViewUploadFolder) mollify.ui.uploader.setMainViewUploadFolder(that._canWrite ? that._currentFolder : false);
				if (that.viewType != null) {
					that.viewType = null;
					that.initList();
				}
			};
						
			this.setupHierarchy = function(h) {
				var items = h;
				var p = $("#mollify-folder-hierarchy").append(mollify.dom.template("mollify-tmpl-main-folder-hierarchy", items));
				
				var rootItems = [];
				for(var i=0,j=mollify.session.folders.length; i<j;i++) {
					var root = mollify.session.folders[i];
					rootItems.push({
						title: root.name,
						callback: function(r) {
							return function() { that.onFolderSelected(r); };
						}(root)
					});
				}
				mollify.ui.controls.dropdown({
					element: $("#mollify-folder-hierarchy-item-root"),
					items: rootItems,
					hideDelay: 0,
					style: 'submenu'
				});
				
				var $hi = $(".mollify-folder-hierarchy-item").click(function() {
					var folder = $(this).tmplItem().data;
					var index = h.indexOf(folder);
					that.onFolderSelected(folder);
				});
				
				if (mollify.ui.draganddrop) {
					mollify.ui.draganddrop.enableDrop($hi.find("a"), {
						canDrop : function($e, e, obj) {
							if (!obj || obj.type != 'filesystemitem') return false;
							var item = obj.payload;
							var me = $e.parent().tmplItem().data;
							return that.canDragAndDrop(me, item);
						},
						dropType : function($e, e, obj) {
							if (!obj || obj.type != 'filesystemitem') return false;
							var item = obj.payload;
							var me = $e.tmplItem().data;
							return that.dropType(me, item);
						},
						onDrop : function($e, e, obj) {
							if (!obj || obj.type != 'filesystemitem') return;
							var item = obj.payload;
							var me = $e.parent().tmplItem().data;
							that.onDragAndDrop(me, item);
						}
					});
				}
			};
			
			this.isListView = function() { return that.viewStyle == 0; };
			
			this.initList = function() {
				if (that.isListView()) {
					var cols = mollify.settings["list-view-columns"];
					if (that.viewType) cols = mollify.settings["list-view-columns-"+that.viewType];
					that.itemWidget = new mollify.view.main.FileList('mollify-folderview-items', 'main', cols);
				} else {
					that.itemWidget = new mollify.view.main.IconView('mollify-folderview-items', 'main', that.viewStyle == 1 ? 'iconview-small' : 'iconview-large');
				}
				
				that.itemWidget.init({
					onFolderSelected : that.onFolderSelected,
					canDrop : that.canDragAndDrop,
					dropType : that.dropType,
					onDrop : that.onDragAndDrop,
					onClick: function(item, t, e) {
						//console.log(t);
						if (that.isListView() && t != 'icon') {
							var col = mollify.ui.filelist.columns[t];
							if (col["on-click"]) {
								col["on-click"](item);
								return;
							}
						}
						var showContext = (!that.isListView() || t=='name');
						if (showContext) {
							that.itemContext.open(item, that.itemWidget.getItemContextElement(item), that.itemWidget.getContainerElement());
						}
					},
					onDblClick: function(item) {
						if (item.is_file) return;
						that.onFolderSelected(item);
					},
					onRightClick: function(item, t, e) {
						that.showActionMenu(item, that.itemWidget.getItemContextElement(item));
					},
					onNativeDrag: function (item, e) {
						if (!item.is_file) return;
						console.log("drag "+item.id);

		                var url = mollify.service.url("filesystem/"+item.id);
		                e.originalEvent.dataTransfer.setData('DownloadURL',['application/octet-stream', item.name, url].join(':'));
		            },
		            onContentRendered : function(items, data) {
			            if (!that.isListView() || that.viewType != 'search') return;
			            that.initSearchResultTooltip(items);
		            }
				});
			};
			
			this.data = function(p) {
				that.p = p;
				$("#mollify-folderview-items").removeClass("loading");
				
				var descriptionExists = p.data && p.data['core-parent-description'];
				if (descriptionExists)
					$("#mollify-folder-description").text(p.data['core-parent-description']);
				
				var $dsc = $("#mollify-folder-description");
				var descriptionEditable = that._currentFolder && $dsc.length > 0 && mollify.session.features.descriptions && mollify.session.admin;
				if (descriptionEditable) {
					mollify.ui.controls.editableLabel({element: $dsc, hint: mollify.ui.texts.get('mainview-description-hint'), onedit: function(desc) {
						that.onDescription(that._currentFolder, desc);
					}});
				} else {
					if (!descriptionExists) $dsc.hide();
				}
				that.updateItems(p.items, p.data);
			};
			
			this.updateItems = function(items, data) {
				$("#mollify-folderview-items").css("top", $("#mollify-folderview-header").outerHeight()+"px");
				that.itemWidget.content(items, data);
			};
			
			this.showActionMenu = function(item, c) {
				c.addClass("open");
				var popup = mollify.ui.controls.popupmenu({ element: c, onHide: function() {
					c.removeClass("open");
					that.itemWidget.removeHover();
				}});
				
				that.getItemActions(item, function(a) {
					if (!a) {
						popup.hide();
						return;
					}
					popup.items(a);
				});
			};
			
			this.getItemActions = function(item, cb) {
				mollify.filesystem.itemDetails(item, mollify.plugins.getItemContextRequestData(item), function(d) {
					if (!d) {
						cb([]);
						return;
					}
					//var coreActions = a[1];
					var plugins = mollify.plugins.getItemContextPlugins(item, d);
					cb(mollify.helpers.cleanupActions(mollify.helpers.getPluginActions(plugins)));
				});
			};
						
			this.onDescription = function(item, desc) {
				//TODO validate
				mollify.service.put("filesystem/"+item.id+"/description/", {description: desc}, function(result) {},function(code, error) {
					alert(error);
				});
			};
		},
		
		main : {
			UploadProgress : function($e) {
				var t = this;
				t.$bar = $e.find(".bar");
				
				return {
					show : function(cb) {
						$e.css("bottom", "0px");
						t.$bar.css("width", "0%");
						$e.show().animate({"bottom": "30px"}, 500, cb);
					},
					set : function(progress, file) {
						t.$bar.css("width", progress+"%");
					},
					hide : function(cb) {
						setTimeout(function() {
							$e.animate({"bottom": "0px"}, 500, function() {
								t.$bar.css("width", "0%");
								$e.hide();
								if (cb) cb();
							});
						}, 1000);
					}
				}
			},
			IconView : function(container, id, cls) {
				var t = this;
				t.$c = $("#"+container);
				t.viewId = 'mollify-iconview-'+id;
				
				this.init = function(p) {
					t.p = p;
					
					mollify.dom.template("mollify-tmpl-iconview", {viewId: t.viewId}).appendTo(t.$c.empty());
					t.$l = $("#"+t.viewId);
					if (cls) t.$l.addClass(cls);
				};
				
				this.content = function(items, data) {
					t.items = items;
					t.data = data;
					
					mollify.dom.template("mollify-tmpl-iconview-item", items, {
						typeClass : function(item) {
							var c = item.is_file ? 'item-file' : 'item-folder';
							if (item.is_file && item.extension) c += ' item-type-'+item.extension;
							else if (!item.is_file && item.id == item.root_id) c += ' item-root-folder';
							return c;
						}
					}).appendTo(t.$l.empty());
					
					t.$l.find(".mollify-iconview-item").hover(function() {
						$(this).addClass("hover");
					}, function() {
						$(this).removeClass("hover");
					}).draggable({
						revert: "invalid",
						distance: 10,
						addClasses: false,
						zIndex: 2700
					}).droppable({
						hoverClass: "drophover",
						accept: function(i) { return t.p.canDrop ? t.p.canDrop($(this).tmplItem().data, $(i).tmplItem().data) : false; }
					}).bind("contextmenu",function(e){
						e.preventDefault();
						var $t = $(this);
						t.p.onRightClick($t.tmplItem().data, "", $t);
						return false;
					}).single_double_click(function() {
						var $t = $(this);
						t.p.onClick($t.tmplItem().data, "", $t);
					},function() {
						t.p.onDblClick($(this).tmplItem().data);
					}).attr('unselectable', 'on').css({
					   '-moz-user-select':'none',
					   '-webkit-user-select':'none',
					   'user-select':'none',
					   '-ms-user-select':'none'
					});
					
					t.p.onContentRendered(items, data);
				};
				
				/*this.getItemContextElement = function(item) {
					return t.$l.find("#mollify-iconview-item-"+item.id);
				};*/
				
				this.getItemContextElement = function(item) {
					return t.$l.find("#mollify-iconview-item-"+item.id);
				};
				
				this.getContainerElement = function() {
					return t.$l;	
				};
				
				this.removeHover = function() {
					t.$l.find(".mollify-iconview-item.hover").removeClass('hover');
				};
			},
			
			FileList: function(container, id, columns) {
				var t = this;
				t.minColWidth = 75;
				t.$c = $("#"+container);
				t.listId = 'mollify-filelist-'+id;
				t.cols = [];
				t.sortCol = false;
				t.sortOrderAsc = true;
				t.colWidths = {};
				
				for (var colId in columns) {
					var col = mollify.ui.filelist.columns[colId];
					if (!col) continue;
					
					var colSpec = $.extend({}, col, columns[colId]);
					t.cols.push(colSpec);
				};
				
				this.init = function(p) {
					t.p = p;
					mollify.dom.template("mollify-tmpl-filelist", {listId: t.listId}).appendTo(t.$c.empty());
					t.$l = $("#"+t.listId);
					t.$h = $("#"+t.listId+"-header-cols");
					t.$i = $("#"+t.listId+"-items");
					
					mollify.dom.template("mollify-tmpl-filelist-headercol", t.cols, {
						title: function(c) {
							var k = c['title-key'];
							if (!k) return "";
							
							return mollify.ui.texts.get(k);
						} 
					}).appendTo(t.$h);
					
					t.$h.find(".mollify-filelist-col-header").each(function(i) {
						var $t = $(this);
						var ind = $t.index();
						var col = t.cols[ind];
						
						$t.css("min-width", t.minColWidth);
						if (col.width) $t.css("width", col.width);
						
						$t.find(".mollify-filelist-col-header-title").click(function() {
							t.onSortClick(col);
						});
						
						if (i != (t.cols.length-1)) {
							$t.resizable({
								handles: "e",
								minWidth: t.minColWidth,
								//autoHide: true,
								start: function(e, ui) {
									var max = t.$c.width() - (t.cols.length * t.minColWidth);
									$t.resizable("option", "maxWidth", max);
								},
								stop: function(e, ui) {
									var w = $t.width();
									t.colWidths[col.id] = w;
									t.updateColWidth(col.id, w);
								}
							});/*.draggable({
								axis: "x",
								helper: "clone",
								revert: "invalid",
								distance: 30
							});*/
						}
					});
					t.items = [];
					t.data = {};
					t.onSortClick(t.cols[0]);
				};
			
				this.updateColWidths = function() {
					for (var colId in t.colWidths) t.updateColWidth(colId, t.colWidths[colId]);
				};
					
				this.updateColWidth = function(id, w) {
					$(".mollify-filelist-col-"+id).width(w);
				};
				
				this.onSortClick = function(col) {
					if (col.id != t.sortCol.id) {
						t.sortCol = col;
						t.sortOrderAsc = true;
					} else {
						t.sortOrderAsc = !t.sortOrderAsc;
					}
					t.refreshSortIndicator();
					t.content(t.items, t.data);
				};
				
				this.sortItems = function() {
					var s = t.sortCol.sort;
					t.items.sort(function(a, b) {
						return s(a, b, t.sortOrderAsc ? 1 : -1, t.data);
					});
				};
				
				this.refreshSortIndicator = function() {
					t.$h.find(".mollify-filelist-col-header").removeClass("sort-asc").removeClass("sort-desc");
					$("#mollify-filelist-col-header-"+t.sortCol.id).addClass("sort-" + (t.sortOrderAsc ? "asc" : "desc"));
				};
				
				this.getDataRequest = function(item) {
					var rq = {};
					for (var i=0, j=t.cols.length; i<j; i++) {
						var c = t.cols[i];
						if (c['request-id']) rq[c['request-id']] = {};
					}
					return rq;
				};
				
				this.content = function(items, data) {
					t.items = items;
					t.data = data;
					t.sortItems();
					
					mollify.dom.template("mollify-tmpl-filelist-item", items, {
						cols: t.cols,
						typeClass : function(item) {
							var c = item.is_file ? 'item-file' : 'item-folder';
							if (item.is_file && item.extension) c += ' item-type-'+item.extension;
							else if (!item.is_file && item.id == item.root_id) c += ' item-root-folder';
							return c;
						},
						col: function(item, col) {
							return col.content(item, t.data);
						},
						itemColStyle: function(item, col) {
							var style="min-width:"+t.minColWidth+"px";
							if (col.width) style = style+";width:"+col.width+"px";
							return style;
						}
					}).appendTo(t.$i.empty());
					
					for (var i=0,j=t.cols.length; i<j; i++) {
						var col = t.cols[i];
						if (col["on-render"]) col["on-render"]();
					}
					
					var $items = t.$i.find(".mollify-filelist-item");
					$items.hover(function() {
						$(this).addClass("hover");
					}, function() {
						$(this).removeClass("hover");
					}).bind("contextmenu",function(e){
						e.preventDefault();
						t.onItemClick($(this), $(e.srcElement), false);
						return false;
					}).single_double_click(function(e) {
						e.preventDefault();
						e.stopPropagation();
						t.onItemClick($(this), $(e.srcElement), true);
						return false;
					},function() {
						t.p.onDblClick($(this).tmplItem().data);
					});
					
					if (mollify.ui.draganddrop) {
						mollify.ui.draganddrop.enableDrag($items, {
							onDragStart : function($e, e) {
								var item = $e.tmplItem().data;
								if (Modernizr.draganddrop) t.p.onNativeDrag($e.tmplItem().data, e);
								//if (item.id == item.root_id) return false;
								return {type:'filesystemitem', payload: item};
							}
						});
						mollify.ui.draganddrop.enableDrop(t.$i.find(".mollify-filelist-item.item-folder"), {
							canDrop : function($e, e, obj) {
								if (!t.p.canDrop || !obj || obj.type != 'filesystemitem') return false;
								var item = obj.payload;
								var me = $e.tmplItem().data;
								return t.p.canDrop(me, item);
							},
							dropType : function($e, e, obj) {
								if (!t.p.dropType || !obj || obj.type != 'filesystemitem') return false;
								var item = obj.payload;
								var me = $e.tmplItem().data;
								return t.p.dropType(me, item);
							},
							onDrop : function($e, e, obj) {
								if (!obj || obj.type != 'filesystemitem') return;
								var item = obj.payload;
								var me = $e.tmplItem().data;
								if (t.p.onDrop) t.p.onDrop(me, item);
							}
						});
					}
					
					/*.click(function(e) {
						e.preventDefault();
						t.onItemClick($(this), $(e.srcElement), true);
						return false;
					})*/
			
					/*t.$i.find(".mollify-filelist-quickmenu").click(function(e) {
						e.preventDefault();
						var $t = $(this);
						t.p.onMenuOpen($t.tmplItem().data, $t);
					});*/
			
					/*t.$i.find(".mollify-filelist-item-name-title").click(function(e) {
						e.preventDefault();
						t.p.onClick($(this).tmplItem().data, "name");
					});*/
					/*t.$i.find(".item-folder .mollify-filelist-item-name-title").click(function(e) {
						e.preventDefault();
						t.p.onFolderSelected($(this).tmplItem().data);
					});*/
					
					t.updateColWidths();
					
					t.p.onContentRendered(items, data);
				};
				
				this.onItemClick = function($item, $el, left) {
					var i = $item.find(".mollify-filelist-col").index($el.closest(".mollify-filelist-col"));
					if (i<0) return;
					var colId = (i == 0 ? "icon" : t.cols[i-1].id);
					if (left)
						t.p.onClick($item.tmplItem().data, colId, $item);
					else
						t.p.onRightClick($item.tmplItem().data, colId, $item);
				};
					
				this.getItemContextElement = function(item) {
					var $i = t.$i.find("#mollify-filelist-item-"+item.id);
					return $i.find(".mollify-filelist-col-name") || $i; 
				};
				
				this.getContainerElement = function() {
					return t.$i;	
				};
				
				this.removeHover = function() {
					t.$i.find(".mollify-filelist-item.hover").removeClass('hover');
				};
			}
		}
	}
});})(window.jQuery);
