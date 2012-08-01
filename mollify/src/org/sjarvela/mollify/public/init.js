(function(){
	window.mollify = new function(){
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
			t.env = core;
			t.ui.texts = t.env.texts();
			t.service = t.env.service();
			t.session = {};
			
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
				login : new LoginView(),
				mainview : new MainView(),
				
				dialogs : new DialogHandler()
			}
			t.env.views().registerHandlers(t.ui.views);
			
			t.plugins.initialize(t.env);
				
			t.templates.load("dialogs.html");
			
			//$.datepicker.setDefaults({
			//	dateFormat: e.texts().get('shortDateFormat').replace(/yyyy/g, 'yy')
			//});
			if (cb) cb();
		}
		
		this.plugins = new function() {
			var pl = this;
			this.list = {};
			this.info = {};
			
			this.register = function(p) {
				var info = p.getPluginInfo();
				if (!info) return;
				var id = info.id;
				if (!id) return;
				
				pl.list[id] = p;
				pl.info[id] = info;
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
				return t.env.service().getPluginUrl(id)+"client/"+p;
			};
			
			this.getItemContextData = function(item, d) {
				var data = {};
				for (var id in pl.list) {
					var plugin = pl.list[id];
					var pluginData = plugin.getItemContextData(item, d);
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
			
			loadContent : function(id, url, handler, process) {
				var $target = $("#"+id);
				$target.load(t.urlWithParam(url, "_="+mollify.time), function() {
					if (process) t.ui.process($target, process, handler);
					if (typeof handler === 'function') handler();
					else if (handler.onLoad) handler.onLoad(id);
				});
			},
			
			template : function(id, data, opt) {
				return $("#"+id).tmpl(data, opt);
			}
		}
		
		this.ui = {
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
			
			hideAllPopups: function() {
				$(".mollify-popup").qtip('hide');
			},
			
			handlers : {
				hintbox : function(p, h) {
					p.find("input.hintbox").each(function() {
						var $this = $(this);
						var hint = t.env.texts().get($this.attr('hint-key'));
						$this.attr("placeholder", hint).removeAttr("hint-key");
					}).placeholder();
				},
	
				localize : function(p, h) {
					p.find(".localized").each(function() {
						var key = $(this).attr('title-key');
						if (key)
							$(this).attr("title", t.env.texts().get(key));
						
						key = $(this).attr('text-key');
						if (key)
							$(this).text(t.env.texts().get(key));
					});
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
					p.find(".bubble-action").each(function() {
						var $t = $(this);
						var b = mollify.ui.controls.bubble($t, h);
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
				hoverDropdown : function(a) {
					var $e = $(a.element);
					$e.addClass('hover-dropdown');
					$e.hover(function() {
						$(this).addClass("hover");
					}, function() {
						$(this).removeClass("hover");
					});
					$('<div class="mollify-dropdown-handle"></div>').click(function(){
						mollify.ui.controls.popupmenu(a);
					}).appendTo($e);
				},
				
				popupmenu : function(a) {
					var $e = $(a.element);
					var createItems = function(itemList) {
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
					};
				},
				
				bubble: function(e, h) {
					var actionId = e.attr('id');
					if (!actionId) return;
					
					var content = $("#" + actionId + '-bubble');
					if (!content || content.length == 0) return;

					var html = content.html();
					content.remove();
					
					e.qtip({
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
					return {};
				},

				dynamicBubble: function(e, c, h) {
					var bubbleHtml = function(c) {
						if (!c) return "";
						if (typeof(c) === 'string') return c;
						return $("<div/>").append(c).html();
					};
					var html = c ? bubbleHtml(c) : '<div class="loading"></div>';
					
					var tip = e.qtip({
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
					tip.show();
					
					return {
						show: function() {
							tip.show();	
						},
						hide: function() {
							tip.hide();
						},
						close: this.hide,
						content: function(c) {
							tip.set('content.text', bubbleHtml(c));
						}
					};
				},
				
				radio: function(e, h) {
					var rid = e.attr('id');
					var items = e.find("a");
					
					var select = function(item) {
						items.removeClass("selected");
						item.addClass("selected");
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
})();

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

function DialogHandler() {
	var dialogDefaults = {
		title: "Mollify"
	};
	
	this.info = function(spec) {
		var dlg = $("#mollify-tmpl-dialog-info").tmpl($.extend(spec, dialogDefaults)).dialog({
			modal: true,
			resizable: false,
			height: 'auto',
			minHeight: 50
		});
		mollify.ui.handlers.localize(dlg);
		dlg.find("#mollify-info-dialog-close-button").click(function() { dlg.dialog('destroy'); dlg.remove(); });
	};
	
	this.error = function(spec) {
		alert("error");
	};
	
	this.confirmation = function(spec) {
		alert("confirm");
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
		var $trg = (spec && spec.target) ? $("#"+spec.target) : $("body");
		var notification = mollify.dom.template("mollify-tmpl-notification", $.extend(spec, dialogDefaults)).hide().appendTo($trg).fadeIn(300);
		setTimeout(function() {	notification.fadeOut(300); }, spec.time | 3000);
	};
	
	this.custom = function(spec) {
		var dlg = $("#mollify-tmpl-dialog-custom").tmpl($.extend(spec, dialogDefaults), {
			getContent: function() {
				if (spec.html) return spec.html;
				if (spec.content) {
					var c = spec.content;
					if (typeof c === 'string') return c;
					return $("<div/>").append(c).html();
				}
				return "";
			},
			getButtonTitle: function(b) {
				if (b["title"]) return b["title"];
				if (b["title-key"]) return mollify.ui.texts.get(b["title-key"]);
				return "";
			}
		}).dialog({
			modal: true,
			resizable: false,
			height: 'auto',
			minHeight: 50
		});
		mollify.ui.handlers.localize(dlg);
		var h = {
			close: function() {
				dlg.dialog('destroy');
				dlg.remove();
			}
		};
		dlg.find(".button").click(function(e) {
			e.preventDefault();
			if (spec["on-button"]) spec["on-button"](e, h);
		});
		if (spec["on-show"]) spec["on-show"](h);
		return h;
	};
}

function LoginView() {
	var that = this;
	
	this.init = function(listener) {
		that.listener = listener;
	}
	
	this.render = function(id) {
		mollify.dom.loadContent(id, mollify.templates.url("login-view.html"), that, ['localize', 'hintbox', 'bubble']);
	}
	
	this.onLoad = function() {
		$(window).resize(that.onResize);
		that.onResize();
	
		if (mollify.hasFeature('lost_password')) $("#login-lost-password").show();
		if (mollify.hasFeature('registration')) {
			$("#login-register").click(function() {
				mollify.ui.window.open(mollify.service.getPluginUrl("registration"));
			});
			$("#login-register").show();
		}
		
		var $data = $("#login-data");
		mollify.ui.handlers.center($data);
		//mollify.ui.handlers.bubble($data, that);
		$("#login-name, #login-password").bind('keypress', function(e) {
			if ((e.keyCode || e.which) == 13) that.onLogin();
		});
		$("#login-button").click(that.onLogin);
		$("#login-name").focus();
		
		//		mollify.views.dialogs.info({message:'tt'});
		//		return;
	}
	
	this.onResize = function() {
		var h = $(window).height();
		$("#login-main").height(h);
		
		$data = $("#login-data");
		$data.css('margin-top', (h / 2) - ($data.height() / 2));
	}
	
	this.onRenderBubble = function(id, bubble) {
		if (id === 'login-forgot-password') {
			$("#login-forgot-button").click(function() {				
				var email = $("#login-forgot-email").val();
				if (!email) return;
				
				bubble.hide();
				that.wait = mollify.ui.views.dialogs.wait({target: "login-main"});
				that.listener.onResetPassword(email);
			});
		}
	}
	
	this.onShowBubble = function(id, bubble) {
		if (id === 'login-forgot-password') {
			$("#login-forgot-email").val("").focus();
		}
	}
	
	this.onLogin = function() {
		var username = $("#login-name").val();
		var password = $("#login-password").val();
		var remember = $("#login-remember-cb").is(':checked');
		
		if (!username || username.length < 1) {
			$("#login-name").focus();
			return;
		}
		if (!password || password.length < 1) {
			$("#login-password").focus();
			return;
		}
		that.wait = mollify.ui.views.dialogs.wait({target: "login-main"});
		that.listener.onLogin(username, password, remember);
	}
	
	this.showLoginError = function() {
		that.wait.close();
		
		mollify.ui.views.dialogs.notification({
			target: "login-main",
			message: mollify.ui.texts.get('loginDialogLoginFailedMessage')
		});
	}
	
	this.onResetPasswordSuccess = function() {
		that.wait.close();
		
		mollify.ui.views.dialogs.notification({
			target: "login-main",
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

function CommentPlugin() {
	var that = this;
	
	this.getPluginInfo = function() {
		return {
			id: "plugin-comment",
			itemContextData : that.getItemContextData
		};
	}
	
	this.initialize = function(core) {
		that.core = core;
		/*that.env.registerContextPlugin(function(item) {
			return {
				components : [{
					html: "",
					on_init: function(id, c, item, details) {
						if (!details["plugin-comment"]) return;
						
						$("#"+id).html("<div id='details-comments'><div id='details-comments-content'><div id='details-comments-icon'/><div id='details-comment-count'>"+details["plugin-comment"].count+"</div></div></div>");
						
						$("#details-comments-content").hover(
							function () { $(this).addClass("hover"); }, 
							function () { $(this).removeClass("hover"); }
						);
						$("#details-comments-content").click(function() {
							c.close();
							that.openComments(item);
						});
					}
				}]
			};
		}, function(item) {
			return {"plugin-comment":["count"]};
		});*/
		
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
/*			"on-render": function() {
				$(".filelist-item-comment-count,.filelist-item-comment-count-none").click(that.onListCellClick);
			},*/
			"on-click": function(item) {
				that.showCommentsBubble(item, $("#item-comment-count-"+item.id));
			}
		});
		/*env.addListColumnSpec({
			"id": "comment-count",
			"request-id": "plugin-comment-count",
			"default-title-key": "",
			"content": that.getListCellContent,
			"request": function(parent) { return {}; },
			"on-render": function() {
				var onclick = function(e) {
					var id = e.target.id.substring(19);
					var item = that.env.fileview().item(id);
					that.openComments(item);
				}
//				var tooltip = "<div class='filelist-item-comment-tooltip mollify-tooltip'>" + that.t("commentsFileListAddTitle") + "</div>";
				$(".filelist-item-comment-count,.filelist-item-comment-count-none").click(onclick);//.simpletip({content: tooltip, fixed: true, position: 'left'});
			}
		});
		
		if (mollify.hasPlugin("plugin-itemdetails"))
			mollify.getPlugin("plugin-itemdetails").addDetailsSpec({
				key: "comments-count",
				"title-key": "commentsDetailsCount"
			});*/
	};
	
	this.getItemContextData = function(item, data) {
		return {
			details: {
				"title-key": "pluginCommentContextTitle",
				"on-render": function(e) { that.renderItemContextDetails(item, e); }
			},
			actions: [
				{ title: 'foo', callback: function() { alert("foo"); } }
			]
		};
	};
	
	this.renderItemContextDetails = function(item, $e) {
		$e.html("comments: "+item.name);
	};
	
	/*this.onListCellClick = function(e) {
		e.preventDefault();
		var id = e.target.id.substring(19);
		var item = that.env.fileview().item(id);
		that.openComments(item);
		return false;
	};*/
	
	this.getListCellContent = function(item, data) {
		if (!item.id || item.id.length == 0 || !data || !data["plugin-comment-count"]) return "";
		var counts = data["plugin-comment-count"];

		if (!counts[item.id])
			return "<div id='item-comment-count-"+item.id+"' class='filelist-item-comment-count-none'></div>";
		
		return "<div id='item-comment-count-"+item.id+"' class='filelist-item-comment-count'>"+counts[item.id]+"</div>";
	}
	
	this.showCommentsBubble = function(item, e) {
		var bubble = mollify.ui.controls.dynamicBubble(e);
		
		mollify.templates.load("comments-content", mollify.plugins.url("Comment", "content.html"), function() {
			bubble.content(mollify.dom.template("comments-content-template", item));
			$("#comments-list").removeClass("loading");
			/*$("#comments-dialog-content .mollify-actionlink").hover(
				function () { $(this).addClass("mollify-actionlink-hover"); }, 
				function () { $(this).removeClass("mollify-actionlink-hover"); }
			);*/
	
			$("#comments-dialog-add").click(function() { that.onAddComment(bubble, item); } );
			
			mollify.service.get("comment/"+item.id, function(result) {
				that.onShowComments(item, result);
			}, function(code, error) {
				alert(error);
			});
			
			/*mollify.ui.views.dialogs.custom({
				"title": mollify.ui.texts.get("commentsDialogTitle"),
				"content": mollify.dom.template("comments-content-template", item),
				"on-show": function(d) { that.onShowCommentsDialog(d, item); },
				"on-button": function(id, d) {
					d.close();
				},
				buttons: [
					{id: 'close', "title-key":'dialogCloseButton'}
				]
			});*/
		});
	}
	
	this.onAddComment = function(d, item) {
		var comment = $("#comments-dialog-add-text").val();
		if (!comment || comment.length == 0) return;
		
		mollify.service.post("comment/"+item.id, { comment: comment }, function(result) {
			d.close();
			
			var e = document.getElementById("item-comment-count-"+item.id);
			e.innerHTML = result.count;
			e.setAttribute('class', 'filelist-item-comment-count');
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.onRemoveComment = function(item, id) {		
		mollify.service.del("comment/"+item.id+"/"+id, function(result) {
			that.onShowComments(item, result);
		},	function(code, error) {
			alert(error);
		});
	}
	
	this.onShowComments = function(item, comments) {
		if (comments.length == 0) {
			$("#comments-list").html("<span class='message'>"+mollify.ui.texts.get("commentsDialogNoComments")+"</span>");
			return;
		}
		
		var isAdmin = mollify.session.admin;
		var userId = mollify.session['user_id'];
		
		for (var i=0,j=comments.length; i<j; i++) {
			comments[i].time = mollify.ui.texts.formatInternalTime(comments[i].time);
			comments[i].comment = comments[i].comment.replace(new RegExp('\n', 'g'), '<br/>');
			comments[i].remove = isAdmin || (userId == comments[i]['user_id']);
		}

		mollify.dom.template("comment-template", comments).appendTo($("#comments-list").empty());
		//mollify.localize("comments-list");
		$(".comment-content").hover(
			function () { $(this).addClass("hover"); }, 
			function () { $(this).removeClass("hover"); }
		);
		$(".comment-remove-action").click(function(e) {
			e.preventDefault();
			var comment = $(this).tmplItem().data
			that.onRemoveComment(item, comment.id);
		});
	}
	
	/*
	
	this.t = function(s) {
		return that.env.texts().get(s);
	}	*/
}

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