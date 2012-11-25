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
				that.roots = p.roots;
				that.listener = p.listener;
				
				that.rootsById = {};
				for (var i=0,j=p.roots.length; i<j; i++)
					that.rootsById[p.roots[i].id] = p.roots[i];
			}
			
			this.getDataRequest = function(folder) {
				return that.itemWidget.getDataRequest ? that.itemWidget.getDataRequest(folder) : {};
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
				if (mollify.session.authenticated) mollify.ui.controls.dropdown({element: $('#mollify-username-dropdown'), items: that.sessionActions()});
				mollify.dom.template("mollify-tmpl-main-root-list", that.roots).appendTo("#mollify-mainview-rootlist");
				$(".mollify-mainview-rootlist-item").click(function() {
					var root = $(this).tmplItem().data;
					that.listener.onFolderSelected(1, root);
				});
				
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
				
				that.listener.onViewLoaded();
			}
			
			this.onEvent = function(e) {
				if (e.type.substring(0, 11) != 'FILESYSTEM_') return;
				var files = e.payload;
				//TODO check if affects this view
				that.listener.onRefresh();
			};
			
			this.unload = function() {
				
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
			
			this.sessionActions = function() {
				//TODO get session actions
				return [
					{'title-key': 'logout', callback: function() { } }
				];
			};
			
			this.showAllRoots = function() {
				that.folder();
				that.data({ items: that.roots });
			};
		
			this.showNoRoots = function() {
				console.log("showNoRoots");
			};
				
			this.showProgress = function() {
				console.log("showProgress");
			};
		
			this.hideProgress = function() {
				console.log("hideProgress");
			};
		
			this.onFolderSelected = function(f) {
				mollify.ui._hideActivePopup();
				that.listener.onSubFolderSelected(f);
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
								}
							}});
							return false;
						});
						
						var actionsElement = mollify.dom.template("mollify-tmpl-main-foldertools-action", { icon: 'icon-cog', dropdown: true }, opt).appendTo($t);
						var folderActions = mollify.ui.controls.dropdown({
							element: actionsElement.find("li"),
							items: false,
							hideDelay: 0,
							style: 'submenu',
							onShow: function(drp, items) {
								if (items) return;
								
								that.getItemActions(that._currentFolder, function(a) {
									/*if (!a) {
										popup.hide();
										return;
									}*/
									drp.items(a);
								});
							},
							onItem: function() {
							},
							onBlur: function(dd) {
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
			};
			
			this.onUpload = function() {
				mollify.ui.uploader.open(that.currentFolder);
			};
			
			this.setupHierarchy = function(h) {
				var items = $.merge([{id: 'root', name: ''}], h);
				
				var p = $("#mollify-folder-hierarchy").empty();
				
				mollify.dom.template("mollify-tmpl-main-folder-hierarchy", items).appendTo(p);
				$("#mollify-folder-hierarchy-root").click(that.listener.onHomeSelected);
				$(".mollify-folder-hierarchy-item").click(function() {
					var index = p.find(".mollify-folder-hierarchy-item").index($(this));
					that.listener.onFolderSelected(index, h[index-1]);
				});
			};
			
			this.isListView = function() { return that.viewStyle == 0; };
			
			this.initList = function() {
				if (that.isListView()) {
					that.itemWidget = new mollify.view.main.FileList('mollify-folderview-items', 'main', mollify.settings["list-view-columns"]);
				} else {
					that.itemWidget = new mollify.view.main.IconView('mollify-folderview-items', 'main', that.viewStyle == 1 ? 'iconview-small' : 'iconview-large');
				}
				
				that.itemWidget.init({
					onFolderSelected : that.listener.onSubFolderSelected,
					canDrop : function(to, item) {
						if (to.id == to.root_id || to.is_file) return false;
						if (item.id == to.id) return false;
						return true;
					},
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
							that.openItemContext(item, that.itemWidget.getItemContextElement(item));
						}
					},
					onDblClick: function(item) {
						if (item.is_file) return;
						that.listener.onSubFolderSelected(item);
					},
					onRightClick: function(item, t, e) {
						that.showActionMenu(item, that.itemWidget.getItemContextElement(item));
					},
					onNativeDrag: function (item, e) {
						if (!item.is_file) return;
						console.log("drag "+item.id);

		                var url = mollify.service.url("filesystem/"+item.id);
		                e.originalEvent.dataTransfer.setData('DownloadURL',['application/octet-stream', item.name, url].join(':'));
		            }
				});
			};
			
			this.data = function(p) {
				that.p = p;
				$("#mollify-folderview-items").removeClass("loading");
				that.itemWidget.content(p.items, p.data);
			};
			
			this.showActionMenu = function(item, c) {
				c.addClass("open");
				var popup = mollify.ui.controls.popupmenu({ element: c, onHide: function() { c.removeClass("open"); that.itemWidget.removeHover(); }});
				
				that.getItemActions(item, function(a) {
					if (!a) {
						popup.hide();
						return;
					}
					popup.items(a);
				});
			};
			
			this.getItemActions = function(item, cb) {
				that.listener.getItemDetails(item, function(a) {
					if (!a) {
						cb([]);
						return;
					}
					var coreActions = a[1];
					var pluginData = mollify.plugins.getItemContextData(item, a[0]);
					cb(that.addPluginActions(a[1], pluginData));
				});
			};
			
			this.addPluginActions = function(actions, pluginData) {
				var list = actions;
				if (pluginData) {
					for (var id in pluginData) {
						var pd = pluginData[id];
						if (pd.actions) {
							list.push({title:"-"});
							$.merge(list, pd.actions);
						}
					}
				}
				return list;
			};
		
			this.getPrimaryActions = function(actions) {
				if (!actions) return [];
				var result = [];
				for (var i=0,j=actions.length; i<j; i++)
					if (actions[i].id == 'download') result.push(actions[i]);
				return result;
			};
				
			this.openItemContext = function(item, e) {
				var popupId = "mainview-itemcontext-"+item.id;
				if (mollify.ui.isActivePopup(popupId)) {
					return;
				}
				
				var html = $("<div/>").append(mollify.dom.template("mollify-tmpl-main-itemcontext", item, {})).html();
				e.popover({
					title: item.name,
					html: true,
					placement: 'bottom',
					trigger: 'manual',
					template: '<div class="popover mollify-itemcontext-popover"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content"><p></p></div></div></div>',
					content: html,
					manualout: true,
					onshow: function($t) {
						mollify.ui.activePopup({ id: popupId, hide: function() { e.popover('destroy'); } });
						
						var closeButton = $('<button type="button" class="close">×</button>').click(function(){
							e.popover('destroy');
						});
						$t.find('.popover-title').append(closeButton);
						var $el = $("#mollify-itemcontext-"+item.id);
						var $content = $el.find(".mollify-itemcontext-content");
						
						that.listener.getItemDetails(item, function(a) {
							if (!a) {
								$t.hide();
								return;
							}
							
							that.renderItemContext($t, $content, item, a);
						});
					},
					onhide: function($t) {
						mollify.ui.removeActivePopup(popupId);
						//e.popover('destroy');
					}
				});
				e.popover('show');
				/*that.itemContext = e.qtip({
					content: html,
					position: {
						my: that.isListView() ? 'top left' : 'top center',
						at: that.isListView() ? 'bottom left' : 'bottom center',
					},
					hide: {
						delay: 1000,
						fixed: true,
						event: ''//'mouseleave'
					},
					style: {
						widget: false,
						tip: {
							corner: true,
							width: 20,
							height: 20
						},
						classes: 'ui-tooltip-light ui-tooltip-shadow ui-tooltip-rounded ui-tooltip-tipped mollify-itemcontext-popup mollify-popup'
					},
					events: {
						render: function(e, api) {
		
						},
						visible: function(e, api) {
							var $t = $(this);
							var $el = $("#mollify-itemcontext-"+item.id);
							var $content = $el.find(".mollify-itemcontext-content");
							
							that.listener.getItemDetails(item, function(a) {
								if (!a) {
									api.hide();
									return;
								}
								
								that.renderItemContext(api, $content, item, a);
							});
						},
						hide: function(e, api) {
							that.itemContext = false;
							api.destroy();
						}
					}
				}).qtip('api');
				
				that.itemContext.show();*/
			};
			
			this.renderItemContext = function(tip, $e, item, d) {
				var details = d[0];
				var showDescription = mollify.session.features.descriptions && !!details.description;
				//TODO permissions to edit descriptions
				var descriptionEditable = showDescription && mollify.session.admin;
				
				var pluginData = mollify.plugins.getItemContextData(item, details);
				var actions = that.addPluginActions(d[1], pluginData);
				var primaryActions = that.getPrimaryActions(actions);
				
				/*if (primaryActionIndex >= 0) {
					primaryAction = secondaryActions[primaryActionIndex];
					secondaryActions.splice(primaryActionIndex,1);
					var i=0;
					while(true) {
						if (secondaryActions.length == i || secondaryActions[i].type == 'action') break;
						i++;
					}
					if (i > 0) secondaryActions.splice(0,i);
				}*/
				var o = {item:item, details:d[0], description: (showDescription ? details.description : false), session: mollify.session, plugins: pluginData};
				
				$e.removeClass("loading").empty().append(mollify.dom.template("mollify-tmpl-main-itemcontext-content", o, {}));
				$e.click(function(e){
					// prevent from closing the popup when clicking the popup itself
					e.preventDefault();
					return false;
				});
				mollify.ui.process($e, ["localize"]);
				
				if (descriptionEditable) {
					mollify.ui.controls.editableLabel({element: $("#mollify-itemcontext-description"), onedit: function(desc) {
						that.onDescription(item, desc);
					}});
				}
				
				if (primaryActions) {
					var $c = $("#mollify-itemcontext-primary-actions");
					
					var opt = {
						title: function() {
							return this.data.title ? this.data.title : mollify.ui.texts.get(this.data['title-key']);
						}
					};
					for (var i=0; i<primaryActions.length;i++) {
						var action = primaryActions[i];
						mollify.dom.template("mollify-tmpl-main-itemcontext-primaryaction", action, opt).appendTo($c).click(function() {
							tip.hide();
							action.callback();
						});
					}
				} else {
					//$("#mollify-itemcontext-primary-actions").hide();
				}
				
				if (pluginData) {
					var $selectors = $("#mollify-itemcontext-details-selectors");
					var $content = $("#mollify-itemcontext-details-content");
					var onSelectDetails = function(id) {
						$(".mollify-itemcontext-details-selector").removeClass("active");
						$("#mollify-itemcontext-details-selector-"+id).addClass("active");
						pluginData[id].details["on-render"]($content.empty(), cache[id]);
					};
					var firstPlugin = false;
					var cache = {};
					for (var id in pluginData) {
						var data = pluginData[id];
						if (!data.details) continue;
						
						if (!firstPlugin) firstPlugin = id;
						cache[id] = {};
						var title = data.details.title ? data.details.title : (data.details["title-key"] ? mollify.ui.texts.get(data.details["title-key"]) : id);
						var selector = mollify.dom.template("mollify-tmpl-main-itemcontext-details-selector", {id: id, title:title, data: data}).appendTo($selectors).click(function() { onSelectDetails(id); });
					}
					/*$e.find(".mollify-itemcontext-details-selector").hover(function() {
						$(this).addClass("hover");
					}, function() {
						$(this).removeClass("hover");
					});*/
					if (firstPlugin) onSelectDetails(firstPlugin);
				}
				
				var actions = mollify.ui.controls.dropdown({
					element: $e.find("#mollify-itemcontext-secondary-actions"),
					items: actions,
					hideDelay: 0,
					style: 'submenu',
					onItem: function() {
						tip.hide();
					},
					onBlur: function(dd) {
						dd.hide();
					}
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
				};
				
				this.getItemContextElement = function(item) {
					return t.$l.find("#mollify-iconview-item-"+item.id);
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
					t.cols.push(col);
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
								return {type:'filesystemitem', payload: item};
							}
						});
						mollify.ui.draganddrop.enableDrop(t.$i.find(".mollify-filelist-item.item-folder"), {
							canDrop : function($e, e, obj) {
								if (!obj || obj.type != 'filesystemitem') return false;
								var item = obj.payload;
								var me = $e.tmplItem().data;
								return t.p.canDrop ? t.p.canDrop(me, item) : false;
							},
							onDrop : function($e, e, obj) {
								if (!obj || obj.type != 'filesystemitem') return;
								var item = obj.payload;
								var me = $e.tmplItem().data;
								alert("dropped "+item.name+" on "+me.name);
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
					return t.$i.find("#mollify-filelist-item-"+item.id+" .mollify-filelist-col-name");
				};
				
				this.removeHover = function() {
					t.$i.find(".mollify-filelist-item.hover").removeClass('hover');
				};
			}
		}
	}
});})(window.jQuery);
