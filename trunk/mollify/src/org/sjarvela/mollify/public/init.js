(function($){window.mollify = new function() {
	var defaults = {
		"template-url": "client/templates/",
		"service-path": "backend/",
		"list-view-columns": {
			"name": { width: 250 },
			"size": {},
			"file-modified": { width: 150 }
		},
		"list-view-columns-search": {
			"name": { width: 250 },
			"path": { width: 150 },
			"size": {}
		}
	};
	var t = this;
	t.time = new Date().getTime();
	
	this.settings = {};

	this.init = function(s, p) {
		t.plugins.register(new mollify.plugin.Core());
		if (p) {
			for (var i=0, j=p.length; i < j; i++)
				t.plugins.register(p[i]);
		}
		
		t.settings = $.extend({}, defaults, s);
	}
	
	this.setup = function(core) {
		t.hiddenInd = 0;
		t.core = core;	//TODO remove this when GWT is gone
		t.ui.texts = core.texts();
		t.service = core.service();
		t.session = false;
		//t.filesystem = core.filesystem();
		
		core.addEventHandler(function(e) {
			if (e.type == 'SESSION_START') {
				t.session = core.session.get();
				mollify.filesystem.init(mollify.session.folders);
				t._start();
			} else if (e.type == 'SESSION_END') {
				t.session = false;
				mollify.filesystem.init([]);
				t._start();
			}
		});

		if (t.texts.locale) $("#mollify").addClass("lang-"+t.texts.locale);
		
		t.ui.filelist.addColumn({
			"id": "name",
			"title-key": "fileListColumnTitleName",
			"sort": function(i1, i2, sort, data) {
				return i1.name.toLowerCase().localeCompare(i2.name.toLowerCase()) * sort;
			},
			"content": function(item, data) {
				return item.name;
			}
		});
		t.ui.filelist.addColumn({
			"id": "path",
			"title-key": "fileListColumnTitlePath",
			"sort": function(i1, i2, sort, data) {
				var p1 = mollify.filesystem.rootsById[i1.root_id].name + i1.path;
				var p2 = mollify.filesystem.rootsById[i2.root_id].name + i2.path;
				return p1.toLowerCase().localeCompare(p2.toLowerCase()) * sort;
			},
			"content": function(item, data) {
				return '<span class="item-path-root">'+mollify.filesystem.rootsById[item.root_id].name + '</span>: <span class="item-path-val">' + item.path + '</span>';
			}
		});
		t.ui.filelist.addColumn({
			"id": "type",
			"title-key": "fileListColumnTitleType",
			"sort": function(i1, i2, sort, data) {
				return i1.extension.toLowerCase().localeCompare(i2.extension.toLowerCase()) * sort;
			},
			"content": function(item, data) {
				return item.extension;
			}
		});
		t.ui.filelist.addColumn({
			"id": "size",
			"title-key": "fileListColumnTitleSize",
			"sort": function(i1, i2, sort, data) {
				var s1 = i1.is_file ? parseInt(i1.size) : 0;
				var s2 = i2.is_file ? parseInt(i2.size) : 0;
				return (s1-s2) * sort;
			},
			"content": function(item, data) {
				return item.is_file ? mollify.ui.texts.formatSize(item.size) : '';
			}
		});
		t.ui.filelist.addColumn({
			"id": "file-modified",
			"request-id": "core-file-modified",
			"title-key": "fileListColumnTitleLastModified",
			"sort": function(i1, i2, sort, data) {
				if (!i1.is_file && !i2.is_file) return 0;
				if (!data || !data["core-file-modified"]) return 0;
				
				var ts1 = data["core-file-modified"][i1.id] ? data["core-file-modified"][i1.id] * 1 : 0;
				var ts2 = data["core-file-modified"][i2.id] ? data["core-file-modified"][i2.id] * 1 : 0;
				return ((ts1 > ts2) ? 1 : -1) * sort;
			},
			"content": function(item, data) {
				if (!item.id || !item.is_file || !data || !data["core-file-modified"] || !data["core-file-modified"][item.id]) return "";
				return mollify.ui.texts.formatInternalTime(data["core-file-modified"][item.id]);
			}
		});
		t.ui.filelist.addColumn({
			"id": "item-description",
			"request-id": "core-item-description",
			"title-key": "fileListColumnTitleDescription",
			"sort": function(i1, i2, sort, data) {
				if (!i1.is_file && !i2.is_file) return 0;
				if (!data || !data["core-item-description"]) return 0;
				
				var d1 = data["core-item-description"][i1.id] ? data["core-item-description"][i1.id] : '';
				var d2 = data["core-item-description"][i2.id] ? data["core-item-description"][i2.id] : '';
				return ((d1 > d2) ? 1 : -1) * sort;
			},
			"content": function(item, data) {
				if (!item.id || !data || !data["core-item-description"] || !data["core-item-description"][item.id]) return "";
				var desc = data["core-item-description"][item.id];
				var stripped = desc.replace(/<\/?[^>]+(>|$)/g, '');
				return '<div class="item-description-container" title="'+stripped+'">'+desc+'</div>';
			}
		});
		
		t.ui.dialogs = new mollify.view.DialogHandler(t.filesystem);
		
		/*t.ui.views = {
			login : new mollify.view.LoginView(),
			mainview : new mollify.view.MainView(),
			
			dialogs : new mollify.view.DialogHandler(t.filesystem)
		}*/
		core.views().registerHandlers({ dialogs : t.ui.dialogs });
		t.plugins.initialize(core);
		t.templates.load("dialogs.html");
			
		if (!mollify.ui.draganddrop) mollify.ui.draganddrop = (Modernizr.draganddrop) ? new mollify.MollifyHTML5DragAndDrop() : new mollify.MollifyJQueryDragAndDrop();
		if (!mollify.ui.uploader) mollify.ui.uploader = new mollify.plugin.MollifyUploader();
		
		$("body").click(function(e) {
			// hide popups when clicked outside
			if (t.ui._activePopup) {
				if (e && e.srcElement && t.ui._activePopup.element) {
					var popupElement = t.ui._activePopup.element();
					if (popupElement.has($(e.srcElement)).length > 0) return;
				}
				t.ui.hideActivePopup();
			}
		});

		//$.datepicker.setDefaults({
		//	dateFormat: e.texts().get('shortDateFormat').replace(/yyyy/g, 'yy')
		//});
		mollify.service.get("session/info/3", function(s) {
			core.session.set(s);
		}, function(c, e) {
			alert(c);	//TODO
		});
	};
	
	this._start = function() {
		var $c = $("#mollify");
		if (!mollify.session.authenticated) {
			new mollify.view.LoginView().init($c);
		} else {
			new mollify.view.MainView().init($c);
		}
	};
	
	this.events = new function() {
		var et = this;
		this._handlers = [];
		
		this.addEventHandler = function(h) {
			et._handlers.push(h);
		};
		
		this.dispatchEvent = function(e) {
			$.each(et._handlers, function(i, h) {
				h(e);
			});
		};
	};
	
	this.filesystem = new function() {
		var ft = this;
		
		this.init = function(f) {
			ft.roots = [];
			ft.rootsById = {};
			
			if (f && t.session.authenticated) {
				ft.roots = f;
				for (var i=0,j=f.length; i<j; i++)
					ft.rootsById[f[i].id] = f[i];
			}
		};
		
		this.itemDetails = function(item, data, cb, err) {
			mollify.service.post("filesystem/"+f.id+"/details/", { data : data }, cb, err);
		};
		
		this.folderInfo = function(f, hierarchy, data, cb, err) {
			mollify.service.post("filesystem/"+f.id+"/info/" + (hierarchy ? "?h=1" : ""), { data : data }, cb, err);
		};
		
		this.folders = function(parent, cb, err) {
			mollify.service.get("filesystem/"+f.id+"/folders/", cb, err);
		};
		
		this.copy = function(items, to, cb, err) {
			
		};
		
		this.move = function(items, to, cb, err) {
			
		};
		
		this.rename = function(item, name, cb, err) {
			
		};
		
		this.remove = function(items, cb, err) {
			
		};
	};
	
	this.plugins = new function() {
		var pl = this;
		this.list = {};
		
		this.register = function(p) {
			var id = p.id;
			if (!id) return;
			
			pl.list[id] = p;
		};
		
		this.initialize = function(core) {
			for (var id in pl.list)
				pl.list[id].initialize(core);
		};
		
		this.get = function(id) {
			if (!def(id)) return pl.list;
			return pl.list[id];
		};
		
		this.exists = function(id) {
			return !!pl.list[id];
		};
		
		this.url = function(id, p) {
			return t.service.pluginUrl(id)+"client/"+p;
		};
		
		this.getItemContextRequestData = function(item) {
			var requestData = {};
			for (var id in pl.list) {
				var plugin = pl.list[id];
				if (!plugin.itemContextRequestData) continue;
				var data = plugin.itemContextRequestData(item);
				if (!data) continue;
				requestData[id] = data;
			}
			return requestData;
		};
		
		this.getItemContextPlugins = function(item, d) {
			var data = {};
			if (!d || !d.plugins) return data;
			for (var id in pl.list) {
				var plugin = pl.list[id];
				if (!plugin.itemContextHandler) continue;
				var pluginData = plugin.itemContextHandler(item, d, d.plugins[id]);
				if (pluginData) data[id] = pluginData;
			}
			return data;
		};
		
		this.getItemCollectionPlugins = function(items) {
			var data = {};
			if (!items || !isArray(items) || items.length < 1) return data;
			
			for (var id in pl.list) {
				var plugin = pl.list[id];
				if (!plugin.itemCollectionHandler) continue;
				var pluginData = plugin.itemCollectionHandler(items);
				if (pluginData) data[id] = pluginData;
			}
			return data;
		};
		
		this.getMainViewPlugins = function() {
			var plugins = [];
			for (var id in pl.list) {
				var plugin = pl.list[id];
				if (!plugin.onMainViewRender) continue;
				plugins.push(plugin);
			}
			return plugins;
		};
	};
	
	this.hasFeature = function(id) {
		return t.session.features && t.session.features[id];
	};

	this.locale = function() {
		return t.texts.locale;
	};
			
	this.urlWithParam = function(url, param) {
		return url + (strpos(url, "?") ? "&" : "?") + param;
	}
	
	this.noncachedUrl = function(url) {
		return t.urlWithParam(url, "_="+t.time);
	}
	
	this.templates = {
		loaded: [],
		
		url : function(name) {
			var base = t.settings["template-url"] || 'client/templates/';
			return t.noncachedUrl(base + name);
		},
		
		load : function(name, url, cb) {
			if (t.templates.loaded.indexOf(name) >= 0) {
				if (cb) cb();
				return;
			}
			
			$.get(url ? url : t.templates.url(name), function(h) {
				t.templates.loaded.push(name);
				$("body").append(h);
				if (cb) cb();
			});
		}
	}
	
	this.dom = {
		hiddenLoaded : [],
		
		importScript : function(url) {
			$.getScript(url);
		},
		
		importCss : function(url) {
			var link = $("<link>");
			link.attr({
		    	type: 'text/css',
		    	rel: 'stylesheet',
		    	href: t.noncachedUrl(url)
			});
			$("head").append(link);
		},

		loadContent : function(contentId, url, cb) {
			if (t.dom.hiddenLoaded.indexOf(contentId) >= 0) {
				if (cb) cb();
				return;
			}
			var id = 'mollify-tmp-'+(t.hiddenInd++);
			$('<div id="'+id+'" style="display:none"/>').appendTo($("body")).load(t.urlWithParam(url, "_="+mollify.time), function() {
				t.dom.hiddenLoaded.push(contentId);
				if (cb) cb();
			});
		},
					
		loadContentInto : function($target, url, handler, process) {
			$target.load(t.urlWithParam(url, "_="+mollify.time), function() {
				if (process) t.ui.process($target, process, handler);
				if (typeof handler === 'function') handler();
				else if (handler.onLoad) handler.onLoad($target);
			});
		},
		
		template : function(id, data, opt) {
			return $("#"+id).tmpl(data, opt);
		}
	};
	
	this.helpers = {
		getPluginActions : function(plugins) {
			var list = [];
			if (plugins) {
				for (var id in plugins) {
					var p = plugins[id];
					if (p.actions) {
						list.push({title:"-",type:'separator'});
						$.merge(list, p.actions);
					}
				}
			}
			return list;
		},
	
		getPrimaryActions : function(actions) {
			if (!actions) return [];
			var result = [];
			for (var i=0,j=actions.length; i<j; i++) {
				var a = actions[i];
				if (a.id == 'download' || a.type == 'primary') result.push(a);
			}
			return result;
		},

		getSecondaryActions : function(actions) {
			if (!actions) return [];
			var result = [];
			for (var i=0,j=actions.length; i<j; i++) {
				var a = actions[i];
				if (a.id == 'download' || a.type == 'primary') continue;
				result.push(a);
			}
			return mollify.helpers.cleanupActions(result);
		},
		
		cleanupActions : function(actions) {
			if (!actions) return [];				
			var last = -1;
			for (var i=actions.length-1,j=0; i>=j; i--) {
				var a = actions[i];
				if (a.type != 'separator' && a.title != '-') {
					last = i;
					break;
				}
			}
			if (last < 0) return [];
			
			var first = -1;
			for (var i=0; i<=last; i++) {
				var a = actions[i];
				if (a.type != 'separator' && a.title != '-') {
					first = i;
					break;
				}
			}
			actions = actions.splice(first, (last-first)+1);
			var prevSeparator = false;
			for (var i=actions.length-1,j=0; i>=j; i--) {
				var a = actions[i];
				var separator = (a.type == 'separator' || a.title == '-');
				if (separator && prevSeparator) actions.splice(i, 1);
				prevSeparator = separator;
			}
			
			return actions;
		}
	};
	
	this.ui = {
		hideActivePopup : function() {
			if (t.ui._activePopup) t.ui._activePopup.hide();
			t.ui._activePopup = false;
		},
		activePopup : function(p) {
			if (p==undefined) return t.ui._activePopup;
			if (t.ui._activePopup) {
				if (p.parentPopupId && t.ui._activePopup.id == p.parentPopupId) return;
				t.ui._activePopup.hide();
			}
			t.ui._activePopup = p;
			if (!t.ui._activePopup.id) t.ui._activePopup.id = new Date().getTime();
			return t.ui._activePopup.id;
		},
		isActivePopup : function(id) {
			return (t.ui._activePopup && t.ui._activePopup.id == id);
		},
		removeActivePopup : function(id) {
			if (!id || !t.ui.isActivePopup(id)) return;
			t.ui._activePopup = false;
		},
		
		uploader : false,
		
		draganddrop : false,
		
		filelist : {
			columns : [],
			addColumn : function(c) {
				t.ui.filelist.columns[c.id] = c;
			}
		},
		
		itemContext : function(o) {
			var ict = this;
			
			this.open = function(item, $e, $c, $t) {
				var popupId = "mainview-itemcontext-"+item.id;
				if (mollify.ui.isActivePopup(popupId)) {
					return;
				}
				
				var openedId = false;
				if (ict._activeItemContext) {
					var openedId = ict._activeItemContext.item.id;
					ict._activeItemContext.close();
					ict._activeItemContext = false;
				}
				if (item.id == openedId) return;
				
				var $cont = $t || $e.parent();				
				var html = mollify.dom.template("mollify-tmpl-main-itemcontext", item, {})[0].outerHTML;
				$e.popover({
					title: item.name,
					html: true,
					placement: 'bottom',
					trigger: 'manual',
					template: '<div class="popover mollify-itemcontext-popover"><div class="arrow"></div><div class="popover-inner"><h3 class="popover-title"></h3><div class="popover-content"><p></p></div></div></div>',
					content: html,
					container: $cont
				}).bind("shown", function(e) {
					var api = { id: popupId, hide: function() { $e.popover('destroy'); } };
					api.close = api.hide;					
					mollify.ui.activePopup(api);

					var $el = $("#mollify-itemcontext-"+item.id);
					var $pop = $el.closest(".popover");
					var maxRight = $c.outerWidth();
					var popLeft = $pop.offset().left - $cont.offset().left;
					var popW = $pop.outerWidth();
					if (popLeft < 0)						
						popLeft = 0;
					else if ((popLeft + popW) > maxRight)
						popLeft = maxRight - popW - 10;
					$pop.css("left", popLeft + "px");
					
					var arrowPos = ($e.offset().left - $cont.offset().left) + ($e.outerWidth() / 2);
					var arrowPos = Math.max(0, (arrowPos - popLeft));
					$pop.find(".arrow").css("left", arrowPos + "px");
					
					$pop.find(".popover-title").append($('<button type="button" class="close">×</button>').click(api.close));
					var $content = $el.find(".mollify-itemcontext-content");
					
					mollify.filesystem.itemDetails(item, mollify.plugins.getItemContextRequestData(item), function(d) {
						if (!d) {
							$t.hide();
							return;
						}
						
						ict.renderItemContext(api, $content, item, d);
						$e[0].scrollIntoView();
					});
				}).bind("hidden", function() {
					$e.unbind("shown").unbind("hidden");
					mollify.ui.removeActivePopup(popupId);
				});
				$e.popover('show');
			};
			
			this.renderItemContext = function(cApi, $e, item, details) {
				//var details = d[0];
				//TODO permissions to edit descriptions
				var descriptionEditable = mollify.session.features.descriptions && mollify.session.admin;
				var showDescription = descriptionEditable || !!details.description;
				
				var plugins = mollify.plugins.getItemContextPlugins(item, details);
				var actions = mollify.helpers.getPluginActions(plugins);
				var primaryActions = mollify.helpers.getPrimaryActions(actions);
				var secondaryActions = mollify.helpers.getSecondaryActions(actions);
				
				var o = {
					item:item,
					details:details,
					showDescription: showDescription,
					description: details.description || '',
					session: mollify.session,
					plugins: plugins,
					primaryActions : primaryActions
				};
				
				$e.removeClass("loading").empty().append(mollify.dom.template("mollify-tmpl-main-itemcontext-content", o, {
					title: function(o) {
						return o.title ? o.title : mollify.ui.texts.get(o['title-key']);
					}
				}));
				$e.click(function(e){
					// prevent from closing the popup when clicking the popup itself
					e.preventDefault();
					return false;
				});
				mollify.ui.process($e, ["localize"]);
				
				if (descriptionEditable && o.onDescription) {
					mollify.ui.controls.editableLabel({element: $("#mollify-itemcontext-description"), hint: mollify.ui.texts.get('itemcontextDescriptionHint'), onedit: function(desc) {
						o.onDescription(item, desc);
					}});
				}
				
				if (primaryActions) {
					$pae = $e.find(".mollify-itemcontext-primary-actions-button");
					$pae.click(function(e) {
						var i = $pae.index($(this));
						var action = primaryActions[i];
						cApi.close();
						action.callback();
					});
				}
				
				if (plugins) {
					var $selectors = $("#mollify-itemcontext-details-selectors");
					var $content = $("#mollify-itemcontext-details-content");
					var contents = {};
					var onSelectDetails = function(id) {
						$(".mollify-itemcontext-details-selector").removeClass("active");
						$("#mollify-itemcontext-details-selector-"+id).addClass("active");
						$content.find(".mollify-itemcontext-plugin-content").hide();
						
						var $c = contents[id] ? contents[id] : false;
						if (!$c) {
							$c = $('<div class="mollify-itemcontext-plugin-content"></div>');
							plugins[id].details["on-render"](cApi, $c);
							contents[id] = $c;
							$content.append($c);
						}
												
						$c.show();
					};
					var firstPlugin = false;
					for (var id in plugins) {
						var plugin = plugins[id];
						if (!plugin.details) continue;
						
						if (!firstPlugin) firstPlugin = id;

						var title = plugin.details.title ? plugin.details.title : (plugin.details["title-key"] ? mollify.ui.texts.get(plugin.details["title-key"]) : id);
						var selector = mollify.dom.template("mollify-tmpl-main-itemcontext-details-selector", {id: id, title:title, data: plugin}).appendTo($selectors).click(function() {
							var s = $(this).tmplItem().data;
							onSelectDetails(s.id);
						});
					}

					if (firstPlugin) onSelectDetails(firstPlugin);
				}
				
				var actions = mollify.ui.controls.dropdown({
					element: $e.find("#mollify-itemcontext-secondary-actions"),
					items: secondaryActions,
					hideDelay: 0,
					style: 'submenu',
					parentPopupId: cApi.id,
					onItem: function() {
						cApi.hide();
					},
					onBlur: function(dd) {
						dd.hide();
					}
				});
			}
			
			return {
				open : ict.open
			};
		},
		
		assign: function(h, id, c) {
			if (!h || !id || !c) return;
			if (!h.controls) h.controls = {};
			h.controls[id] = c;
		},
		
		process: function($e, ids, handler) {
			$.each(ids, function(i, k) {
				if (t.ui.handlers[k]) t.ui.handlers[k]($e, handler);
			});
		},
				
		handlers : {
			/*hintbox : function(p, h) {
				p.find("input.hintbox").each(function() {
					var $this = $(this);
					var hint = t.env.texts().get($this.attr('hint-key'));
					$this.attr("placeholder", hint).removeAttr("hint-key");
				});//.placeholder();
			},*/

			localize : function(p, h) {
				p.find(".localized").each(function() {
					var $t = $(this);
					var key = $t.attr('title-key');
					if (key) {
						$t.attr("title", mollify.ui.texts.get(key));
						$t.removeAttr('title-key');
					}
					
					key = $t.attr('text-key');
					if (key) {
						$t.prepend(mollify.ui.texts.get(key));
						$t.removeAttr('text-key');
					}
				});
				p.find("input.hintbox").each(function() {
					var $this = $(this);
					var hint = mollify.ui.texts.get($this.attr('hint-key'));
					$this.attr("placeholder", hint).removeAttr("hint-key");
				});//.placeholder();
			},
			
			center : function(p, h) {
				p.find(".center").each(function() {
					var $this = $(this);
					var x = ($this.parent().width() - $this.outerWidth(true)) / 2;
					$this.css({
						position: "relative",
						left: x
					});
				});
			},
			
			hover: function(p) {
				p.find(".hoverable").hover(function() {
					$(this).addClass("hover");
				}, function() {
					$(this).removeClass("hover");
				});
			},
			
			bubble: function(p, h) {
				p.find(".bubble-trigger").each(function() {
					var $t = $(this);
					var b = mollify.ui.controls.bubble({element:$t, handler: h});
					mollify.ui.assign(h, $t.attr('id'), b);
				});
			},
			
			radio: function(p, h) {
				p.find(".mollify-radio").each(function() {
					var $t = $(this);
					var r = mollify.ui.controls.radio($t, h);
					mollify.ui.assign(h, $t.attr('id'), r);
				});
			}
		},
		
		window : {
			open : function(url) {
				window.open(url);
			}
		},
		
		controls: {
			dropdown : function(a) {
				var $e = $(a.element);
				var $mnu = false;
				var popupId = false;
				var popupItems = a.items;
				$e.addClass('dropdown');
				var hidePopup = function() {
					if (!$mnu) return;
					if (a.onHide) a.onHide();
					$mnu.parent().removeClass("open");
					t.ui.removeActivePopup(popupId);
				};
				var createItems = function(itemList) {
					var i = mollify.dom.template("mollify-tmpl-popupmenu", {items:itemList||{}}, {
						isSeparator : function(i) {
							return (i.type == 'separator' || i.title == '-');
						},
						getTitle : function(i) {
							if (i.title) return i.title;
							if (i['title-key']) return mollify.ui.texts.get(i['title-key']);
							return "";
						}
					});
					return i;
				};
				var initItems = function(l) {
					var $items = $e.find(".dropdown-item");
					$items.click(function() {
						var item = l[$(this).index()];
						if (a.onItem) a.onItem(item);
						if (item.callback) item.callback();
					});
				};
				var api = {
					hide: hidePopup,
					items: function(items) {
						$mnu.remove();
						$mnu = createItems(items);
						$e.removeClass("loading").append($mnu);
						initItems(items);
						popupItems = items;
					}
				};
				if (a.parentPopupId) api.parentPopupId = a.parentPopupId;
				$e.append(createItems(a.items)).find(".dropdown-toggle").dropdown({
					onshow: function($p) {
						if (!$mnu) $mnu = $($p.find(".dropdown-menu")[0]);
						if (!a.parentPopupId)
							popupId = t.ui.activePopup(api);
						if (!popupItems) $mnu.addClass("loading");
						if (a.onShow) a.onShow(api, popupItems);
					},
					onhide: function() {
						hidePopup();
						if (a.dynamic) {
							popupItems = false;
							$mnu.remove();
						}
					}
				});
				initItems(a.items);
				/*$e.hover(function() {
					$(this).addClass("hover");
				}, function() {
					$(this).removeClass("hover");
				});*/
				/*$('<div class="mollify-dropdown-handle"></div>').click(function(){
					mollify.ui.controls.popupmenu(a);
				}).appendTo($e);*/
			},
			
			popupmenu : function(a) {
				var popupId = false;
				var $e = $(a.element);
				var pos = $e.offset();
				var $mnu = $('<div class="mollify-popupmenu" style="position: absolute; top: '+(pos.top + $e.outerHeight())+'px; left:'+pos.left+'px;"></div>');
				var popupitems = a.items;
				var hidePopup = function() {
					if (a.onHide) a.onHide();
					$mnu.remove();
					t.ui.removeActivePopup(popupId);
				};
				var createItems = function(itemList) {
					var items = mollify.dom.template("mollify-tmpl-popupmenu", {items:itemList||{}}, {
						isSeparator : function(i) {
							return i.title == '-';
						},
						getTitle : function(i) {
							if (i.title) return i.title;
							if (i['title-key']) return mollify.ui.texts.get(i['title-key']);
							return "";
						}
					}).css("display", "block");
					return items;
				};
				var initItems = function(l) {
					var $items = $mnu.find(".dropdown-item");
					$items.click(function() {
						hidePopup();
						var item = l[$(this).index()];
						if (a.onItem) a.onItem(item);
						if (item.callback) item.callback();
					});
				};
				
				if (!a.items) $mnu.addClass("loading");
				$mnu.append(createItems(a.items));
				if (a.style) $mnu.addClass(a.style);
				$("#mollify").append($mnu);//.on('click', hidePopup);
				
				var api = {
					hide: hidePopup,
					items: function(items) {
						$mnu.empty().removeClass("loading").append(createItems(items));
						initItems(items);
						items
					}
				};
				popupId = t.ui.activePopup(api);
				return api;
			},
			
			bubble: function(o) {
				var $e = o.element;
				var actionId = $e.attr('id');
				if (!actionId) return;
				
				var content = $("#" + actionId + '-bubble');
				if (!content || content.length == 0) return;

				var html = content.html();
				content.remove();

				var $tip = false;
				var rendered = false;
				var api = {
					hide: function() {
						e.popover('hide');
					},
					close: this.hide
				};
				var $el = $('<div class="popover mollify-bubble-popover"><div class="arrow"></div><div class="popover-inner"><div class="popover-content"><p></p></div></div></div>');
				$e.popover({
					title: false,
					html: true,
					placement: 'bottom',
					trigger: 'click',
					template: $el,
					content: html,
					/*onshow: function($t) {
						$tip = $t;
						t.ui.activePopup(api);
						if (!rendered) {
							if (o.handler && o.handler.onRenderBubble) o.handler.onRenderBubble(actionId, api);
							rendered = true;
						}
						if (o.handler && o.handler.onShowBubble) o.handler.onShowBubble(actionId, api);
					},
					onhide: function($t) {
						t.ui._activePopup = false;
						//e.popover('destroy');
					}*/
				}).bind("shown", function(e) {
					$tip = $el;
					t.ui.activePopup(api);
					if (!rendered) {
						if (o.handler && o.handler.onRenderBubble) o.handler.onRenderBubble(actionId, api);
						rendered = true;
					}
					if (o.handler && o.handler.onShowBubble) o.handler.onShowBubble(actionId, api);
				}).bind("hidden", function() {
					//$e.unbind("shown").unbind("hidden");
					mollify.ui.removeActivePopup(api.id);
				});
			},

			dynamicBubble: function(o) {
				var $e = o.element;
				
				var bubbleHtml = function(c) {
					if (!c) return "";
					if (typeof(c) === 'string') return c;
					return $("<div/>").append(c).html();
				};
				var html = o.content ? bubbleHtml(o.content) : '<div class="loading"></div>';
				var $tip = false;
				var api = {
					show: function() {
						$e.popover('show');
					},
					hide: function(dontDestroy) {
						if (dontDestroy) $tip.hide();
						else $e.popover('destroy');
					},
					element : function() {
						return $tip;
					},
					getContent: function() {
						return $tip.find('.popover-content');	
					},
					content: function(c) {
						var $c = $tip.find('.popover-content');
						$c.html(bubbleHtml(c));
					}
				};
				api.close = api.hide;
				var $el = $('<div class="popover mollify-bubble-popover"><div class="arrow"></div>' + (o.title ? '<h3 class="popover-title"></h3>' : '') + '<div class="popover-content"></div></div>');

				$e.popover({
					title: o.title ? o.title : false,
					html: true,
					placement: 'bottom',
					trigger: 'manual',
					template: $el,
					content: html,
					container: $e.parent()
				}).bind("shown", function(e) {
					$tip = $el;
					t.ui.activePopup(api);
					if (o.title)
						$tip.find(".popover-title").append($('<button type="button" class="close">×</button>').click(api.close));
					mollify.ui.handlers.localize($tip);
					if (o.handler && o.handler.onRenderBubble) o.handler.onRenderBubble(api);
				}).bind("hidden", function() {
					$e.unbind("shown").unbind("hidden");
					mollify.ui.removeActivePopup(api.id);
				});
				$e.popover('show');
				
				return api;
			},
			
			table: function(id, o) {
				var $e = $("#"+id);
				if ($e.length == 0 || !o.columns) return false;
				
				$e.addClass("table");

				var $h = $("<tr></tr>").appendTo($("<thead></thead>").appendTo($e));
				for (var i=0,j=o.columns.length; i<j; i++) {
					var col = o.columns[i];
					$h.append("<th>"+(col.title ? col.title : "")+"</th>");
				}

				var $l = $("<tbody></tbody>").appendTo($e);
				
				var setCellValue = function($cell, col, item) {
					var v = item[col.id];		
					if (col.renderer) col.renderer(item, v, $cell);
					else $cell.html(v);
				};
				var addItem = function(item) {
					var $row = $("<tr></tr>").appendTo($l);
					$row[0].data = item;
					if (o.onRow) o.onRow($row, item);
					
					for (var i=0,j=o.columns.length; i<j; i++) {
						var $cell = $("<td></td>").appendTo($row);
						setCellValue($cell, o.columns[i], item);
					}
				};
				
				var findRow = function(item) {
					var found = false;
					$l.find("tr").each(function() {
						var $row = $(this);
						var rowItem = $row[0].data;
						if (item == rowItem) {
							found = $row;
							return false;
						}
					});
					return found;
				};
				var updateRow = function($row) {
					$row.find("td").each(function() {
						var $cell = $(this);
						var index = $cell.index();
						setCellValue($cell, o.columns[index], $row[0].data);
					});
				};
				
				var api = {
					findByKey : function(k) {
						if (!o.key) return false;
						var found = false;
						$l.find("tr").each(function() {
							var item = $(this)[0].data;
							if (item[o.key] == k) {
								found = item;
								return false;
							}
						});
						return found;
					},
					add : function(item) {
						if (!item) return;
						
						if (isArray(item)) {
							for (var i=0,j=item.length; i<j; i++) addItem(item[i]);
						} else {
							addItem(item);
						}	
					},
					update : function(item) {
						if (!item) return;
						var $row = findRow(item);
						if (!$row) return;
						updateRow($row);
					},
					remove : function(item) {
						if (!item) return;
						var $row = findRow(item);
						if (!$row) return;
						$row.remove();
					}
				};
				return api;
			},
			
			select: function(e, o) {				
				var $e = (typeof(e) === "string") ? $("#"+e) : e;
				if (!$e || $e.length == 0) return false;

				var addItem = function(item) {
					var $row = $("<option></option>").appendTo($e);
					if (item == o.none) {
						$row.html(item.title);
					} else {
						if (o.renderer) o.renderer(item, $row);
						else $row.html(o.title ? item[o.title] : item);
					}
					$row[0].data = item;
				};
				
				var getSelected = function() {
					var s = $e.find('option:selected');
					if (!s || s.length == 0) return false;
					var item = s[0].data;
					if (item == o.none) return false;
					return item;
				}
				
				if (o.onChange) {
					$e.change(function() {
						o.onChange(getSelected());
					});
				}
				
				var api = {
					add : function(item) {
						if (!item) return;
						
						if (isArray(item)) {
							for (var i=0,j=item.length; i<j; i++) addItem(item[i]);
						} else {
							addItem(item);
						}	
					},
					select : function(item) {
						var $c = $e.find("option");
						
						if (typeof(item) === 'number') {
							if ($c.length >= item) return;
							$($c[item]).attr("selected", "true");
							return;	
						}
						
						var find = item;
						if (o.none && !find) find = o.none;
						
						for (var i=0,j=$c.length; i<j; i++) {
							if ($c[i].data == find) {
								$($c[i]).attr("selected", "true");
								return;
							}
						}
					},
					selected : getSelected
				};
				if (o.none) api.add(o.none);
				if (o.values) api.add(o.values);
				return api;
			},
			
			radio: function(e, h) {
				var rid = e.addClass("btn-group").attr('id');
				var items = e.find("button");
				
				var select = function(item) {
					items.removeClass("active");
					item.addClass("active");
				}
				
				items.click(function() {
					var i = $(this);
					var ind = items.index(i);
					select(i);
					
					var id = i.attr('id');
					if (h && rid && h.onRadioChanged) h.onRadioChanged(rid, id, ind);
				});
				
				return {
					set: function(ind) {
						select($(items[ind]));
					}
				};
			},
			
			editableLabel: function(o) {
				var $e = $(o.element);
				var id = $e.attr('id');
				var originalValue = o.value || $e.html().trim();
				if (!id) return;
				
				$e.addClass("editable-label").hover(function() {
					$e.addClass("hover");
				}, function() {
					$e.removeClass("hover");
				});
				
				var $label = $("<label></label>").appendTo($e.empty());
				var $editor = $("<input></input>").appendTo($e);
				var ctrl = {
					value: function(v) {
						originalValue = v;
						if (originalValue || !o.hint) {
							$e.removeClass("hint");
							$label.html(originalValue);
						} else {
							$e.addClass("hint");
							$label.html(o.hint);
						}
						$editor.val(originalValue);	
					}
				};
				ctrl.value(originalValue);
				
				var onFinish = function() {
					var v = $editor.val();
					if (o.isvalid && !o.isvalid(v)) return;
					
					$editor.hide();
					$label.show();
		            if (originalValue != v) {
		            	if (o.onedit) o.onedit(v);
		            	ctrl.value(v);
		            }
				};
				var onCancel = function() {
					$editor.hide();
					$label.show();
					ctrl.value(originalValue);
				};
  
				$editor.hide().bind("blur", onFinish).keyup(function(e) {
					if (e.which == 13) onFinish();
					else if (e.which == 27) onCancel();
				});
				
				$label.bind("click", function() {
					$label.hide();
					$editor.show().focus();
				});
				
				return ctrl;
			}
		}
	}
	
	
	/*this.formatDate = function(d) {
		return $.datepicker.formatDate(getDateFormat(), d);
	}*/

	this.formatDateTime = function(time, fmt) {
		return time.format(fmt);
	}

	/*this.parseDate = function(dateFmt, date, time) {
		if (!date || date.length == 0) return null;
		
		var t = $.datepicker.parseDate(dateFmt, date);
		if (!time || time.length < 5) {
			t.setHours("00");
			t.setMinutes("00");
			t.setSeconds("00");
		} else {
			//TODO timeFmt
			t.setHours(time.substring(0,2));
			t.setMinutes(time.substring(3,5));
			t.setSeconds(time.length > 6 ? time.substring(7,9) : "00");
		}
		return t;
	}*/

	this.parseInternalTime = function(time) {
		var ts = new Date();
		ts.setYear(time.substring(0,4));
		ts.setMonth(time.substring(4,6) - 1);
		ts.setDate(time.substring(6,8));
		ts.setHours(time.substring(8,10));
		ts.setMinutes(time.substring(10,12));
		ts.setSeconds(time.substring(12,14));
		return ts;
	}

	this.formatInternalTime = function(time) {
		if (!time) return null;
		return time.format('yymmddHHMMss', time);
	}
	
	this.texts = new function() {
		var tt = this;
		this.locale = '';
		this.values = null;
		
		this.set = function(id, values) {
			tt.locale = id;
			tt.values = values;
		}
		
		this.add = function(id, values) {
			//TODO handle different locale
			for (v in values) {
				tt.values[v] = values[v];
			}
		}
	}
}

/**
/* Dialogs
/**/
$.extend(true, mollify, {
	view : {
		DialogHandler : function(fs) {
			var that = this;
			
			var dialogDefaults = {
				title: "Mollify"
			};
			
			this.info = function(spec) {
				that.custom({
					title: spec.title,
					content: $("#mollify-tmpl-dialog-info").tmpl({message: spec.message}),
					buttons: [
						{ id: "ok", "title-key": "ok" }
					],
					"on-button": function(btn, d) {
						d.close();
						if (spec.callback) spec.callback();
					}
				});
				/*var dlg = $("#mollify-tmpl-dialog-info").tmpl($.extend(spec, dialogDefaults)).dialog({
					modal: true,
					resizable: false,
					height: 'auto',
					minHeight: 50
				});
				mollify.ui.handlers.localize(dlg);
				dlg.find("#mollify-info-dialog-close-button").click(function() { dlg.dialog('destroy'); dlg.remove(); });*/
			};
			
			this.error = function(spec) {
				that.custom({
					title: spec.title,
					content: $("#mollify-tmpl-dialog-error").tmpl({message: spec.message}),
					buttons: [
						{ id: "ok", "title-key": "ok" }
					],
					"on-button": function(btn, d) {
						d.close();
						if (spec.callback) spec.callback();
					}
				});
			};
			
			this.confirmation = function(spec) {
				that.custom({
					title: spec.title,
					content: spec.message,
					buttons: [
						{ id: "yes", "title-key": "yes" },
						{ id: "no", "title-key": "no" }
					],
					"on-button": function(btn, d) {
						d.close();
						if (spec.callback && btn.id === 'yes') spec.callback();
					}
				});
			};
			
			this.input = function(spec) {
				var $input = false;
				that.custom({
					title: spec.title,
					content: $("#mollify-tmpl-dialog-input").tmpl({message: spec.message}),
					buttons: [
						{ id: "yes", "title": spec.yesTitle },
						{ id: "no", "title": spec.noTitle }
					],
					"on-button": function(btn, d) {
						if (btn.id === 'yes') {
							if (!spec.handler || !spec.handler.isAcceptable) return;
							if (!spec.handler.isAcceptable($input.val())) return;
						}
						d.close();
						if (btn.id === 'yes') spec.handler.onInput($input.val());
					},
					"on-show": function(h, $dlg) {
						$input = $dlg.find(".mollify-inputdialog-input");
						if (spec.default) $input.val(spec.default);
						$input.focus();
					}
				});
			};
			
			this.wait = function(spec) {
				var $trg = (spec && spec.target) ? $("#"+spec.target) : $("body");
				var w = mollify.dom.template("mollify-tmpl-wait", $.extend(spec, dialogDefaults)).appendTo($trg).show();
				return {
					close: function() {
						w.remove();
					}
				};
			};
			
			this.notification = function(spec) {
				var $trg = (spec && spec.target) ? $("#"+spec.target) : $("#mollify-notification-container");
				if ($trg.length == 0) $trg = $("body");
				var notification = mollify.dom.template("mollify-tmpl-notification", $.extend(spec, dialogDefaults)).hide().appendTo($trg).fadeIn(300);
				setTimeout(function() {
					notification.fadeOut(300);
					if (spec["on-finish"]) spec["on-finish"]();
				}, spec.time | 3000);
			};
			
			this.custom = function(spec) {
				var center = function($d) {
				    $d.css("margin-left", -$d.outerWidth()/2);
				    $d.css("margin-top", -$d.outerHeight()/2);
				    $d.css("top", "50%");
				    $d.css("left", "50%");
				};
				var s = spec;
				if (s['title-key']) s.title = mollify.ui.texts.get(s['title-key']);
				
				var $dlg = $("#mollify-tmpl-dialog-custom").tmpl($.extend(dialogDefaults, s), {
					getContent: function() {
						if (spec.html) return spec.html;
						if (spec.content) {
							var c = spec.content;
							if (typeof c === 'string') return c;
							return $("<div/>").append(c.clone()).html();
						}
						return "";
					},
					getButtonTitle: function(b) {
						if (b["title"]) return b["title"];
						if (b["title-key"]) return mollify.ui.texts.get(b["title-key"]);
						return "";
					}
				});
				if (spec.element) $dlg.find(".modal-body").append(spec.element);
				
				mollify.ui.handlers.localize($dlg);
				$dlg.on('hidden', function() { $dlg.remove(); }).modal({
					backdrop: !!spec.backdrop,
					show: true,
					keyboard: true
				});
				var h = {
					close: function() {
						$dlg.modal('hide');
					},
					center: function() {
						center($dlg);
					}
				};
				$dlg.find(".modal-footer .btn").click(function(e) {
					e.preventDefault();
					var data = $(this).tmplItem().data;
					var btn = data.buttons[$(this).index()];
					if (spec["on-button"]) spec["on-button"](btn, h);
				});
				if (spec.resizable) {
					var $header = $dlg.find(".modal-header");
					var $body = $dlg.find(".modal-body");
					var $footer = $dlg.find(".modal-footer");
					var magicNr = 30;//$body.css("padding-top") + $body.css("padding-bottom");	//TODO??
					
					$body.css({
						"max-height": "none",
						"max-width": "none"
					});
					
					var onResize = function() {
						center($dlg);
				    	var h = $dlg.innerHeight() - $header.outerHeight() - $footer.outerHeight() - magicNr;
				      	$body.css("height", h);
					}
					
					$dlg.css({
						"max-height": "none",
						"max-width": "none",
						"min-height": $dlg.outerHeight()+"px",
						"min-width": $dlg.outerWidth()+"px"
					}).on("resize", onResize).resizable();
					if (spec.initSize) {
						$dlg.css({
							"width": spec.initSize[0]+"px",
							"height": spec.initSize[1]+"px"
						});
					}
					onResize();
				}
				if (spec["on-show"]) spec["on-show"](h, $dlg);
				return h;
			};
			
			this.folderSelector = function(spec) {
				var selectedFolder = false;
				var content = $("#mollify-tmpl-dialog-folderselector").tmpl({message: spec.message});
				var $selector = false;
				var loaded = {};
				
				var load = function($e, parent) {
					if (loaded[parent ? parent.id : "root"]) return;
					
					$selector.addClass("loading");
					fs.folders(parent, function(l) {
						$selector.removeClass("loading");
						loaded[parent ? parent.id : "root"] = true;
						
						if (!l || l.length == 0) {
							if ($e) $e.find(".mollify-folderselector-folder-indicator").empty();
							return;
						}
						
						var level = 0;
						var levels = [];
						if (parent) {
							var matches = parent.path.match(/\//g);
							if (matches) level = matches.length + 1;
							else level = 1;
							
							//generate array for template to iterate
							for(var i=0;i<level;i++) levels.push({});
						}
						var c = $("#mollify-tmpl-dialog-folderselector-folder").tmpl(l, {cls:(level == 0 ? 'root' : ''),levels:levels});
						if ($e) {
							$e.after(c);
							$e.addClass("loaded");
							if ($e) $e.find(".mollify-folderselector-folder-indicator").find("i").removeClass("icon-caret-right").addClass("icon-caret-down");
						} else {
							$selector.append(c);
						}
						if (!parent && l.length == 1) {
							load($(c[0]), l[0]);
						}
					});
				};
				
				that.custom({
					title: spec.title,
					content: content,
					buttons: [
						{ id: "action", "title": spec.actionTitle },
						{ id: "cancel", "title-key": "dialogCancel" }
					],
					"on-button": function(btn, d) {
						if (btn.id === 'action') {
							if (!selectedFolder || !spec.handler || !spec.handler.canSelect(selectedFolder)) return;	
						}
						d.close();
						if (btn.id === 'action') spec.handler.onSelect(selectedFolder);
						
					},
					"on-show": function(h, $dlg) {
						$selector = $dlg.find(".mollify-folderselector-tree");
						$selector.on("click", ".mollify-folderselector-folder-indicator", function(e) {
							var $e = $(this).parent();
							var p = $e.tmplItem().data;
							load($e, p);
							return false;
						});
						$selector.on("click", ".mollify-folderselector-folder", function(e) {
							var $e = $(this);
							var p = $(this).tmplItem().data;
							if (spec.handler.canSelect(p)) {
								selectedFolder = p;
								$(".mollify-folderselector-folder").removeClass("selected");
								$e.addClass("selected");
							}
						});
						load(null, null);
					}
				});
			};
		}
	}
});

/**
/* Login view
/**/
$.extend(true, mollify, {
	view : {
		LoginView : function() {
			var that = this;
			
			this.init = function($c) {
				mollify.dom.loadContentInto($c, mollify.templates.url("loginview.html"), that, ['localize', 'bubble']);
			}
			
			this.onLoad = function() {
				$(window).resize(that.onResize);
				that.onResize();
			
				if (mollify.hasFeature('lost_password')) $("#login-lost-password").show();
				if (mollify.hasFeature('registration')) {
					$("#mollify-login-register").click(function() {
						mollify.ui.window.open(mollify.service.pluginUrl("registration"));
					});
					$("#mollify-login-register").show();
				}
				
				var $data = $("#mollify-login-data");
				mollify.ui.handlers.center($data);
				//mollify.ui.handlers.bubble($data, that);
				$("#mollify-login-name, #mollify-login-password").bind('keypress', function(e) {
					if ((e.keyCode || e.which) == 13) that.onLogin();
				});
				$("#mollify-login-button").click(that.onLogin);
				$("#mollify-login-name").focus();
			}
			
			this.onResize = function() {
				var h = $(window).height();
				$("#mollify-login-main").height(h);
				
				$data = $("#mollify-login-data");
				$data.css('margin-top', (h / 2) - ($data.height() / 2));
			}
			
			this.onRenderBubble = function(id, bubble) {
				if (id === 'mollify-login-forgot-password') {
					$("#mollify-login-forgot-button").click(function() {				
						var email = $("#mollify-login-forgot-email").val();
						if (!email) return;
						
						bubble.hide();
						that.wait = mollify.ui.dialogs.wait({target: "mollify-login-main"});
						//TODO that.listener.onResetPassword(email);
					});
				}
			}
			
			this.onShowBubble = function(id, bubble) {
				if (id === 'mollify-login-forgot-password') {
					$("#mollify-login-forgot-email").val("").focus();
				}
			}
			
			this.onLogin = function() {
				var username = $("#mollify-login-name").val();
				var password = $("#mollify-login-password").val();
				var remember = $("#mollify-login-remember-cb").is(':checked');
				
				if (!username || username.length < 1) {
					$("#mollify-login-name").focus();
					return;
				}
				if (!password || password.length < 1) {
					$("#mollify-login-password").focus();
					return;
				}
				that.wait = mollify.ui.dialogs.wait({target: "mollify-login-main"});
				mollify.service.post("session/authenticate", {protocol_version: 3, username: username, password: Base64.encode(password), remember: remember}, function(s) {
					mollify.core.session.set(s);
				}, function(c, e) {
					that.showLoginError();
				});
			}
			
			this.showLoginError = function() {
				that.wait.close();
				
				mollify.ui.dialogs.notification({
					message: mollify.ui.texts.get('loginDialogLoginFailedMessage')
				});
			}
			
			this.onResetPasswordSuccess = function() {
				that.wait.close();
				
				mollify.ui.dialogs.notification({
					message: mollify.ui.texts.get('resetPasswordPopupResetSuccess')
				});
			}
			
			this.onResetPasswordFailed = function() {
				that.wait.close();
				
				mollify.ui.dialogs.info({
					message: mollify.ui.texts.get('resetPasswordPopupResetFailed')
				});
			}
		}
	}
});

/**
/* Item details plugin
/**/
$.extend(true, mollify, {
	plugin : {
		ItemDetailsPlugin: function(conf, sp) {
			var that = this;
			that.formatters = {};
			that.typeConfs = false;
			
			this.initialize = function(core) {
				that.core = core;
				
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
				if (ext.length == 0 || !that.typeConfs[ext]) {
					ext = item.is_file ? "[file]" : "[folder]";
					if (!that.typeConfs[ext])
						return that.typeConfs["*"];
				}
				return that.typeConfs[ext];
			}
			
			this.renderItemContextDetails = function(el, item, $content, data) {
				$content.addClass("loading");
				mollify.templates.load("itemdetails-content", mollify.noncachedUrl(mollify.plugins.url("ItemDetails", "content.html")), function() {
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
					if (f.groupTitle) return spec.groupTitle;
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
				if (key == 'size') return mollify.ui.texts.formatSize(data);
				if (key == 'last-modified') return mollify.ui.texts.formatInternalTime(data);
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
				itemContextHandler : function(item, details, data) {
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
	}
});

/**
/* File viewer editor plugin
/**/
$.extend(true, mollify, {
	plugin : {
		FileViewerEditorPlugin: function() {
			var that = this;
			
			this.initialize = function(core) {
				that.core = core;
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
				
				$c = $v.find(".carousel").carousel({interval: false}).on('slid', function() {
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
				itemContextHandler : function(item, details, data) {
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
		}
	}
});


/**
/* Comment plugin
/**/
$.extend(true, mollify, {
	plugin : {
		CommentPlugin: function() {
			var that = this;
			
			this.initialize = function(core) {
				that.core = core;
				
				mollify.dom.importCss(mollify.plugins.url("Comment", "style.css"));
				mollify.dom.importScript(mollify.plugins.url("Comment", "texts_" + mollify.ui.texts.locale + ".js"));
				
				mollify.ui.filelist.addColumn({
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
				});
			};
			
			this.getListCellContent = function(item, data) {
				if (!item.id || item.id.length == 0 || !data || !data["plugin-comment-count"]) return "";
				var counts = data["plugin-comment-count"];
		
				if (!counts[item.id])
					return "<div id='item-comment-count-"+item.id+"' class='filelist-item-comment-count-none'></div>";
				
				return "<div id='item-comment-count-"+item.id+"' class='filelist-item-comment-count'>"+counts[item.id]+"</div>";
			};
			
			this.renderItemContextDetails = function(el, item, $content, data) {
				$content.addClass("loading");
				mollify.templates.load("comments-content", mollify.noncachedUrl(mollify.plugins.url("Comment", "content.html")), function() {
					$content.removeClass("loading");
					if (data.count == 0) {
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
					if (!comment || comment.length == 0) return;
					that.onAddComment(item, comment, el.close);
				} );
				
				that.updateComments($("#comments-list"), item, comments);
			};
			
			this.showCommentsBubble = function(item, e) {
				var bubble = mollify.ui.controls.dynamicBubble({element:e, title: item.name});
				
				mollify.templates.load("comments-content", mollify.noncachedUrl(mollify.plugins.url("Comment", "content.html")), function() {
					bubble.content(mollify.dom.template("comments-template", item));
			
					$("#comments-dialog-add").click(function() { 
						var comment = $("#comments-dialog-add-text").val();
						if (!comment || comment.length == 0) return;
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
				var userId = mollify.session['user_id'];
				var isAdmin = mollify.session.admin;
				
				for (var i=0,j=comments.length; i<j; i++) {
					comments[i].time = mollify.ui.texts.formatInternalTime(comments[i].time);
					comments[i].comment = comments[i].comment.replace(new RegExp('\n', 'g'), '<br/>');
					comments[i].remove = isAdmin || (userId == comments[i]['user_id']);
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
				
				if (comments.length == 0) {
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
				itemContextHandler : function(item, data) {
					return {
						details: {
							"title-key": "pluginCommentContextTitle",
							"on-render": function(el, $content) { that.renderItemContextDetails(el, item, $content, data); }
						}
					};
				}
			};
		}
	}
});

/**
/* Permissions plugin
/**/
$.extend(true, mollify, {
	plugin : {
		PermissionsPlugin: function() {
			var that = this;
			
			this.initialize = function(core) {
				that.core = core;
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
				mollify.templates.load("comments-content", mollify.noncachedUrl(mollify.plugins.url("Comment", "content.html")), function() {
					$content.removeClass("loading");
					if (data.count == 0) {
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
					"removed": [],	
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
						if (permissionData["new"].length == 0 && permissionData["modified"].length == 0 && permissionData["removed"].length == 0)
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
					usersById : {},
				};
				for (var i=0,j=l.length; i<j; i++) {
					var u = l[i];
					if (u["is_group"] == "0") {
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
				
				var isGroup = function(id) { return (userData.usersById[id]["is_group"] != "0"); };
				var onAddOrUpdate = function(user, permissionVal) {
					var userVal = $list.findByKey(user.id);
					if (userVal) {
						if (!userVal.isnew) permissionData["modified"].push(userVal);
						userVal.permission = permissionVal;
						$list.update(userVal);
					} else {
						// if previously deleted, move it to modified
						for (var i=0,j=permissionData["removed"].length; i<j; i++) {
							var d = permissionData["removed"][i];
							if (d["user_id"] == user.id) {
								permissionData["removed"].remove(i);
								permissionData["modified"].push(d);
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
					if (!permission.isnew) permissionData["removed"].push(permission);
				};
				var onEdit = function(permission) {
					if (!permission.isnew) permissionData["modified"].push(permission);
				};
				
				$list = mollify.ui.controls.table("mollify-pluginpermissions-editor-permission-list", {
					key: "user_id",
					onRow: function($r, i) { if (isGroup(i["user_id"])) $r.addClass("group"); },
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
				$("#mollify-pluginpermissions-editor-permission-list a.remove-link").live("click", function() {
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
					
					$list = mollify.ui.controls.table("mollify-pluginpermissions-context-permission-list", {
						key: "user_id",
						columns: [
							{ id: "user_id", title: mollify.ui.texts.get('pluginPermissionsEditColUser'), renderer: function(i, v, $c){ $c.html(userData.usersById[v].name).addClass("user"); } },
							{ id: "permission", title: mollify.ui.texts.get('pluginPermissionsEditColPermission'), renderer: function(i, v, $c){
								$c.html(that.permissionOptionsByKey[v].title);
							}},
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
				itemContextHandler : function(item, data) {
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
	}
});

/**
/* Dropbox plugin
/**/
$.extend(true, mollify, {
	plugin : {
		DropboxPlugin: function() {
			var that = this;
			that.w = 0;
			that.$dbE = false;
			that.items = [];
			
			this.initialize = function(core) {
				that.core = core;
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
						if (items) return;
						
						that.getActions(function(a) {
							if (!a) {
								drp.hide();
								return;
							}
							drp.items(a);
						});
					},
					onItem: function() {
						
					},
					onBlur: function(dd) {
						
					}
				});
				that.openDropbox(false);
			};
			
			this.getActions = function(cb) {				
				if (that.items.length == 0) {
					cb([]);
					return;
				}
				var plugins = mollify.plugins.getItemCollectionPlugins(that.items);
				cb(mollify.helpers.cleanupActions(mollify.helpers.addPluginActions([], plugins)));
			};
			
			this.openDropbox = function(o) {
				var open = that.$dbE.hasClass("opened");
				if (def(o)) {
					if (o == open) return;
				} else {
					o = !open;
				}
				
				if (!o) that.$dbE.removeClass("opened").addClass("closed").animate({"width": "0"}, 300);
				else that.$dbE.addClass("opened").removeClass("closed").animate({"width": that.w+""}, 300);
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
					that.itemContext.open(item, $i, $("#mollify"), $("#mollify"));
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
				onMainViewRender: that.onMainViewRender,
				itemContextHandler : function(item, data) {
					return {
						actions: [
							{ id: 'pluginDropbox', 'title-key': 'pluginDropboxAddTo', callback: function() { that.onAddItem(item); that.openDropbox(true); } }
						]
					};
				}
			};
		}
	}
});

/**
/* Core plugin
/**/
$.extend(true, mollify, {
	plugin : {
		Core: function() {
			var that = this;
			
			this.initialize = function(core) {
				that.core = core;
			};
									
			return {
				id: "plugin-core",
				initialize: that.initialize,
				itemContextHandler : function(item, data) {
					var root = item.id == item.root_id;
					var writable = !root && data.permission == "RW";
//								boolean root = !item.isFile()
//										&& ((JsFolder) item.cast()).isRoot();
//								boolean writable = !root
//										&& details.getFilePermission()
//												.canWrite();
					var actions = [];
					
					if (item.is_file ) {
						actions.push({ 'title-key': 'downloadItem', callback: function() { mollify.filesystem.del(item); } });
						actions.push({ title: '-' });
					}
					
					actions.push({ 'title-key': 'copyItem', callback: function() { mollify.filesystem.copy(item);}});
					
					if (writable) {
						actions.push({ 'title-key': 'copyHereItem', callback: function() { mollify.filesystem.copyHere(item); } });
						actions.push({ 'title-key': 'moveItem', callback: function() { mollify.filesystem.move(item); } });
						actions.push({ 'title-key': 'renameItem', callback: function() { mollify.filesystem.rename(item); } });
						actions.push({ 'title-key': 'deleteItem', callback: function() { mollify.filesystem.del(item); } });
					}
					return {
						actions: actions
					};
				},
				itemCollectionHandler : function(items) {
					return {
						actions: [
							{ 'title-key': 'copyMultiple', callback: function() { } },
							{ 'title-key': 'moveMultiple', callback: function() { } }
						]
					};
				}
			};
		}
	}
});


/**
/* Item collection plugin
/**/
$.extend(true, mollify, {
	plugin : {
		ItemCollectionPlugin: function() {
			var that = this;
			
			this.initialize = function(core) {
				that.core = core;
			};
									
			return {
				id: "plugin-itemcollection",
				initialize: that.initialize,
				itemCollectionHandler : function(items) {
					return {
						actions: [
							{ id: 'pluginItemCollection', 'title-key': 'copyMultiple', callback: function() { } }
						]
					};
				}
			};
		}
	}
});


/*function ItemDetailsPlugin(conf, sp) {
	var that = this;
	that.specs = {};
	that.typeConfs = false;
	
	this.getPluginInfo = function() { return { id: "plugin-itemdetails" }; }
	
	this.initialize = function(env) {
		that.env = env;
		
		if (sp) {
			for (var i=0; i<sp.length;i++)
				that.addDetailsSpec(sp[i]);
		}
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
		
		that.env.addItemContextProvider(function(item) {
			if (!that.typeConfs || !that.getApplicableSpec(item)) return null;
			
			return {
				components : [{
					title: that.t("fileActionDetailsTitle"),
					html: "<div id='file-item-details' class='loading'></div>",
					on_init: that.onInit,
					on_open: that.onOpen,
					on_dispose: function() { that.loaded = false; },
					index: 5
				}]
			}
		}, function(item) {
			if (!that.typeConfs) return null;
			var spec = that.getApplicableSpec(item);
			if (!spec) return null;
			
			var result = { itemdetails: [] };
			for (var k in spec)
				result.itemdetails.push(k);
			return result;
		});
	}
	
	this.onOpen = function(item, details) {
		if (that.loaded) return;
		that.loaded = true;
		
		mollify.loadContent("file-item-details", that.url("content.html"), function() {
			$("#file-item-details").removeClass("loading");
			var s = that.getApplicableSpec(item);
			var data = [];
			for (var k in s) {
				var rowSpec = s[k];
				var rowData = details.itemdetails[k];
				if (!rowData) continue;
				
				data.push({key:k, title:that.getTitle(k, rowSpec), value: that.formatData(k, rowData)});
			}
			$("#item-details-template").tmpl(data).appendTo("#mollify-file-item-details-content");
		});
	}
	
	this.addDetailsSpec = function(s) {
		if (!s || !s.key) return;
		that.specs[s.key] = s;
	}
	
	this.getApplicableSpec = function(item) {
		var ext = item.is_file ? item.extension.toLowerCase().trim() : "";
		if (ext.length == 0 || !that.typeConfs[ext]) {
			ext = item.is_file ? "[file]" : "[folder]";
			if (!that.typeConfs[ext])
				return that.typeConfs["*"];
		}
		return that.typeConfs[ext];
	}
	
	this.onInit = function(id, c, item, details) {
		if (!that.typeConfs || !details.itemdetails) return false;
		that.loaded = false;
	}
	
	this.getTitle = function(dataKey, rowSpec) {
		if (rowSpec.title) return rowSpec.title;
		if (rowSpec["title-key"]) return that.t(rowSpec["title-key"]);

		if (dataKey == 'name') return that.t('fileItemContextDataName');
		if (dataKey == 'size') return that.t('fileItemContextDataSize');
		if (dataKey == 'path') return that.t('fileItemContextDataPath');
		if (dataKey == 'extension') return that.t('fileItemContextDataExtension');
		if (dataKey == 'last-modified') return that.t('fileItemContextDataLastModified');
		if (dataKey == 'image-size') return that.t('fileItemContextDataImageSize');
		
		if (that.specs[dataKey]) {
			var spec = that.specs[dataKey];
			if (spec.title) return spec.title;
			if (spec["title-key"]) return that.t(spec["title-key"]);
		}
		return dataKey;
	}
	
	this.formatData = function(key, data) {
		if (key == 'size') return that.env.texts().formatSize(data);
		if (key == 'last-modified') return that.env.texts().formatInternalTime(data);
		if (key == 'image-size') return that.t('fileItemContextDataImageSizePixels', [data]);
		
		if (that.specs[key]) {
			var spec = that.specs[key];
			if (spec.formatter) return spec.formatter(data);
		}

		return data;
	}
		
	this.url = function(p) {
		return that.env.service().getPluginUrl("ItemDetails")+"client/"+p;
	}
	
	this.t = function(s, p) {
		return that.env.texts().get(s, p);
	}
}

function ExifDetails() {
	var t = this;
	
	this.formatExif = function(d) {
		var html = "<div id='item-details-exif'><table id='item-details-exif-values'>";
		for (var s in d) {
			var first = true;
			for (var k in d[s]) {
				var v = t.formatValue(s, k, d[s][k]);
				if (!v) continue;
				
				html += '<tr id="exif-row-'+s+'-'+k+'" class="'+(first?'exif-row-section-first':'exif-row')+'"><td class="exif-section">'+(first?s:'')+'</td><td class="exif-key">'+k+'</td><td class="exif-value">'+v+'</td></tr>';
				first = false;
			}
		}
		return html + "</table></div>";
	}
	
	this.formatValue = function(section, key, value) {
		if (section == 'FILE' && key == 'SectionsFound') return false;
		//TODO format values?
		return value;
	}
	
	return {
		key: "exif",
		"title-key": "fileItemDetailsExif",
		formatter: t.formatExif
	}
}

function SharePlugin() {
	var that = this;
	
	this.getPluginInfo = function() { return { id: "plugin_share" }; }
	
	this.initialize = function(env) {
		that.env = env;
		
		if (window.ZeroClipboard) {
			that.testclip = new ZeroClipboard.Client();
			that.testclip.addEventListener('load', function(client) {
				//console.log("Clipboard support detected");
				that.testclip.hide();
				
				that.clip = new ZeroClipboard.Client();
				that.clip.setHandCursor(true);
				that.clip.setCSSEffects(false);
				that.clip.addEventListener('onMouseOver', function() { that.onClipMouse(true); });
				that.clip.addEventListener('onMouseOut', function() { that.onClipMouse(false); });
				that.clip.addEventListener('onComplete', that.onClipClick);
				
				if (that.shares.length > 0) $(".share-link-copy-container").removeClass("hidden");
			});
		}

		that.clip = false;
		that.hoverId = false;
		
		mollify.importCss(that.url("style.css"));
		mollify.importScript(that.url("texts_" + that.env.texts().locale + ".js"));
		
		that.env.addItemContextProvider(function(item) {
			return {
				actions: {
					secondary: [
						{ title: "-" },
						{
							title: that.t('itemContextShareMenuTitle'),
							callback: function(item) {
								that.openShares(item);
							}
						}
					]
				}
			};
		}, function(item) {
			return {"plugin-share":[]};
		});
	}
	
	this.openShares = function(item) {
		that.env.dialog().showDialog({
			modal: false,
			title: that.t("shareDialogTitle"),
			html: "<div id='share-dialog-content' class='loading'></div>",
			on_show: function(d) { that.onShowSharesDialog(d, item); }
		});
	}

	this.onShowSharesDialog = function(d, item) {
		mollify.loadContent("share-dialog-content", that.url("content.html"), function() {
			$("#share-item-title").html(that.t(item.is_file ? 'shareDialogShareFileTitle' : 'shareDialogShareFolderTitle'));
			$("#share-item-name").html(item.name);
			$("#share-dialog-content").removeClass("loading");

			$("#add-share-btn").click(function() { that.onAddShare(item); } );
			$("#share-dialog-close").click(function() { d.close(); } );
			if (that.testclip) that.testclip.glue("clip-test");
			
			that.env.service().get("share/items/"+item.id, function(result) {
				that.refreshShares(item, result);
				that.updateShareList(item);
				d.setMinimumSizeToCurrent();
				d.center();
			},	function(code, error) {
				alert(error);
			});
		});
	}
	
	this.refreshShares = function(item, shares) {
		that.shares = shares;
		that.shareIds = [];
		
		for (var i=0, j=shares.length; i<j; i++)
			that.shareIds.push(shares[i].id);
	}
	
	this.getShare = function(id) {
		return that.shares[that.shareIds.indexOf(id)];
	}
	
	this.updateShareList = function(item) {
		$("#share-items").empty();
		
		if (that.shares.length == 0) {
			$("#share-items").html('<div class="no-share-items">'+that.t("shareDialogNoShares")+'</div>');
			return;
		}
		
		var opt = {
			name : function() {
				if (!this.data.name || this.data.name.length == 0)
					return '<text key="shareDialogUnnamedShareTitle" />';
				return this.data.name;
			},
			itemClass : function() {
				var c = "item-share";
				if (!this.data.active)
					c = c + " item-share-inactive";
				if (!this.data.name || this.data.name.length == 0)
					c = c + " item-share-unnamed";
				return c;
			},
			link : function() {
				return that.env.service().getUrl("public/"+this.data.id);
			}
		};
		
		$("#share-template").tmpl(that.shares, opt).appendTo("#share-items");
		mollify.localize("share-list");
		
		if (that.clip) $(".share-link-copy-container").removeClass("hidden");
		//else console.log("Clipboard support not detected");
		
		var initClipboard = function(id) {
			if (!that.clip) return;
			if (!that.getShare(id).active) {
				that.clip.setText("");
				that.clip.hide();
				return;
			}
			
			that.clip.show();
			that.clip.setText(that.env.service().getUrl("public/"+id));
			var el = $("#share-copy-"+id);
			if (that.clip.div) {
				that.clip.reposition(el[0]);
			} else {
				that.clip.glue(el[0]);
			}
		}

		$(".item-share").hover(
			function() {
				$(".item-share").removeClass("item-share-hover");
				
				var el = $(this);
				var id = el.attr('id').substring(6);
				el.addClass("item-share-hover");
				that.hoverId = id;
				
				initClipboard(id);
			},
			function() {
			}
		);
		//initClipboard(that.shares[0].id);
		
		var idFunction = function(i, f) {
			var p = $(i).hasClass('item-share') ? i : $(i).parentsUntil(".item-share").parent()[0];
			var id = p.id.substring(6);
			f(item, id);
		};
		$(".share-link-toggle-title").click(function() {
			var id = $(this).parent()[0].id.substring(6);
			if (!that.getShare(id).active) return;
			
			var linkContainer = $(this).next();
			var open = linkContainer.hasClass("open");
			if (!open) $(".share-link-content").removeClass("open");
			linkContainer.toggleClass("open");
			return false;
		}).hover(
			function() { $(this).addClass("hover"); },
			function() { $(this).removeClass("hover"); }
		);
		
		$(".share-edit").click(function(e) {
			idFunction(this, that.onEditShare);
			return false;
		});
		$(".share-remove").click(function(e) {
			idFunction(this, that.removeShare);
			return false;
		});
	}
	
	this.onClipMouse = function(over) {
		if (!that.hoverId) return;
		
		if (over) $("#share-copy-"+that.hoverId).addClass("hover");
		else $("#share-copy-"+that.hoverId).removeClass("hover");
	}

	this.onClipClick = function() {
		if (!that.hoverId) return;
		
		var el = $("#share-copy-"+that.hoverId);
		el.addClass("click");
		window.setTimeout(function() { el.removeClass("click"); }, 200);
	}
	
	this.closeAddEdit = function() {
		$("#share-items").removeClass("minimized");
		$("#share-context").addClass("minimized");
		$(".share-context-toolbar-option").hide();
		$("#add-share-btn").show();
	}
	
	this.openContext = function(toolbarId, contentTemplateId) {
		$("#share-items").addClass("minimized");
		$("#share-context").removeClass("minimized");
		$(".share-context-toolbar-option").hide();
		$("#"+toolbarId).show();
		$("#"+contentTemplateId).tmpl({}).appendTo($("#share-context-content").empty());
		mollify.localize("share-context-content");
		//$("#share-validity-expirationdate-value").datepicker();
	}
	
	this.onAddShare = function(item) {
		that.openContext('add-share-title', 'share-context-addedit-template');
		
		$("#share-general-name").val('');
		$('#share-general-active').attr('checked', true);

		$("#share-addedit-btn-ok").click(function() {
			var name = $("#share-general-name").val();
			var active = $("#share-general-active").is(":checked");
			var expiration = null;//mollify.parseDate(that.t('shortDateFormat'), $("#share-validity-expirationdate-value").val(), $("#share-validity-expirationtime-value").val());
			
			$("#share-items").empty().append('<div class="loading"/>');
			that.closeAddEdit();
			that.addShare(item, name || '', expiration, active);
		});
		
		$("#share-addedit-btn-cancel").click(function() {
			that.closeAddEdit();
		});
	}
	
	this.onEditShare = function(item, id) {
		that.openContext('edit-share-title', 'share-context-addedit-template');
		
		var share = that.getShare(id);
		
		$("#share-general-name").val(share.name);
		$("#share-general-active").attr("checked", share.active);
		
		$("#share-addedit-btn-ok").click(function() {
			var name = $("#share-general-name").val();
			var active = $("#share-general-active").is(":checked");
			var expiration = mollify.parseDate(that.t('shortDateFormat'), $("#share-validity-expirationdate-value").val(), $("#share-validity-expirationtime-value").val());
			
			$("#share-items").empty().append('<div class="loading"/>')
			that.closeAddEdit();
			that.editShare(item, share.id, name || '', expiration, active);
		});
		
		$("#share-addedit-btn-cancel").click(function() {
			that.closeAddEdit();
		});
	}
	
	this.addShare = function(item, name, expiration, active) {
		that.env.service().post("share/items/"+item.id, { item: item.id, name: name, expiration: mollify.formatInternalTime(expiration), active: active }, function(result) {
			that.refreshShares(item, result);
			that.updateShareList(item);
		},	function(code, error) {
			alert(error);
		});
	}

	this.editShare = function(item, id, name, expiration, active) {
		that.env.service().put("share/"+id, { id: id, name: name, expiration: mollify.formatInternalTime(expiration), active: active }, function(result) {
			var share = that.getShare(id);
			share.name = name;
			share.active = active;
			share.expiration = expiration;
			that.updateShareList(item);
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.removeShare = function(item, id) {
		that.env.service().del("share/"+id, function(result) {
			var i = that.shareIds.indexOf(id);
			that.shareIds.splice(i, 1);
			that.shares.splice(i, 1);
			that.updateShareList(item);
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.url = function(p) {
		return that.env.service().getPluginUrl("Share")+"client/"+p;
	}
	
	this.t = function(s, p) {
		return that.env.texts().get(s, p);
	}
}*/

})(window.jQuery);

(function($, mollify){
mollify.MollifyHTML5DragAndDrop = function() {
	var t = this;
	t.dragObj = false;
	t.dragEl = false;
	t.dragListener = false;
	
	var endDrag = function() {
		if (t.dragEl) {
			t.dragEl.removeClass("dragged");
			if (t.dragListener && t.dragListener.onDragEnd) t.dragListener.onDragEnd(t.dragEl, e);
			t.dragEl = false;
		}
		t.dragObj = false;
		t.dragListener = false;
	};
	
	$("body").bind('dragover', function(e) {
		if (e.preventDefault) e.preventDefault();
		e.originalEvent.dataTransfer.dropEffect = "none";
		return false;
	});
	
	return {
		enableDrag : function($e, l) {
			$e.attr("draggable","true").bind('dragstart', function(e) {
				t.dragObj = false;
				e.originalEvent.dataTransfer.effectAllowed = "none";
				if (l.onDragStart) {
					t.dragObj = l.onDragStart($(this), e);
					if (t.dragObj) {
						t.dragEl = $(this);
						t.dragListener = l;
						t.dragEl.addClass("dragged");
						e.originalEvent.dataTransfer.effectAllowed = "copyMove";
						return;
					}
				}
				return false;
			}).bind('dragend', function(e) {	
				endDrag();
			});
		},
		enableDrop : function($e, l) {
			$e.addClass("droppable").bind('drop', function(e) {
				if (e.stopPropagation) e.stopPropagation();
				if (!l.canDrop || !l.onDrop || !t.dragObj) return;
				var $t = $(this);
				if (l.canDrop($t, e, t.dragObj)) {
					l.onDrop($t, e, t.dragObj);
					$t.removeClass("dragover");
				}
				endDrag();
			}).bind('dragenter', function(e) {
				if (!l.canDrop || !t.dragObj) return false;
				var $t = $(this);
				if (l.canDrop($t, e, t.dragObj)) {
					$t.addClass("dragover");
				}
			}).bind('dragover', function(e) {
				if (e.preventDefault) e.preventDefault();
				
				var fx = "none";
				if (l.canDrop && l.dropType && t.dragObj) {
					var $t = $(this);
					if (l.canDrop($t, e, t.dragObj)) {
						var tp = l.dropType($t, e, t.dragObj);
						if (tp) fx = tp;
					}
				}
				
				e.originalEvent.dataTransfer.dropEffect = fx;
				return false;
			}).bind('dragleave', function(e) {
				$(this).removeClass("dragover");
			});
		}
	};
};

mollify.MollifyJQueryDragAndDrop = function() {
	return {
		enableDrag : function($e, l) {
			$e.draggable({
				revert: "invalid",
				distance: 10,
				addClasses: false,
				zIndex: 2700,
	            start: function(e) {
	            	if (l.onDragStart) l.onDragStart($(this), e);
	            }
			});
		}
	};
};
})(window.jQuery, window.mollify);

/* Common */

function isArray(o) {
	return Object.prototype.toString.call(o) === '[object Array]';
}

if(typeof String.prototype.trim !== 'function') {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, ''); 
	}
}

function def(o) {
	return (typeof(o) != 'undefined');
}

if (!Array.prototype.indexOf) { 
    Array.prototype.indexOf = function(obj, start) {
         for (var i = (start || 0), j = this.length; i < j; i++) {
             if (this[i] === obj) { return i; }
         }
         return -1;
    }
}

if (!Array.prototype.remove) { 
	Array.prototype.remove = function(from, to) {
		if (typeof(to) == 'undefined' && typeof(from) == 'object')
			from = this.indexOf(from);
		var rest = this.slice((to || from) + 1 || this.length);
		this.length = from < 0 ? this.length + from : from;
		return this.push.apply(this, rest);
	};
}

function strpos(haystack, needle, offset) {
    // Finds position of first occurrence of a string within another  
    // 
    // version: 1109.2015
    // discuss at: http://phpjs.org/functions/strpos
    // +   original by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
    // +   improved by: Onno Marsman    
    // +   bugfixed by: Daniel Esteban
    // +   improved by: Brett Zamir (http://brett-zamir.me)
    var i = (haystack + '').indexOf(needle, (offset || 0));
    return i === -1 ? false : i;
}

/**
*
*  Base64 encode / decode
*  http://www.webtoolkit.info/
*
**/
 
var Base64 = {
 
	// private property
	_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
 
	// public method for encoding
	encode : function (input) {
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = Base64._utf8_encode(input);
 
		while (i < input.length) {
 
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);
 
			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;
 
			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}
 
			output = output +
			this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
			this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);
 
		}
 
		return output;
	},
 
	// public method for decoding
	decode : function (input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
 
		while (i < input.length) {
 
			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));
 
			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;
 
			output = output + String.fromCharCode(chr1);
 
			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}
 
		}
 
		output = Base64._utf8_decode(output);
 
		return output;
 
	},
 
	// private method for UTF-8 encoding
	_utf8_encode : function (string) {
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";
 
		for (var n = 0; n < string.length; n++) {
 
			var c = string.charCodeAt(n);
 
			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
 
		}
 
		return utftext;
	},
 
	// private method for UTF-8 decoding
	_utf8_decode : function (utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;
 
		while ( i < utftext.length ) {
 
			c = utftext.charCodeAt(i);
 
			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			}
			else if((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i+1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else {
				c2 = utftext.charCodeAt(i+1);
				c3 = utftext.charCodeAt(i+2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}
 
		}
 
		return string;
	}
 
}