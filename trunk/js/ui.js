!function($, mollify) {

	"use strict"; // jshint ;_;
	
	var t = mollify;
	
	/* TEXTS */
	mollify.ui.texts = {};
	var tt = mollify.ui.texts;
	
	tt.locale = null;
	tt._dict = {};
			
	tt.add = function(locale, t) {
		if (!locale || !t) return;
		
		if (!tt.locale) tt.locale = locale;
		else if (locale != tt.locale) return;
		
		for (var id in t) tt._dict[id] = t[id];
	};
	
	tt.get = function(id, p) {
		if (!id) return "";
		var t = tt._dict[id];
		if (!t) return "!"+tt.locale+":"+id;
		if (p) {
			if (!window.isArray(p)) p = [p];
			for (var i=0,j=p.length; i<j; i++)
				t = t.replace("{" + i + "}", p[i]);
		}
		return t;
	};
	
	/* FORMATTERS */
	
	t.ui.formatters = {
		ByteSize : function(nf) {			
			this.format = function(bytes) {		
				if (bytes < 1024)
					return (bytes == 1 ? t.ui.texts.get('sizeOneByte') : t.ui.texts.get('sizeInBytes', nf.format(bytes)));
		
				if (bytes < (1024 * 1024)) {
					var kilobytes = bytes / 1024;
					return (kilobytes == 1 ? t.ui.texts.get('sizeOneKilobyte') : t.ui.texts.get('sizeInKilobytes', nf.format(kilobytes)));
				}
		
				if (bytes < (1024 * 1024 * 1024)) {
					var megabytes = bytes / (1024 * 1024);
					return t.ui.texts.get('sizeInMegabytes', nf.format(megabytes));
				}
		
				var gigabytes = bytes / (1024 * 1024 * 1024);
				return t.ui.texts.get('sizeInGigabytes', nf.format(gigabytes));
			};
		},
		Timestamp : function(fmt) {
			this.format = function(ts) {
				/*var s = fmt;
				s = s.replace('yyyy', ts.getFullYear());
				s = s.replace('M', ts.getMonth()+1);
				s = s.replace('d', ts.getDay());
				s = s.replace('h', ts.getHours());
				s = s.replace('hh', ts.getHours());
				s = s.replace('mm', ts.getMinutes());
				s = s.replace('ss', ts.getSeconds());
				s = s.replace('a', (ts.getHours() < 12 ? "AM" : "PM"));*/
				return ts.toString(fmt);
			};
		},
		Number : function(precision, ds) {
			this.format = function(n) {
				if (!window.def(n) || typeof(n) !== 'number') return "";
				
				var s = Math.pow(10, precision);
				var v = Math.floor(n * s) / s;
				var sv = v.toString();
				if (ds) sv = sv.replace(".", ds);
				return sv;
			};
		}
	};
	

	
	/* UI */
	
	t.ui._activePopup = false;
	
	t.ui.hideActivePopup = function() {
		if (t.ui._activePopup) t.ui._activePopup.hide();
		t.ui._activePopup = false;
	};
	
	t.ui.activePopup = function(p) {
		if (p===undefined) return t.ui._activePopup;
		if (t.ui._activePopup) {
			if (p.parentPopupId && t.ui._activePopup.id == p.parentPopupId) return;
			t.ui._activePopup.hide();
		}
		t.ui._activePopup = p;
		if (!t.ui._activePopup.id) t.ui._activePopup.id = new Date().getTime();
		return t.ui._activePopup.id;
	};
	
	t.ui.isActivePopup = function(id) {
		return (t.ui._activePopup && t.ui._activePopup.id == id);
	};
	
	t.ui.removeActivePopup = function(id) {
		if (!id || !t.ui.isActivePopup(id)) return;
		t.ui._activePopup = false;
	};
		
	t.ui.itemContext = function(o) {
		var ict = {};
		ict._activeItemContext = false;
		
		ict.open = function(item, $e, $c, $t) {
			var popupId = "mainview-itemcontext-"+item.id;
			if (mollify.ui.isActivePopup(popupId)) {
				return;
			}
			
			var openedId = false;
			if (ict._activeItemContext) {
				openedId = ict._activeItemContext.item.id;
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
				arrowPos = Math.max(0, (arrowPos - popLeft));
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
		
		ict.renderItemContext = function(cApi, $e, item, details) {
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
				var $pae = $e.find(".mollify-itemcontext-primary-actions-button");
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
				var selectorClick = function() {
					var s = $(this).tmplItem().data;
					onSelectDetails(s.id);
				};
				for (var id in plugins) {
					var plugin = plugins[id];
					if (!plugin.details) continue;
					
					if (!firstPlugin) firstPlugin = id;

					var title = plugin.details.title ? plugin.details.title : (plugin.details["title-key"] ? mollify.ui.texts.get(plugin.details["title-key"]) : id);
					var selector = mollify.dom.template("mollify-tmpl-main-itemcontext-details-selector", {id: id, title:title, data: plugin}).appendTo($selectors).click(selectorClick);
				}

				if (firstPlugin) onSelectDetails(firstPlugin);
			}
			
			mollify.ui.controls.dropdown({
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
	};
	
	/**/
		
	t.ui.assign = function(h, id, c) {
		if (!h || !id || !c) return;
		if (!h.controls) h.controls = {};
		h.controls[id] = c;
	};
		
	t.ui.process = function($e, ids, handler) {
		$.each(ids, function(i, k) {
			if (t.ui.handlers[k]) t.ui.handlers[k]($e, handler);
		});
	};
				
	t.ui.handlers = {
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
	};
		
	t.ui.window = {
		open : function(url) {
			window.open(url);
		}
	};
	
	/* CONTROLS */
		
	t.ui.controls = {
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
			if (!content || content.length === 0) return;

			var html = content.html();
			content.remove();

			var $tip = false;
			var rendered = false;
			var api = {
				hide: function() {
					$e.popover('hide');
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
				content: html
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
			if ($e.length === 0 || !o.columns) return false;
			
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
					
					if (window.isArray(item)) {
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
			if (!$e || $e.length === 0) return false;

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
				if (!s || s.length === 0) return false;
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
					
					if (window.isArray(item)) {
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
	};
	
	/* DIALOGS */
	
	mollify.ui.dialogs = {};
	var dh = mollify.ui.dialogs;
			
	dh._dialogDefaults = {
		title: "Mollify"
	};
			
	dh.info = function(spec) {
		dh.custom({
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
	
	dh.error = function(spec) {
		dh.custom({
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
	
	dh.confirmation = function(spec) {
		dh.custom({
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
	
	dh.input = function(spec) {
		var $input = false;
		dh.custom({
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
				if (spec.defaultValue) $input.val(spec.defaultValue);
				$input.focus();
			}
		});
	};
	
	dh.wait = function(spec) {
		var $trg = (spec && spec.target) ? $("#"+spec.target) : $("body");
		var w = mollify.dom.template("mollify-tmpl-wait", $.extend(spec, dh._dialogDefaults)).appendTo($trg).show();
		return {
			close: function() {
				w.remove();
			}
		};
	};
	
	dh.notification = function(spec) {
		var $trg = (spec && spec.target) ? $("#"+spec.target) : $("#mollify-notification-container");
		if ($trg.length === 0) $trg = $("body");
		var notification = mollify.dom.template("mollify-tmpl-notification", $.extend(spec, dh._dialogDefaults)).hide().appendTo($trg).fadeIn(300);
		setTimeout(function() {
			notification.fadeOut(300);
			if (spec["on-finish"]) spec["on-finish"]();
		}, spec.time | 3000);
	};
	
	dh.custom = function(spec) {
		var center = function($d) {
			$d.css("margin-left", -$d.outerWidth()/2);
			$d.css("margin-top", -$d.outerHeight()/2);
			$d.css("top", "50%");
			$d.css("left", "50%");
		};
		var s = spec;
		if (s['title-key']) s.title = mollify.ui.texts.get(s['title-key']);
		
		var $dlg = $("#mollify-tmpl-dialog-custom").tmpl($.extend(dh._dialogDefaults, s), {
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
				if (b.title) return b.title;
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
	
	dh.folderSelector = function(spec) {
		var selectedFolder = false;
		var content = $("#mollify-tmpl-dialog-folderselector").tmpl({message: spec.message});
		var $selector = false;
		var loaded = {};
		
		var load = function($e, parent) {
			if (loaded[parent ? parent.id : "root"]) return;
			
			$selector.addClass("loading");
			mollify.filesystem.folders(parent, function(l) {
				$selector.removeClass("loading");
				loaded[parent ? parent.id : "root"] = true;
				
				if (!l || l.length === 0) {
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
				var c = $("#mollify-tmpl-dialog-folderselector-folder").tmpl(l, {cls:(level === 0 ? 'root' : ''),levels:levels});
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
		
		dh.custom({
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
	
	/* DRAG&DROP */
	
	mollify.MollifyHTML5DragAndDrop = function() {
		var t = this;
		t.dragObj = false;
		t.dragEl = false;
		t.dragListener = false;
		
		var endDrag = function(e) {
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
					endDrag(e);
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
					endDrag(e);
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


}(window.jQuery, window.mollify);