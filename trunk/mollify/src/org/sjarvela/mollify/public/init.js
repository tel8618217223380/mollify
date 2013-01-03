(function($){window.mollify = new function() {
	var defaults = {
		"template-url": "client/templates/",
		"service-path": "backend/",
		"list-view-columns": {
			"name": { width: 250 },
			"size": {},
			"file-modified": { width: 150 }
		}
	};
	var t = this;
	t.time = new Date().getTime();
	
	this.settings = {};

	this.init = function(s, p) {
		if (p) {
			for (var i=0, j=p.length; i < j; i++)
				t.plugins.register(p[i]);
		}
		
		t.settings = $.extend({}, defaults, s);
	}
	
	this.setup = function(core, cb) {
		t.hiddenInd = 0;
		t.env = core;
		t.ui.texts = t.env.texts();
		t.service = t.env.service();
		t.session = {};
		t.filesystem = core.filesystem();
		
		core.addEventHandler(function(e) {
			if (e.type == 'SESSION_START') {
				t.session = core.session.get();
			} else if (e.type == 'SESSION_END') {
				t.session = {};
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
				return item.is_file ? t.env.texts().formatSize(item.size) : '';
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
				return t.env.texts().formatInternalTime(data["core-file-modified"][item.id]);
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
		
		t.ui.views = {
			login : new mollify.view.LoginView(),
			mainview : new mollify.view.MainView(),
			
			dialogs : new mollify.view.DialogHandler(t.filesystem)
		}
		t.env.views().registerHandlers(t.ui.views);
		
		t.plugins.initialize(t.env);
			
		t.templates.load("dialogs.html");
		
		if (!mollify.ui.uploader) mollify.ui.uploader = new mollify.plugin.MollifyUploader(t.env);
		if (!mollify.ui.draganddrop) mollify.ui.draganddrop = (Modernizr.draganddrop) ? new mollify.MollifyHTML5DragAndDrop() : new mollify.MollifyJQueryDragAndDrop();
		
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
		if (cb) cb();
	}
	
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
			for (var id in pl.list) {
				var plugin = pl.list[id];
				if (!plugin.itemContextHandler) continue;
				var pluginData = plugin.itemContextHandler(item, d, d.plugins[id]);
				if (pluginData) data[id] = pluginData;
			}
			return data;
		}
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
	}
	
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
				t.ui.filelist.columns.push(c);
				if (t.settings["list-view-columns"][c.id])
					t.ui.filelist.columns[c.id] = $.extend({}, c, t.settings["list-view-columns"][c.id]);
			}
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
					var key = $(this).attr('title-key');
					if (key)
						$(this).attr("title", t.env.texts().get(key));
					
					key = $(this).attr('text-key');
					if (key)
						$(this).prepend("<span>"+t.env.texts().get(key)+"</span>");
				});
				p.find("input.hintbox").each(function() {
					var $this = $(this);
					var hint = t.env.texts().get($this.attr('hint-key'));
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
							return i.title == '-';
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
						//if (!a.parentPopupId)
						popupId = t.ui.activePopup(api);
						if (!popupItems) $mnu.addClass("loading");
						if (a.onShow) a.onShow(api, popupItems);
					},
					onhide: function() {
						hidePopup();
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
				/*var createItems = function(itemList) {
					return $("<div/>").append(mollify.dom.template("mollify-tmpl-popupmenu", {items:itemList}, {
						isSeparator : function(i) {
							return i.title == '-';
						},
						getTitle : function(i) {
							if (i.title) return i.title;
							if (i['title-key']) return mollify.ui.texts.get(i['title-key']);
							return "";
						}
					})).html();
				};
				var initItems = function(l, api) {
					var $items = api.elements.content.find(".mollify-popupmenu-item");
					$items.click(function() {
						var item = l[$(this).index()];
						api.hide();
						if (a.onItem) a.onItem(api, item);
						item.callback();
					});
				};
				var html = a.items ? createItems(a.items) : '<div class="loading"></div>';
				var cls = 'mollify-popup ui-tooltip-light ui-tooltip-shadow ui-tooltip-rounded ui-tooltip-tipped';
				if (a.style) cls = cls + " " + a.style;
				
				var tip = $e.qtip({
					content: html,
					position: {
						my: a.positionMy || 'top left',
						at: a.positionAt || 'bottom left',
						container: a.container || $('#mainview-content')
					},
					hide: {
						target: a.hideTarget || false,
						delay: a.hideDelay || 200,
						fixed: a.hideFixed || true,
						event: a.hideEvent || 'mouseleave'
					},
					style: {
						tip: false,
						classes: cls
					},
					events: {
						render: function(e, api) {
							initItems(a.items, api);
						},
						show: function(e, api) {
							if (a.onShow) a.onShow(api);
						},
						hide: function(e, api) {
							if (a.onHide) a.onHide(api);
							api.destroy();
						},
						blur: function(e, api) {
							if (a.onBlur) a.onBlur(api);
						}
					}
				}).qtip('api');
				tip.show();
				
				return {
					hide: function() {
						tip.hide();
					},
					items: function(items) {
						tip.set('content.text', createItems(items));
						initItems(items, tip);
					}
				};*/
			},
			
			bubble: function(o) {
				var e = o.element;
				var actionId = e.attr('id');
				if (!actionId) return;
				
				var content = $("#" + actionId + '-bubble');
				if (!content || content.length == 0) return;

				var html = content.html();
				content.remove();
				
				/*e.qtip({
					content: html,
					position: {
						my: 'top center',
						at: 'bottom center'
					},
					show: 'click',
					hide: {
						delay: 200,
						fixed: true,
						event: 'click mouseleave'
					},
					style: {
						tip: true,
						classes: 'mollify-popup ui-tooltip-light ui-tooltip-shadow ui-tooltip-rounded ui-tooltip-tipped'
					},
					events: {
						render: function(e, api) {
							if (!h || !h.onRenderBubble) return;
							h.onRenderBubble(actionId, api);
						},
						visible: function(e, api) {
							if (!h || !h.onShowBubble) return;
							h.onShowBubble(actionId, api);
						},
						hide: function(e, api) {
							//api.destroy();
						}
					}
				});
				return {};*/
				var $tip = false;
				var rendered = false;
				var api = {
					hide: function() {
						e.popover('hide');
					},
					close: this.hide
				};
				e.popover({
					title: false,
					html: true,
					placement: 'bottom',
					trigger: 'click',
					template: '<div class="popover mollify-bubble-popover"><div class="arrow"></div><div class="popover-inner"><div class="popover-content"><p></p></div></div></div>',
					content: html,
					onshow: function($t) {
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
					}
				});
			},

			dynamicBubble: function(o) {
				//e, c, h
				var e = o.element;
				//var handler = o.handler;
				
				var bubbleHtml = function(c) {
					if (!c) return "";
					if (typeof(c) === 'string') return c;
					return $("<div/>").append(c).html();
				};
				var html = o.content ? bubbleHtml(o.content) : '<div class="loading"></div>';
				
				/*var tip = e.qtip({
					content: html,
					position: {
						my: 'top center',
						at: 'bottom center'
					},
					hide: {
						delay: 1000,
						fixed: true,
						event: 'mouseleave'
					},
					style: {
						tip: true,
						classes: 'mollify-popup ui-tooltip-light ui-tooltip-shadow ui-tooltip-rounded ui-tooltip-tipped'
					},
					events: {
						render: function(e, api) {
							if (!h || !h.onRenderBubble) return;
							h.onRenderBubble(api);
						},
						visible: function(e, api) {
							if (!h || !h.onShowBubble) return;
							h.onShowBubble(api);
						},
						hide: function(e, api) {
							api.destroy();
						}
					}
				}).qtip('api');
				tip.show();*/
				var $tip = false;
				var api = {
					show: function() {
						e.popover('show');
					},
					hide: function(dontDestroy) {
						if (dontDestroy) $tip.hide();
						else e.popover('destroy');
					},
					element : function() {
						return $tip;
					},
					getContent: function() {
						return $tip.find('.popover-content');	
					},
					content: function(c) {
						//var $t = e.popover('tip');
						var $c = $tip.find('.popover-content');
						$c.html(bubbleHtml(c));
					}
				};
				api.close = api.hide;
				e.popover({
					title: o.title ? o.title : false,
					html: true,
					placement: 'bottom',
					trigger: 'manual',
					template: '<div class="popover mollify-bubble-popover"><div class="arrow"></div><div class="popover-inner">' + (o.title ? '<h3 class="popover-title"></h3>' : '') + '<div class="popover-content"><p></p></div></div></div>',
					content: html,
					manualout: true,
					onshow: function($t) {
						$tip = $t;
						$tip.click(function(e){
							//return false;
						});
						t.ui.activePopup(api);
						if (o.title) {
							var closeButton = $('<button type="button" class="close">×</button>').click(function(){
								e.popover('destroy');
							});
							$t.find('.popover-title').append(closeButton);
						}
						mollify.ui.handlers.localize($tip);
						if (o.handler && o.handler.onRenderBubble) o.handler.onRenderBubble(api);
					},
					onhide: function($t) {
						t.ui._activePopup = false;
						//e.popover('destroy');
					}
				});
				e.popover('show');
				
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
						$label.html(originalValue);
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
					}
				};
				$dlg.find(".btn").click(function(e) {
					e.preventDefault();
					var data = $(this).tmplItem().data;
					var btn = data.buttons[$(this).index()];
					if (spec["on-button"]) spec["on-button"](btn, h);
				});
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
			
			this.init = function(listener) {
				that.listener = listener;
			}
			
			this.render = function(id) {
				mollify.dom.loadContentInto($('#'+id), mollify.templates.url("loginview.html"), that, ['localize', 'bubble']);
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
						that.wait = mollify.ui.views.dialogs.wait({target: "mollify-login-main"});
						that.listener.onResetPassword(email);
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
				that.wait = mollify.ui.views.dialogs.wait({target: "mollify-login-main"});
				that.listener.onLogin(username, password, remember);
			}
			
			this.showLoginError = function() {
				that.wait.close();
				
				mollify.ui.views.dialogs.notification({
					message: mollify.ui.texts.get('loginDialogLoginFailedMessage')
				});
			}
			
			this.onResetPasswordSuccess = function() {
				that.wait.close();
				
				mollify.ui.views.dialogs.notification({
					message: mollify.ui.texts.get('resetPasswordPopupResetSuccess')
				});
			}
			
			this.onResetPasswordFailed = function() {
				that.wait.close();
				
				mollify.ui.views.dialogs.info({
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
					return that.formatters[g].getGroupRows(s, d[g]);
				if (g == 'exif') return that.getExifRows(s, d[g]);
				
				// file rows
				var rows = [];
				for (var k in s) {
					if (k == 'exif' || that.formatters[k]) continue;
					var spec = s[k];

					var rowData = d[k];
					if (!rowData) continue;
					
					rows.push({
						title: that.getRowTitle(k, s[k]),
						value: that.formatData(k, rowData)
					});
				}
				return rows;
			};
			
			this.getExifRows = function(s, d) {
				var rows = [];
				for (var k in d) {
					var v = that.formatExifValue(s, k, d[k]);
					if (!v) continue;
					
					rows.push({title: k, value: v});
				}
				return rows;
			};
			
			this.getRowTitle = function(dataKey, rowSpec) {
				if (rowSpec.title) return rowSpec.title;
				if (rowSpec["title-key"]) return mollify.ui.texts.get(rowSpec["title-key"]);
		
				if (dataKey == 'name') return mollify.ui.texts.get('fileItemContextDataName');
				if (dataKey == 'size') return mollify.ui.texts.get('fileItemContextDataSize');
				if (dataKey == 'path') return mollify.ui.texts.get('fileItemContextDataPath');
				if (dataKey == 'extension') return mollify.ui.texts.get('fileItemContextDataExtension');
				if (dataKey == 'last-modified') return mollify.ui.texts.get('fileItemContextDataLastModified');
				if (dataKey == 'image-size') return mollify.ui.texts.get('fileItemContextDataImageSize');
				//if (dataKey == 'exif') return mollify.ui.texts.get('fileItemDetailsExif');
				
				/*if (that.specs[dataKey]) {
					var spec = that.specs[dataKey];
					if (spec.title) return spec.title;
					if (spec["title-key"]) return mollify.ui.texts.get(spec["title-key"]);
				}*/
				return dataKey;
			};
			
			this.formatData = function(key, data) {
				if (key == 'size') return mollify.ui.texts.formatSize(data);
				if (key == 'last-modified') return mollify.ui.texts.formatInternalTime(data);
				if (key == 'image-size') return mollify.ui.texts.get('fileItemContextDataImageSizePixels', [data]);
				//if (key == 'exif') return that.formatExif(data);
				
				if (that.specs[key]) {
					var spec = that.specs[key];
					if (spec.formatter) return spec.formatter(data);
				}
		
				return data;
			};
			
			/*this.formatExif = function(d) {
				var html = "<div id='item-details-exif'><table id='item-details-exif-values'>";
				for (var s in d) {
					var first = true;
					for (var k in d[s]) {
						var v = that.formatExifValue(s, k, d[s][k]);
						if (!v) continue;
						
						html += '<tr id="exif-row-'+s+'-'+k+'" class="'+(first?'exif-row-section-first':'exif-row')+'"><td class="exif-section">'+(first?s:'')+'</td><td class="exif-key">'+k+'</td><td class="exif-value">'+v+'</td></tr>';
						first = false;
					}
				}
				return html + "</table></div>";
			};*/
			
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
				
			this.onView = function(item, all, spec) {
				var loaded = {};
				var list = [{
					embedded: spec.embedded,
					full: spec.full,
					item: item
				}];
				var init = list[0];
				var visible = false;
				init.init = true;
				
				var $lb;
				var $lbc;
				var resize = function() {
					$lbc.css({
						"max-width": ($(window).width()-100)+"px",
						"max-height": ($(window).height()-100)+"px"
					});
					$lb.lightbox('center');
				};
				$(window).resize(resize);
				var load = function(itm) {
					var id = itm.item.id;
					if (loaded[id]) return;
					$.ajax({
						type: 'GET',
						url: itm.embedded
					}).done(function(data) {
						loaded[id] = true;
						
						var $ic = $("#mollify-fileviewereditor-viewer-item-"+id).find(".mollify-fileviewereditor-viewer-item-content");
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
				
				$lb = $v.lightbox({backdrop: true, resizeToFit: false, show: false});
				$lb.find("button.close").click(function(){
					$lb.lightbox('hide');
					$v.remove();
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
				load(init);
			};
						
			return {
				id: "plugin-fileviewereditor",
				initialize: that.initialize,
				itemContextHandler : function(item, details, data) {
					if (!data) return false;
					
					var previewerAvailable = !!data.preview;
					var viewerAvailable = !!data.view;
					var editorAvailable = false;	//TODO
					
					var result = {};
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
						result.actions = [
							{ id: 'pluginFileViewerEditorView', title: 'pluginFileViewerEditorView', type:"primary", callback: function() {
								that.onView(item, [], data.view);
							}}
						];
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
						},
						actions: [
							{ id: 'pluginCommentFoo', title: 'foo', callback: function() { alert("foo"); } }
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
	
	return {
		enableDrag : function($e, l) {
			$e.attr("draggable","true").bind('dragstart', function(e) {
				t.dragObj = false;
				if (l.onDragStart) {
					t.dragObj = l.onDragStart($(this), e);
					if (t.dragObj) $t.addClass("dragged");
				}
			}).bind('dragover', function(e) {
				if (e.preventDefault) e.preventDefault();
				e.originalEvent.dataTransfer.dropEffect = 'move';
			}).bind('dragend', function(e) {
				if (l.onDragEnd) l.onDragEnd($(this), e);
				$t.removeClass("dragged");
			});
		},
		enableDrop : function($e, l) {
			$e.addClass("droppable").bind('drop', function(e) {
				if (e.stopPropagation) e.stopPropagation();
				if (!l.canDrop || !l.onDrop) return;
				var $t = $(this);
				if (l.canDrop($t, e, t.dragObj)) {
					l.onDrop($t, e, t.dragObj);
					$t.removeClass("dragover");
				}
				t.dragObj = false;
			}).bind('dragenter', function(e) {
				if (!l.canDrop) return;
				var $t = $(this);
				if (l.canDrop($t, e, t.dragObj)) {
					$t.addClass("dragover");
				}
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